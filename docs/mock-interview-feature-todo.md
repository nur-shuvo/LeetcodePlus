# Mock Interview Feature — Resume Notes

Status as of 2026-07-21: fully implemented, building, and **wired to a real Firebase project** —
matching is done **on-device** with no Cloud Functions backend and no Calendar/Meet API
integration, which removed the multi-week Google OAuth sensitive-scope verification that used to
block the feature. Console setup (see "Firebase project is live" below) is done. Single-account
on-device flow, booking limits, and the UX pass below (live match detection/modal, status chips,
countdown/join-gating, account management) are all built and verified on a real device.

## UI polish: icons, action-intent color, copy (2026-07-21)

Added leading icons to every action button/toggle across 4 screens (profile setup,
slot picker, session detail, feedback), replaced the feedback screen's Unicode star
glyphs (★/☆) with real `Icon` composables, and applied two action-intent color
changes: "Join Video Call" now uses the tertiary color (matching the existing
`JOINABLE`-phase status chip), and the "peer didn't show up" checkbox tints toward
error when checked. One copy fix: "Save profile" → "Save & Continue" (it also
navigates forward). Full design: `docs/superpowers/specs/2026-07-21-mock-interview-ui-polish-design.md`.

## In-app "how it works" explanation (2026-07-21)

Added a static "how it works" explanation (4-icon summary + 6 bullets, no image asset - built
entirely from existing Material icons) in `InterviewHowItWorks.kt`, surfaced two ways: an
expandable inline section on the profile setup screen (before sign-in), and an info-icon dialog
in the Mock Interviews session list's top bar (after sign-in, for on-demand reference). Full
design: `docs/superpowers/specs/2026-07-21-mock-interview-how-it-works-design.md`.

## Calendar-style slot picker (2026-07-21)

The slot picker (`InterviewSlotPickerScreen.kt`) was redesigned from a flat scrollable list of
~42 rows (14 days x 3 fixed times/day) into a month calendar grid for picking a date, with a
short tap-to-book list of that day's times below it — the flat list was hard to scan. New pure
date/grid math lives in `InterviewSlotCalendar.kt` (unit tested); the grid itself is
`InterviewCalendarGrid.kt`. Full design:
`docs/superpowers/specs/2026-07-21-mock-interview-calendar-picker-design.md`.

## UX additions (2026-07-21)

- **Live match detection**: `InterviewMatchWatcherViewModel`
  (`ui/screens/interview/watcher/`), hosted once at the app root (`MainActivity`, alongside the
  nav graph, not inside it) so it's active regardless of which screen is on top. Reacts to the
  same real-time `getMySessions()` Firestore listener already used elsewhere (no polling) via
  `googleAuthRepository.currentUser.flatMapLatest { ... }` so it stays correct across sign-in/out.
  Only actually collects while the app is at least `STARTED` (`repeatOnLifecycle` in
  `InterviewMatchModal`), matching "while the app is foreground". Fires the matched notification
  immediately (deduped via the same `NotificationDataStore` keys the background worker uses, so
  they never double-fire) and surfaces an `AlertDialog` with peer details for the most recent
  still-upcoming match.
- **Status chips + "Ended" tag**: `InterviewSessionPhase` (`ui/screens/interview/`) is a shared
  enum + `phase()` extension on both `InterviewSession` and `SlotBooking`, used by both the
  session list and detail screens so phase logic isn't duplicated. Phases: `WAITING`,
  `MATCHED_UPCOMING`, `JOINABLE`, `ENDED`/`EXPIRED`, `CANCELLED`.
- **Countdown + join gating**: `rememberNowTicker()` (1s tick) + `formatCountdown()` in the same
  file. "Join Video Call" is only shown once `JOINABLE` (within `JOIN_WINDOW_MILLIS` = 5 min of
  start); before that, a live "Join opens in HH:MM:SS" countdown is shown instead.
