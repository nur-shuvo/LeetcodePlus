# Mock Interview UI Polish — Icons, Action Colors, Copy

## Context

The user reviewed the mock-interview feature on-device and found it "not professional" — almost
every button and interactive row is plain text with no icon, and the feedback screen's star
rating renders as raw Unicode characters (★/☆) instead of real icon components. The ask was to
review the whole feature's UI and add icons, color-code actions by intent, and tighten up any
weak copy.

## Scope

Four files, all additive polish — no new screens, no layout restructuring, no new dependencies
(every icon used below is already confirmed present in this project's existing
`material-icons-core`/`material-icons-extended` dependencies). Deliberately **not** in scope:
role-based color coding (considered and declined in favor of action-intent coloring only),
restructuring the calendar grid or card layouts, or dark-theme work (a separate, app-wide concern
unrelated to this feature).

## Icons + action-intent color

Buttons/toggles get a leading icon and, where the action has a distinct "kind," a color to match:
primary actions stay primary (the app's default `Button` color — no change needed there), the
video-call join action gets the **tertiary** color to visually tie it to the existing "Starting
soon" status chip (`statusChipColors()` in `InterviewSessionPhase.kt` already uses
`tertiaryContainer` for that phase), and the "peer didn't show up" checkbox tints toward
**error** when checked, since it's flagging a problem.

| File | Element | Icon | Color |
|---|---|---|---|
| `InterviewProfileSetupScreen.kt` | "Sign in with Google" `Button` | `Icons.Filled.Login` | primary (default) |
| `InterviewProfileSetupScreen.kt` | "How does this work?"/"Hide details" `TextButton` | `Icons.Filled.ExpandMore` (collapsed) / `Icons.Filled.ExpandLess` (expanded) as a trailing icon | text (default) |
| `InterviewProfileSetupScreen.kt` | "Save & Continue" `Button` (renamed from "Save profile") | `Icons.Filled.Check` | primary (default) |
| `InterviewSlotPickerScreen.kt` | Each `TimeSlotRow` | `Icons.Filled.Schedule` (leading, before the time text) | neutral — it's a list row, not an emphasized action |
| `InterviewSessionDetailScreen.kt` | "Join Video Call" `Button` | `Icons.Filled.Videocam` | **tertiary** container/content colors |
| `InterviewSessionDetailScreen.kt` | "Submit Feedback" `Button` | `Icons.Filled.RateReview` | primary (default) |
| `InterviewFeedbackScreen.kt` | Star rating (`RatingRow`) | `Icons.Filled.Star` / `Icons.Filled.StarBorder`, replacing the current `"★"`/`"☆"` text glyphs | same primary/onSurfaceVariant logic already in place, just rendered as real icons |
| `InterviewFeedbackScreen.kt` | "My peer didn't show up" `Checkbox` | — (no icon change) | `checkedColor` → `MaterialTheme.colorScheme.error` |
| `InterviewFeedbackScreen.kt` | "Submit" `Button` | `Icons.Filled.Send` | primary (default) |

Implementation pattern for icon+label buttons (Material 3's `Button` has no built-in icon slot —
this is the standard idiom, and this codebase doesn't have an existing button-with-icon precedent
to match, so this establishes one cleanly):

```kotlin
Button(onClick = { ... }) {
    Icon(Icons.Filled.Login, contentDescription = null, modifier = Modifier.size(18.dp))
    Spacer(modifier = Modifier.width(8.dp))
    Text("Sign in with Google")
}
```

## Copy

One change, not a broad rewrite — the rest of the feature's copy was already reviewed carefully
earlier in this session and reads clearly as-is:

| Location | Current | New | Why |
|---|---|---|---|
| `InterviewProfileSetupScreen.kt` "Save profile" button | "Save profile" | "Save & Continue" | Tapping it also calls `onProfileSaved()`, navigating forward to the session list — the current label doesn't signal that it proceeds, only that it saves. |

## Out of scope (explicitly declined or not raised)

- Role-based color coding for chips/cards (user chose action-intent coloring instead).
- Any change to `InterviewSessionListScreen.kt`, `InterviewCalendarGrid.kt`, or
  `InterviewHowItWorks.kt` — already reviewed and not flagged as needing icon/color/copy changes
  in this pass.
- Dark theme / Material You scheme changes — `Theme.kt` currently only defines a light scheme
  plus `dynamicLightColorScheme` (no `dynamicDarkColorScheme`); that's a pre-existing, app-wide
  gap unrelated to this feature's polish and not something this spec addresses.

## Testing

Presentational-only changes (icon swaps, color parameters, one string literal, a checkbox color
parameter) — no new logic to unit test, matching this codebase's existing convention for
Compose UI files. Verification is `compileDebugKotlin` + `testDebugUnitTest` (confirming nothing
existing broke) + detekt (zero new findings in the 4 touched files) + an on-device manual check
of all four screens.
