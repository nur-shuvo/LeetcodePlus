# Mock Interview "How It Works" Section Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add a reusable "how it works" explanation (bullets + a 4-icon visual summary) to the
mock interview feature, surfaced inline on the profile setup screen and on-demand via an info
icon on the session list.

**Architecture:** One new file holds two static composables — the shared content and a dialog
wrapper around it. Two existing screens each get a small, additive change to show one or the
other. No ViewModel, repository, or data-model changes anywhere.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), existing Material icons (no new
dependencies, no new image assets).

## Global Constraints

- No new image/illustration asset — the icon-based visual (`Icons.Filled.Person`,
  `CalendarToday`, `People`, `Videocam`) is the illustration. All four are already available:
  `Person` ships in the base `material-icons-core` artifact; `CalendarToday`, `People`, and
  `Videocam` are in the `material-icons-extended` artifact already declared in this project's
  `gradle/libs.versions.toml` — confirmed present in the resolved dependency jars during
  brainstorming, not assumed.
- `Icons.Filled.Info` (used for the info-icon entry point) is also confirmed present, in the base
  `material-icons-core` artifact.
- No new navigation route or back-stack entry for the on-demand view — it's an `AlertDialog`, not
  a screen.
- No changes to any ViewModel, repository, Firestore rules, or the booking/matching logic — this
  is presentation-only, additive content.
- No new automated test — matches this codebase's existing convention for static/presentational
  composables (e.g. `InterviewSessionPhase.kt`'s status chips, `InterviewCalendarGrid.kt` have no
  dedicated tests either). Verification is `compileDebugKotlin` + `testDebugUnitTest` (confirming
  nothing existing broke) + detekt + an on-device manual check.
- `MaxLineLength` (120 chars) applies to every file touched here — this repo's actual
  `detekt.yml` does NOT exclude `ui/` paths from `MaxLineLength` (only
  `LongMethod`/`LongParameterList`/`MagicNumber`/naming rules are excluded for `ui/`).
- Detekt currently reports exactly 15 pre-existing findings project-wide, unrelated to this
  plan's files (in `ProblemsDao.kt`, `ProblemsRepositoryImpl.kt`, `UserLoginViewModel.kt`,
  `NetworkLogInterceptor.kt`, `Extensions.kt`, and some algorithm-visualizer/code-editor files).
  This plan's files must add zero new findings.

---

### Task 1: `InterviewHowItWorks.kt` shared content + dialog

**Files:**
- Create: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/InterviewHowItWorks.kt`

**Interfaces:**
- Produces (used by Task 2 and Task 3):
  - `@Composable fun InterviewHowItWorksContent(modifier: Modifier = Modifier)`
  - `@Composable fun InterviewHowItWorksDialog(onDismiss: () -> Unit)`

- [ ] **Step 1: Create the file**

Create `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/InterviewHowItWorks.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class HowItWorksStep(val icon: ImageVector, val label: String)

private val HOW_IT_WORKS_STEPS = listOf(
    HowItWorksStep(Icons.Filled.Person, "Set up profile"),
    HowItWorksStep(Icons.Filled.CalendarToday, "Book a slot"),
    HowItWorksStep(Icons.Filled.People, "Get matched"),
    HowItWorksStep(Icons.Filled.Videocam, "Join & practice"),
)

private val HOW_IT_WORKS_BULLETS = listOf(
    "Pick the role(s) you want to practice and sign in with Google",
    "Book any open time slot - 3 times a day, up to 14 days ahead",
    "Matched instantly if someone's already waiting on that slot",
    "Otherwise you wait, and get notified the moment a peer books it too",
    "Join your video call once it's within 5 minutes of starting",
    "Leave quick feedback for your peer afterward",
)

/**
 * Static "how it works" explanation for the mock-interview feature: a 4-step icon summary plus
 * a bulleted walkthrough. Reused both inline (expandable on the profile setup screen) and inside
 * [InterviewHowItWorksDialog] (on-demand from the session list) - this is the single source of
 * truth for the copy so neither consumer duplicates it.
 */