- **Account management**: session list's `TopAppBar` shows the signed-in Google account's avatar
  (via Coil `AsyncImage` on `photoUrl`); tapping it opens a menu with the signed-in email and
  Sign out. Sign-out routes back through `InterviewProfileSetup` (which shows "Sign in with
  Google" once signed out) via the same `popUpTo(Main)` back-stack-cleanup pattern used elsewhere.

## How Matching Works

### User point of view

1. **Sign in with Google, then set up a profile** — tapping "Practice a mock interview" on Home
   always lands on the profile setup screen first, which asks for Google sign-in (this is
   separate from the LeetCode WebView login used elsewhere in the app) before letting you pick
   role(s) to practice (Android, Backend, Frontend, ML, System Design) and your LeetCode handle.
   Timezone is captured automatically from the device. Returning users who already signed in and
   saved a profile skip straight past this screen to their session list.
2. **Browse slots for a role** — a fixed catalog: 3 times a day (8am / 1pm / 7pm UTC, 1 hour
   each), for the next 14 days. Nothing to configure; every user sees the same slot times for a
   given role, which is what makes matching possible without a server.
3. **Book a slot.** Two things can happen:
   - **You're the second person to book that exact slot + role** → you're matched immediately.
     You land straight on a "Matched" session showing your peer's name/LeetCode handle and a
     **Join Video Call** button.
   - **You're the first person to book it** → your session list shows "Waiting for a peer...".
     If someone else books the same slot+role before it starts, you're both matched and get a
     notification within ~15 minutes (the app checks periodically in the background). If nobody
     else books it before the slot's start time passes, it flips to "No peer found - slot
     expired" — no penalty, just book another slot.
   - **Booking rules**: tapping "Book" on a slot you've already booked is a harmless no-op (you
     can't double-book the same slot). You can have at most **3 upcoming** (not-yet-ended)
     bookings at once, across all roles combined — trying to book a 4th shows "You already have 3
     upcoming mock interviews - finish or wait for one before booking another."
4. **Join the call** — tapping Join Video Call opens a Jitsi Meet room in the browser/Jitsi app.
   No sign-up needed on Jitsi's side; the room name is unique to your session.
5. **After the session ends** (past its scheduled end time), you get a feedback prompt and can
   rate your peer (communication, problem-solving, would-match-again, no-show flag).

### Technical view

**No backend.** There is no server component at all — everything below runs on the booking
device via the Firestore client SDK. Two Firestore collections drive it:
- `slotBookings/{slotDocId}/candidates/{uid}` — one doc per user per slot attempt.
  `slotDocId` is derived deterministically from the slot's start time + role
  (`InterviewSlotCatalog.slotDocId()`), so two peers booking "the same slot" always write into
  the same subcollection without ever having to look each other up by name.
- `sessions/{sessionId}` — created only once two candidates match. `sessionId` reuses the
  `slotDocId`, so it never needs to be coordinated or randomly generated.

**Booking flow** (`InterviewSlotRepositoryImpl.bookSlot()`):
1. A plain (non-transactional) query reads the `candidates` subcollection to find *whether*
   someone else might already be waiting on this slot. This is just a hint, not authoritative.
2. A Firestore transaction then:
   - Re-reads the caller's own candidate doc and (if one was found) the peer's candidate doc via
     `tx.get(DocumentReference)` — these are the authoritative reads. (The Android SDK's
     `Transaction.get()` only accepts a `DocumentReference`, not a `Query`, which is why step 1
     has to happen outside the transaction — you can't discover an unknown peer's doc ID from
     inside one.) If the caller's own doc already exists, the transaction is a no-op (idempotent
     retry-safe: tapping "Book" twice on a slot you already booked does nothing the second time).
   - Runs `decideMatch(candidates, newCandidateId)` — a small pure function
     (`InterviewMatchDecision.kt`, unit tested) ported from what used to be a Cloud Function's
     `matchDecision.ts` — against `[peer candidate, own booking]` to decide whether to match.
   - Writes the caller's own candidate doc **exactly once**, with whichever status the match
     decision produced (`waiting` if no match, `matched` if one was found) — never `set()` as
     `waiting` followed by a separate `update()` to `matched`. If matched, also flips the peer's
     doc to `status = matched` and creates the `sessions` doc with `participantUids`, role, and
     time range, all inside the same transaction. See "Fixed: PERMISSION_DENIED crash on match"
     below for why writing to the same document twice in one transaction doesn't work.
