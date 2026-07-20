# Mock Interview Feature — Resume Notes

Status as of 2026-07-20: full code scaffold is implemented and compiling/passing tests. The
feature is **not runnable yet** — it's blocked on manual Firebase/Google Cloud console setup
(see below). Full design/plan: `~/.claude/plans/i-will-introduce-a-async-music.md`.

## What's already built

- **Cloud Functions** (`functions/`, Node/TS): Firestore-triggered matching (`src/matching/`),
  Calendar/Meet event creation (`src/calendar/`), scheduled reminder/expiry/feedback jobs
  (`src/scheduled/`), FCM helper (`src/notify/`). Matching decision logic is a pure function
  (`matchDecision.ts`) with passing Jest tests.
- **Firestore config**: `firestore.rules`, `firestore.indexes.json`, `firebase.json`,
  `.firebaserc` (project id still a placeholder — see step 1 below).
- **Android Gradle**: Firebase + Google Sign-In dependencies added to
  `gradle/libs.versions.toml` / `app/build.gradle.kts`. The `google-services` plugin only
  applies once `app/google-services.json` exists (see `app/build.gradle.kts`), so the rest of
  the app keeps building until that file is added.
- **Android data layer**: `data/model/interview/`, `data/repository/interview/` (profile, slot,
  session, feedback, Google auth repositories), `data/firebase/di/FirebaseModule.kt`, wired into
  `data/repository/di/RepositoryModule.kt`.
- **Android UI**: 5 screens under `ui/screens/interview/` (profile setup, slot picker, session
  list, session detail, feedback form), new nav routes in `ui/navigation/`, entry point card on
  the Home screen.
- **Push notifications**: `service/InterviewFcmService.kt`, new functions in
  `utils/NotificationHandler.kt`, manifest service registration, `MainActivity` deep-link
  handling for `interviewSessionId`.
- **Tests**: `app/src/test/.../InterviewSlotCatalogTest.kt` (Android, passing),
  `functions/src/matching/matchDecision.test.ts` (Jest, passing).

Verify anytime with:
```
./gradlew :app:compileDebugKotlin :app:testDebugUnitTest
cd functions && npx tsc --noEmit && npx jest
```

## What's blocked on manual console setup

Nothing further can be automated here without you authenticating a CLI locally (see below) —
these steps need your Google account.

1. **Firebase project**: create it, add an Android app (package
   `com.byteutility.dev.leetcode.plus`), register SHA-1/SHA-256 fingerprints, download
   `google-services.json` → place at `app/google-services.json` (gitignored, never commit).
   - Debug fingerprints (get again anytime with `./gradlew signingReport`):
     - SHA-1: `42:31:25:FD:C9:50:56:A7:14:83:C4:51:72:50:82:60:58:E8:A5:AD`
     - SHA-256: `0F:2C:CA:45:80:EE:4E:80:11:58:D3:9F:4B:54:5A:AD:06:24:7F:EA:C7:8E:CA:51:5E:71:AE:93:2F:CD:55:CE`
   - Enable in Firebase console: Firestore, Authentication → Google provider, Cloud Messaging,
     Functions (requires upgrading to the Blaze plan).
2. **Google Cloud Console** (same project): enable the **Google Calendar API**; configure the
   OAuth consent screen with scope `.../auth/calendar.events`; create two OAuth client IDs
   (Android — package + SHA-1; Web application — used by Cloud Functions server-side).
3. **Submit the OAuth consent screen for verification** (the `calendar.events` scope is
   sensitive). This is the long pole — days to weeks. Start it as early as possible.
4. **Set Cloud Functions secrets** from the Web client created in step 2:
   ```
   firebase functions:secrets:set GOOGLE_OAUTH_CLIENT_ID
   firebase functions:secrets:set GOOGLE_OAUTH_CLIENT_SECRET
   ```
5. **Deploy**: `firebase deploy --only functions,firestore:rules` (update `.firebaserc`'s
   placeholder project id first).
6. Once `google-services.json` is in place, rebuild and sanity-check the app compiles/runs with
   real Firebase wired in.

If you'd rather have this driven by CLI instead of clicking through consoles, `firebase` and
`gcloud` CLIs aren't installed on this machine yet — say so next session and they can be
installed, with you doing only the interactive `login` step yourself.

## Known scope simplifications (flagged, not fixed)

- **Slot catalog isn't server-validated.** `InterviewSlotCatalog.kt` generates the fixed slot
  list client-side; `firestore.rules` doesn't check that a booked slot's timestamp is a
  legitimate catalog entry. A modified client could in principle book an arbitrary time. Low
  risk (just a garbage booking, not a security hole beyond that) but worth tightening before
  wider rollout — e.g. move slot generation server-side into a Cloud Function-maintained
  `slotCatalog` collection and validate against it in the rules.
- **"Waiting for a peer" bookings aren't shown in-app.** `InterviewSessionListScreen` only
  reads the `sessions` collection (already-matched), not `slotBookings/*/candidates` (booked but
  not yet matched). A user who books a slot with no match yet currently has no in-app visibility
  into that state until/unless they get matched or the booking expires.
- **No in-app problem picker** for interviewers — explicitly out of scope per the original
  design (peers coordinate the question themselves).
- **RevenueCat/Pro-tier gating**: intentionally not applied to this feature. Note a stale memory
  had claimed RevenueCat was integrated in this codebase; it isn't (confirmed by search) — see
  the `RevenueCat Integration` memory entry for that correction.

## Next steps when resuming

1. Confirm the console setup above is done and `app/google-services.json` is real.
2. `./gradlew :app:assembleDebug` and install on a device/emulator with Play Services.
3. Manual two-account end-to-end test: both sign in, set the same role, book the same slot,
   confirm the match/Meet-link/notification/feedback flow works (see the plan file's
   verification section for the detailed script).
4. Decide whether to address the slot-catalog validation gap before wider testing.