@Composable
fun InterviewHowItWorksContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HOW_IT_WORKS_STEPS.forEach { step -> HowItWorksStepIcon(step) }
        }
        HOW_IT_WORKS_BULLETS.forEach { bullet ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                Text(text = bullet, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun HowItWorksStepIcon(step: HowItWorksStep) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = step.icon, contentDescription = "")
        Text(text = step.label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}

/** On-demand "how it works" dialog, opened from an info icon on the session list. */
@Composable
fun InterviewHowItWorksDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How Mock Interviews Work") },
        text = { InterviewHowItWorksContent() },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        }
    )
}
```

- [ ] **Step 2: Run the build to verify it compiles**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`. This file isn't referenced from any other file yet (Tasks 2 and 3
wire it in), so this step only confirms it's valid, self-contained Compose code.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/InterviewHowItWorks.kt
git commit -m "$(cat <<'EOF'
Add InterviewHowItWorks: shared how-it-works content + dialog

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 2: Inline expandable section on the profile setup screen

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/profile/InterviewProfileSetupScreen.kt`

**Interfaces:**
- Consumes: `InterviewHowItWorksContent(modifier: Modifier = Modifier)` (Task 1).

- [ ] **Step 1: Add the new imports**

In `InterviewProfileSetupScreen.kt`, find this exact block near the top of the file:

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
```

Replace it with (adds `AnimatedVisibility`, `TextButton`, and the Task 1 import):

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewHowItWorksContent
```

- [ ] **Step 2: Add the toggle state**

Find this exact block:

```kotlin
    var selectedRoles by remember(profile) { mutableStateOf(profile?.roles?.toSet() ?: emptySet()) }
    var leetcodeHandle by remember(profile) { mutableStateOf(profile?.leetcodeHandle ?: "") }
```

Replace it with:

```kotlin
    var selectedRoles by remember(profile) { mutableStateOf(profile?.roles?.toSet() ?: emptySet()) }
    var leetcodeHandle by remember(profile) { mutableStateOf(profile?.leetcodeHandle ?: "") }
    var showHowItWorks by remember { mutableStateOf(false) }