3. **Race safety**: because the transaction re-reads the peer's doc live at commit time (not the
   stale copy from step 1's query), Firestore's optimistic concurrency control retries the whole
   transaction if that doc changed in between — so two near-simultaneous bookings can't both
   match against a peer that a third party already matched with, and you can't get two
   independent sessions out of what should be one pair.
4. **Booking limits**: before writing a genuinely new booking (not a re-tap on one you already
   have), `bookSlot()` runs `countUpcomingBookings()` — a `collectionGroup(candidates)` query
   counting the caller's own docs (any role, any status) whose `endEpochMillis` hasn't passed —
   and throws `TooManyActiveBookingsException` if it's already at `MAX_ACTIVE_BOOKINGS_PER_USER`
   (3). This is **client-side only, not enforced by `firestore.rules`** — Firestore Security
   Rules can't count/aggregate across a query, only inspect the document(s) being written, so a
   modified client could in principle bypass this cap. Same accepted trust trade-off as the
   matching logic below.

**Nothing is written back for "waiting" or "expired".** A `SlotBooking` with `status = waiting`
is shown as "waiting for a peer" or "no peer found / expired" purely by comparing its
`endEpochMillis` to `System.currentTimeMillis()` on-device (`InterviewSessionListViewModel`) —
there's no scheduled job flipping a status field, since there's no server to run one.

**The meeting link isn't stored either.** `InterviewSession.meetingUrl` is computed as
`"https://meet.jit.si/lcplus-$sessionId"` in Kotlin — since `sessionId` is the same deterministic
value on both peers' devices, they land on the identical Jitsi room with zero Firestore writes
and no Calendar/Meet API call.

**Trust boundary:** with no Admin-SDK backend as the sole arbiter, `firestore.rules` has to allow
any signed-in user to flip a `waiting` candidate to `matched` (status-only diff) and to create a
`sessions` doc naming themselves as a participant. This is a deliberate, accepted trade-off (see
Known scope simplifications below) — worst case is a bogus match, not data corruption.

**Notifications:** `InterviewSessionWorker` (WorkManager, periodic, ~15 min) polls the signed-in
user's own sessions and fires local notifications for matched / starting-soon / feedback-due
events via the existing `NotificationHandler` functions. Match/reminder notifications are
deduped through a small "already notified" set in `NotificationDataStore`; feedback prompts are
not deduped (see below).

## What's already built

- **Matching, meeting link, notifications**: all client-side, no backend — see "How Matching
  Works" above for the full user-facing and technical explanation.
- **Firestore config**: `firestore.rules`, `firestore.indexes.json`, `firebase.json`,
  `.firebaserc` (real project id, deployed - see "Firebase project is live" below).
- **Android Gradle**: Firebase (Auth + Firestore only) and Google Sign-In dependencies in
  `gradle/libs.versions.toml` / `app/build.gradle.kts`. The `google-services` plugin only
  applies once `app/google-services.json` exists, so the rest of the app keeps building until
  that file is added.
- **Android data layer**: `data/model/interview/`, `data/repository/interview/` (profile, slot,
  session, feedback, Google auth repositories), `data/firebase/di/FirebaseModule.kt`, wired into
  `data/repository/di/RepositoryModule.kt`.
