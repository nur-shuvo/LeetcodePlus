# Mock Interview "How It Works" Section

## Context

The mock-interview feature (peer matching for practice interviews) has grown several moving
parts — booking, matching, notifications, video calls, feedback — but nothing in the app itself
explains the flow to a first-time user beyond a two-sentence blurb on the profile setup screen.
The user asked for a "how it works" section, in bullet points, with an image illustration, so
someone opening this feature for the first time understands what it does before committing to
sign in, and can refer back to it later.

## Goal

Add one reusable "how it works" content block (bullets + a small icon-based visual — no new
image asset, since none can be generated for this app and none was supplied) and surface it in
two places: expanded inline on the profile setup screen (the first screen a user sees), and via
an on-demand info icon in the Mock Interviews session list (for reference after they're already
using the feature).

## Content

**Bullets** (adapted from the feature's existing user-facing description, condensed for in-app
display):

- Pick the role(s) you want to practice and sign in with Google
- Book any open time slot — 3 times a day, up to 14 days ahead
- Matched instantly if someone's already waiting on that slot
- Otherwise you wait, and get notified the moment a peer books it too
- Join your video call once it's within 5 minutes of starting
- Leave quick feedback for your peer afterward

**Icon visual**: a horizontal row of 4 steps, each an icon over a short label, summarizing the
above at a glance:

1. `Icons.Filled.Person` — "Set up profile"
2. `Icons.Filled.CalendarToday` — "Book a slot"
3. `Icons.Filled.People` — "Get matched"
4. `Icons.Filled.Videocam` — "Join & practice"

All four icons are confirmed present in this project's dependencies: `Person` ships in the base
`material-icons-core` artifact already used everywhere in the app; `CalendarToday`, `People`, and
`Videocam` are confirmed present in the `material-icons-extended` artifact already declared in
`gradle/libs.versions.toml` (verified directly against the resolved dependency jars — not
assumed).

## Component design

One new file, `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/InterviewHowItWorks.kt`
(same shared package as the existing `InterviewSessionPhase.kt`), containing:

- `@Composable fun InterviewHowItWorksContent(modifier: Modifier = Modifier)` — the bullets plus
  the icon-step row. No parameters beyond `modifier`; content is static. This is the single
  source of truth for the copy — neither consumer duplicates the text.
- `@Composable fun InterviewHowItWorksDialog(onDismiss: () -> Unit)` — wraps
  `InterviewHowItWorksContent` in an `AlertDialog` (title "How Mock Interviews Work", a single
  "Got it" dismiss button), for the on-demand entry point.

### Consumer 1: `InterviewProfileSetupScreen.kt` (inline, expandable)

Mirrors the existing "How to find your username?" pattern in `UserLoginScreen.kt` exactly: a
`TextButton` toggling a `showHowItWorks` boolean, wrapping `InterviewHowItWorksContent()` in an
`AnimatedVisibility`. Placed after the existing intro text, before the "Sign in with Google"
button — visible whether or not the user is signed in yet, so it's usable as a decision aid
before committing to sign in.

### Consumer 2: `InterviewSessionListScreen.kt` (on-demand dialog)

A new `IconButton` (info icon: `Icons.Filled.Info` — already available in `material-icons-core`)
added to the `TopAppBar`'s `actions`, placed before the existing account-avatar `IconButton`.
Tapping it sets a local `showHowItWorks` boolean to true; `InterviewHowItWorksDialog` is rendered
conditionally based on that flag, with `onDismiss` setting it back to false. No new navigation
route, no back-stack entry — this is reference content, not a destination.

## Out of scope

- No new image/illustration asset (none can be generated here, none was supplied) — the icon-row
  visual is the illustration.
- No change to the actual matching/booking logic, ViewModels, or any other screen's behavior.
- No localization of the new copy (matches the rest of the app, which is English-only today).

## Testing

Compose UI content with no parameters and no logic — matches this codebase's existing convention
of no dedicated tests for presentational composables (e.g. `InterviewSessionPhase.kt`'s status
chips, `InterviewCalendarGrid.kt`). Verification is compile (`compileDebugKotlin`) plus an
on-device manual check: the inline section expands/collapses on the profile screen, and the info
icon opens/dismisses the dialog on the session list, both showing the same content.