```

- [ ] **Step 3: Add the toggle button and expandable content**

Find this exact block (the pre-sign-in branch):

```kotlin
            if (currentUser == null) {
                Text(
                    text = "Mock interviews match you with another LeetCodePlus user in " +
                        "real time for a live practice session.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Google sign-in is required so we know who you are when pairing " +
                        "you with a peer - this is separate from your LeetCode account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Button(
                    onClick = { viewModel.signIn(context) },
                    enabled = !isSigningIn,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Text("Sign in with Google")
                }
            } else {
```

Replace it with (inserts the toggle + `AnimatedVisibility` between the intro text and the sign-in
button):

```kotlin
            if (currentUser == null) {
                Text(
                    text = "Mock interviews match you with another LeetCodePlus user in " +
                        "real time for a live practice session.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Google sign-in is required so we know who you are when pairing " +
                        "you with a peer - this is separate from your LeetCode account.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(onClick = { showHowItWorks = !showHowItWorks }) {
                    Text(if (showHowItWorks) "Hide details" else "How does this work?")
                }
                AnimatedVisibility(visible = showHowItWorks) {
                    InterviewHowItWorksContent()
                }
                Button(
                    onClick = { viewModel.signIn(context) },
                    enabled = !isSigningIn,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    }
                    Text("Sign in with Google")
                }
            } else {
```

- [ ] **Step 4: Run the build to verify it compiles**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/profile/InterviewProfileSetupScreen.kt
git commit -m "$(cat <<'EOF'
Show an expandable "how it works" section on mock interview sign-in

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 3: On-demand info dialog on the session list

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/sessions/InterviewSessionListScreen.kt`

**Interfaces:**
- Consumes: `InterviewHowItWorksDialog(onDismiss: () -> Unit)` (Task 1).

- [ ] **Step 1: Add the new imports**

Find this exact block:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AssistChip
```

Replace it with:

```kotlin
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
```

Find this exact block:

```kotlin
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
```

Replace it with:

```kotlin
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewHowItWorksDialog
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
```

- [ ] **Step 2: Add the dialog-visibility state**

Find this exact line:

```kotlin
    var accountMenuExpanded by remember { mutableStateOf(false) }
```

Replace it with:

```kotlin
    var accountMenuExpanded by remember { mutableStateOf(false) }
    var showHowItWorks by remember { mutableStateOf(false) }
```

- [ ] **Step 3: Add the info icon button to the TopAppBar**

Find this exact block:

```kotlin
                actions = {
                    Box {
                        IconButton(onClick = { accountMenuExpanded = true }) {
```

Replace it with:

```kotlin
                actions = {
                    IconButton(onClick = { showHowItWorks = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "How mock interviews work")
                    }
                    Box {
                        IconButton(onClick = { accountMenuExpanded = true }) {
```

- [ ] **Step 4: Render the dialog conditionally**

Find this exact block (the end of the composable function):

```kotlin
        }
    }
}

private fun itemKey(item: InterviewListItem): String = when (item) {
```

Replace it with (adds the dialog as a sibling right after the `Scaffold` call, matching where
other top-level conditional UI already lives in this codebase's interview screens):

```kotlin
        }
    }

    if (showHowItWorks) {
        InterviewHowItWorksDialog(onDismiss = { showHowItWorks = false })
    }
}

private fun itemKey(item: InterviewListItem): String = when (item) {
```

- [ ] **Step 5: Run the build to verify it compiles**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/sessions/InterviewSessionListScreen.kt
git commit -m "$(cat <<'EOF'
Add an info icon opening a "how it works" dialog on the session list

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 4: Full verification and doc update

**Files:**
- Modify: `docs/mock-interview-feature-todo.md`

- [ ] **Step 1: Run the full verification suite**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL` (this also confirms Task 1-3's changes didn't break any existing
test, even though none of them added a new one).

Run detekt (on this machine, `JAVA_HOME` must point at a JDK ≤ 22 — the system default JDK 24 is
rejected by detekt's embedded compiler):
`JAVA_HOME=/Users/pathao/Library/Java/JavaVirtualMachines/corretto-17.0.16/Contents/Home ./gradlew detekt --console=plain`
Expected: the same 15 pre-existing findings this project already had (listed in Global
Constraints above) — zero new findings in `InterviewHowItWorks.kt`,
`InterviewProfileSetupScreen.kt`, or `InterviewSessionListScreen.kt`. If detekt reports anything
new in these 3 files, fix it before continuing (most likely a stray line over 120 characters).

- [ ] **Step 2: Build and install the debug APK on a connected device**

Run:
```bash
./gradlew :app:assembleDebug --console=plain
adb devices
```
Take note of the device serial from `adb devices`, then (substituting `<SERIAL>`):
```bash
adb -s <SERIAL> install -r app/build/outputs/apk/debug/app-debug.apk
adb -s <SERIAL> shell am force-stop com.byteutility.dev.leetcode.plus
adb -s <SERIAL> shell am start -n com.byteutility.dev.leetcode.plus/.ui.MainActivity
```
If no device is connected, report this clearly rather than skipping the step silently (matching
how the calendar-picker plan's equivalent step was handled) — the controller will decide whether
to wait for a device or defer this check.

- [ ] **Step 3: Manually verify on-device**

Navigate: Home → "Practice a mock interview". If not currently signed in, confirm:
- The intro text on the profile setup screen is followed by a "How does this work?" link.
- Tapping it expands the 4-icon row (Person/CalendarToday/People/Videocam, each with its label)
  and the 6 bullets below it, with an animated expand/collapse.
- Tapping it again ("Hide details") collapses it back.

Then sign in (or, if already signed in, navigate to the Mock Interviews session list) and
confirm:
- An (i) info icon appears in the top bar, to the left of the account avatar.
- Tapping it opens a dialog titled "How Mock Interviews Work" showing the same icon row and
  bullets.
- Tapping "Got it" (or tapping outside the dialog) dismisses it.

Fix anything that doesn't match before proceeding.

- [ ] **Step 4: Update the feature doc**

In `docs/mock-interview-feature-todo.md`, find this exact text:

```
## Calendar-style slot picker (2026-07-21)
```

Replace it with (inserting a new section directly above, changing nothing else):

```
## In-app "how it works" explanation (2026-07-21)

Added a static "how it works" explanation (4-icon summary + 6 bullets, no image asset - built
entirely from existing Material icons) in `InterviewHowItWorks.kt`, surfaced two ways: an
expandable inline section on the profile setup screen (before sign-in), and an info-icon dialog
in the Mock Interviews session list's top bar (after sign-in, for on-demand reference). Full
design: `docs/superpowers/specs/2026-07-21-mock-interview-how-it-works-design.md`.

## Calendar-style slot picker (2026-07-21)
```

- [ ] **Step 5: Commit**

```bash
git add docs/mock-interview-feature-todo.md
git commit -m "$(cat <<'EOF'
Document the mock interview "how it works" section

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```