- **Android UI**: 5 screens under `ui/screens/interview/` (profile setup, slot picker, session
  list, session detail, feedback form), new nav routes in `ui/navigation/`, entry point card on
  the Home screen. The session list also surfaces bookings still waiting for a peer, or that
  timed out with none found (no server job to do that anymore, so it's derived client-side).
- **Tests**: `InterviewSlotCatalogTest.kt`, `InterviewMatchDecisionTest.kt` (both Android/JVM,
  passing).

Verify anytime with:
```
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest
```

## Firebase project is live (2026-07-20)

Console setup is done. Project: **`leetcodeplus-mock-interview`**
(https://console.firebase.google.com/project/leetcodeplus-mock-interview/overview), Spark
(free) plan — no Blaze plan needed since there's no Cloud Functions/Calendar API.

What's in place:
- Firebase project + Android app registered (package `com.byteutility.dev.leetcode.plus`), debug
  SHA-1/SHA-256 fingerprints uploaded.
  - Get fresh fingerprints anytime with `./gradlew signingReport` (debug keystore is
    machine-local, so a new dev machine needs its own SHA-1/256 added via
    `npx firebase-tools apps:android:sha:create <appId> <sha>` or the console).
  - **Release builds need their own SHA-1/256 added** the same way once a release keystore
    exists — Google Sign-In will fail on a release APK until that's done.
- Authentication → Google sign-in provider enabled.
- Firestore database created (`nam5`/us-central).
- `firestore.rules` / `firestore.indexes.json` deployed (`firebase deploy --only
  firestore:rules,firestore:indexes --project leetcodeplus-mock-interview`) — re-run that same
  command any time the rules/indexes files change.
- `app/google-services.json` downloaded and in place (gitignored, machine-local — anyone else
  building this needs their own copy, pulled with
  `npx firebase-tools apps:sdkconfig ANDROID <appId> --project leetcodeplus-mock-interview`).
- `.firebaserc` points at the real project id.
- `./gradlew :app:assembleDebug` verified building successfully with real Firebase wired in
  (confirms `google-services` plugin picked up the config and generated
  `default_web_client_id` correctly for Google Sign-In).

No CLI-scriptable step remains. Everything above was done via `npx firebase-tools` (no global
install needed/possible in this environment — global `npm install -g` hit an `EACCES` permission
error, `npx` works fine without it).

## Known scope simplifications (flagged, not fixed)

- **Matching trust boundary is client-side now.** `firestore.rules` allows any signed-in user to
  flip a `waiting` candidate doc to `matched` (status-only diff) and to create a `sessions` doc
  naming themselves as a participant. There's no Admin-SDK backend to be the sole arbiter
  anymore. Worst case is a bogus match, not data corruption (a modified client still can't forge
  a session with 2 arbitrary uids it doesn't control the identity of via its own writes) - this
  mirrors the already-accepted risk on the client-generated slot catalog below. Worth revisiting
  if this feature gets real usage.
- **Slot catalog isn't server-validated.** `InterviewSlotCatalog.kt` generates the fixed slot
  list client-side; `firestore.rules` doesn't check that a booked slot's timestamp is a
  legitimate catalog entry. A modified client could in principle book an arbitrary time. Low
  risk (just a garbage booking) but worth tightening before wider rollout.
- **The 3-upcoming-bookings cap isn't server-enforced either**, for the same structural reason:
  Firestore rules can't count/aggregate across a query. A modified client could bypass
  `TooManyActiveBookingsException` and over-book. Low risk (spammy bookings, not data corruption).
- **Feedback-prompt notifications can repeat.** Unlike the match/reminder notifications (which
  are deduped via `NotificationDataStore`), the feedback prompt has no dedupe - it'll keep firing
  every ~15 min after a session ends until feedback is submitted. Self-limiting, but mildly
  annoying; add a "prompted" marker if it becomes a problem.
- **No in-app problem picker** for interviewers — explicitly out of scope per the original
  design (peers coordinate the question themselves).
- **RevenueCat/Pro-tier gating**: intentionally not applied to this feature.

## Fixed: sign-in was unreachable, booking failed silently (2026-07-20)

Found during first device testing: the Home screen's "Practice a mock interview" card navigated
straight to `InterviewSessionList`, never through `InterviewProfileSetup` — so a first-time user
never saw the "Sign in with Google" button at all. Worse, `InterviewSlotRepositoryImpl.bookSlot()`
silently no-op'd when there was no signed-in user (`?: return`), so tapping "Book" on a slot
looked like it succeeded (no error shown) and navigated to the session list, which was then
empty because nothing had actually been written to Firestore.

Fixed:
- `MainScreen.kt`: the entry card now always routes to `InterviewProfileSetup` first.
- `InterviewProfileSetupScreen.kt`: auto-forwards straight to the session list if the user is
  already signed in with a saved profile, so returning users aren't stuck re-entering roles every
  time; the sign-in prompt copy now explains *why* Google sign-in is needed.
- `InterviewSlotRepositoryImpl.bookSlot()` now throws `NotSignedInException` instead of silently
  returning; `InterviewSlotPickerViewModel`/`Screen` surface a specific "Sign in with Google
  first" message instead of a generic booking error.
- Home entry card copy and the "Confirmed" meeting-link references were stale from before the
  Calendar API removal — updated to match the Jitsi-based flow.

## Fixed: PERMISSION_DENIED crash on match (2026-07-20)

First on-device test crashed with `FirebaseFirestoreException: PERMISSION_DENIED` inside the
booking transaction. Root-caused with the Firestore emulator (`@firebase/rules-unit-testing`) by
reproducing the exact transaction shape from `InterviewSlotRepositoryImpl.bookSlot()` in
isolation — confirmed empirically, not guessed:

**Cause**: the transaction wrote the caller's own candidate doc *twice* — `tx.set(ownRef, {status:
"waiting", ...})` immediately followed by `tx.update(ownRef, {status: "matched"})` inside
`applyMatch()`, when a match was found on the same booking. Firestore evaluates a transaction's
security rules against each document's *final* coalesced value, not each individual API call —
so the `create` rule (which only allows `status == "waiting"`) saw the final value `"matched"`
and denied the whole transaction, taking the peer's `update` down with it (all writes in a
transaction are evaluated and committed together).

**Fix**: `bookSlot()` now writes the own candidate doc with exactly one `tx.set()` call, choosing
`waiting` or `matched` up front based on the match decision — never a `set()` + `update()` pair on
the same doc. `firestore.rules`'s candidates `create` rule was loosened to accept either status
on create, since a create can now legitimately be the final "matched" state. Also added: a guard
so re-booking an already-booked slot is a safe no-op instead of hitting the same broken write
pattern, and a `.catch` on `InterviewSessionListViewModel`'s combined flow so a future Firestore
listener error surfaces as an empty list instead of an uncaught exception that crashes the app
(this is what turned the transaction failure into a hard crash rather than a visible error toast).
Verified fixed against the Firestore emulator (clean match transaction now succeeds) and
redeployed to the live project with `firebase deploy --only firestore:rules`.

## Fixed: pending bookings invisible, weird back-press (2026-07-20, on-device)

Ran the app on a real device (adb + screenshots) to check two things reported after booking a
slot: the session list still said "No mock interviews yet", and back-press behaved oddly.

**Cause 1 — `getMyBookings()` denied.** `logcat` showed
`Listen for Query(target=Query(collectionGroup=candidates where uid==...)) failed:
PERMISSION_DENIED` on every app start. The existing `match
/slotBookings/{slotDocId}/candidates/{uid}` rule authorizes direct/subcollection access but does
**not** automatically extend to `collectionGroup()` queries — that needs its own recursive
wildcard rule. Confirmed via the emulator: the same query failed with "No matching allow
statements" until adding
```
match /{path=**}/candidates/{uid} {
  allow read: if isSignedIn();
}
```
alongside the existing rule (kept, since create/update still need the specific-path version).
Redeployed and verified on-device: "Waiting for a peer..." now shows correctly. Note this means
the *previous* PERMISSION_DENIED fix's `.catch` safety net was working as designed the whole
time — it just meant this second bug failed silently instead of crashing, which is why it looked
like "no information about the waiting state" rather than an obvious error.

**Cause 2 — back-stack duplication.** `LeetCodePlusNavigation.kt`'s interview nav actions only
set `launchSingleTop = true`, which dedupes consecutive instances of the *same* destination but
does nothing when navigating between *different* screens. The flow `ProfileSetup → SessionList →
SlotPicker → SessionList` (after booking) pushed a **second** `SessionList` on top instead of
replacing the first, leaving the back stack as `Main → SessionList(1) → SlotPicker →
SessionList(2)`. Pressing back cycled through the slot picker and a stale session list instead of
going to Home.

**Fix**: `navigateToInterviewSessionList` now does `popUpTo(Main) { inclusive = false }` before
pushing, so the back stack is always `Main → InterviewSessionList` regardless of which screen it
was reached from (mirrors the existing `popUpTo` pattern already used by
`navigateToMainScreen`/`navigateToLogin` in the same file). Verified on-device with adb: back
from the session list now goes straight to Home, and a second back exits the app cleanly.

## Fixed: re-signing in wiped saved roles (2026-07-21, on-device)

Found while testing the new account/logout UI: signing out and back into the *same* account
that already had a saved profile landed on an *empty* role picker instead of auto-forwarding to
the session list, even though nothing else had changed.

**Cause**: `InterviewProfileViewModel.signIn()` built the profile to save using
`profile.value?.roles ?: emptyList()`, where `profile` is this ViewModel's own
`getMyProfile()`-backed `StateFlow` — subscribed *before* sign-in completes, when there's no user
yet, so `profile.value` was always `null` at that point and the write went out with `roles = []`.
`saveProfile()` uses `SetOptions.merge()`, but since `"roles"` is explicitly present in the write
(even as an empty list), merge still overwrites it — wiping any previously-saved roles on *every*
sign-in, not just the first.

A second, related bug made this worse: `InterviewProfileRepositoryImpl.getMyProfile()` read
`firebaseAuth.currentUser?.uid` once at flow-*creation* time, not reactively - so even after
fixing the write, a ViewModel instance created before sign-in would never see the (correctly
saved) profile afterward, leaving the auto-forward stuck forever on that screen instance.

**Fix**:
- `getMyProfile()` now reacts to `FirebaseAuth.AuthStateListener` via `flatMapLatest`, matching
  the pattern already used in the new match-watcher ViewModel - re-subscribes to the real
  document as soon as `currentUser` changes instead of freezing on the pre-sign-in snapshot.
- `signIn()` now does a fresh one-shot `getProfile(user.uid).first()` right after sign-in
  succeeds and preserves its `roles`/`leetcodeHandle` instead of trusting the stale `profile`
  StateFlow.

Verified on-device: saved roles now survive sign-out → sign-in, and returning users correctly
auto-forward straight to their session list again.

## Fixed: already-booked slots still showed as bookable (2026-07-21, on-device)

`InterviewSlotPickerViewModel.slots` returned `interviewSlotRepository.getAvailableSlots(role)`
unfiltered - the full 14-day catalog for the role, with no check against what the user had
already booked. Re-tapping "Book" on one of those was already a harmless no-op (from an earlier
fix), but the slot stayed listed as if available, which was confusing.

**Fix**: `slots` now combines the role's catalog with `interviewSlotRepository.getMyBookings()`
and filters out any slot whose `(slotId, role)` matches an existing booking (`waiting` or
`matched` - `InterviewSlotCatalog.generateUpcomingSlots()` already excludes past slots, so any
booking that still shows up in the catalog range can't be expired). Added an empty-state message
("You've booked all available `<role>` slots.") for the case where every slot for a role is
already booked. Verified on-device: a user with three existing bookings (waiting/matched/expired
on 21 Jul) now sees the picker start at 22 Jul - the two still-upcoming booked slots are excluded,
the expired one was already excluded by the catalog's own past-slot filter.

## Next steps when resuming

1. Single-account on-device flow (sign-in, book, pending state, back-press, sign-out/re-login,
   account menu, status chips, countdown/join-gating) is now verified working - see the "Fixed"
   sections and "UX additions" above.
2. Manual two-account end-to-end test still needed: both sign in, set the same role, book the
   same slot, confirm one device's booking transaction matches both, the match-found modal and
   notification fire promptly on the waiting peer's device while foregrounded, the Jitsi link
   works, and background notifications still fire within ~15 min if backgrounded instead.
3. Decide whether to address the client-side trust-boundary / slot-catalog validation gaps
   before wider testing.
4. Before a release build, add the release keystore's SHA-1/SHA-256 to the Firebase Android app
   (see "Firebase project is live" above) — Google Sign-In will otherwise fail on release builds.
