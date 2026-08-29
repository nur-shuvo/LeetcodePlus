# Mock Interview UI Polish Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add leading icons, action-intent color, and one copy fix across the 4 mock-interview screens flagged as "not professional," per the approved spec at `docs/superpowers/specs/2026-07-21-mock-interview-ui-polish-design.md`.

**Architecture:** Purely presentational edits to 4 existing Composable files — no new files, no ViewModel/model/logic changes, no new dependencies. Each task touches exactly one file and is independently buildable/reviewable.

**Tech Stack:** Kotlin, Jetpack Compose, Material 3 (`Icon`, `Icons.Filled.*`, `ButtonDefaults`, `CheckboxDefaults`).

## Global Constraints

- No new Gradle dependencies — every icon (`Login`, `ExpandMore`, `ExpandLess`, `Check`, `Schedule`, `Videocam`, `RateReview`, `Star`, `StarBorder`, `Send`) is already confirmed present in this project's resolved `material-icons-core`/`material-icons-extended` jars (verified during brainstorming).
- No role-based color coding — only the two action-intent color changes specified: "Join Video Call" → tertiary, "didn't show up" checkbox → error when checked. Every other button keeps the default primary `Button`/`TextButton` color.
- Icon size for all leading/trailing button icons: `18.dp`. Spacing between icon and label: `8.dp` (`Spacer(modifier = Modifier.width(8.dp))`), except the toggle's trailing icon which uses `4.dp` (tighter, next to text it follows).
- Exactly one copy change: `InterviewProfileSetupScreen.kt`'s "Save profile" button text becomes `"Save & Continue"`. No other copy in these 4 files changes.
- Detekt: these 4 files are named `*Screen.kt`, so `CyclomaticComplexMethod`/`NestedBlockDepth` are already excluded for them; `MaxLineLength` (120 chars) is **not** excluded for `ui/` — keep every new/changed line under 120 chars (use multi-line `Icon(...)`/`ButtonDefaults.buttonColors(...)` calls, one parameter per line, wherever a single-line call would run long).
- No new tests — matches this codebase's existing convention of no dedicated tests for presentational Compose composables. Verification is `compileDebugKotlin` + `testDebugUnitTest` + `detekt` + on-device manual check.

---

### Task 1: `InterviewProfileSetupScreen.kt` — sign-in icon, toggle icon, save button icon + copy

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/profile/InterviewProfileSetupScreen.kt`

**Interfaces:**
- Consumes: nothing new — no change to `InterviewProfileViewModel`, `InterviewRole`, or `InterviewHowItWorksContent`.
- Produces: nothing consumed by other tasks — this file is self-contained.

- [ ] **Step 1: Update the import block**

Find:
```kotlin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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

Replace with:
```kotlin
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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

- [ ] **Step 2: Add a trailing expand/collapse icon to the "How does this work?" toggle**

Find:
```kotlin
                TextButton(onClick = { showHowItWorks = !showHowItWorks }) {
                    Text(if (showHowItWorks) "Hide details" else "How does this work?")
                }
```

Replace with:
```kotlin
                TextButton(onClick = { showHowItWorks = !showHowItWorks }) {
                    Text(if (showHowItWorks) "Hide details" else "How does this work?")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = if (showHowItWorks) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
```

- [ ] **Step 3: Add a leading `Login` icon to the sign-in button**

Find:
```kotlin
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
```

Replace with:
```kotlin
                Button(
                    onClick = { viewModel.signIn(context) },
                    enabled = !isSigningIn,
                    modifier = Modifier.wrapContentSize(Alignment.Center)
                ) {
                    if (isSigningIn) {
                        CircularProgressIndicator(modifier = Modifier.padding(end = 8.dp))
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Login,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Sign in with Google")
                }
```

This keeps the existing spinner exactly as-is while signing in, and only shows the `Login` icon in the idle state — the two visuals never compete for the same slot.

- [ ] **Step 4: Add a leading `Check` icon to the save button and rename it to "Save & Continue"**

Find:
```kotlin
                Button(
                    onClick = {
                        viewModel.saveRoles(selectedRoles.toList(), leetcodeHandle)
                        onProfileSaved()
                    },
                    enabled = selectedRoles.isNotEmpty()
                ) {
                    Text("Save profile")
                }
```

Replace with:
```kotlin
                Button(
                    onClick = {
                        viewModel.saveRoles(selectedRoles.toList(), leetcodeHandle)
                        onProfileSaved()
                    },
                    enabled = selectedRoles.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save & Continue")
                }
```

- [ ] **Step 5: Verify it compiles**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL, no unresolved reference errors.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/profile/InterviewProfileSetupScreen.kt
git commit -m "feat: add icons and copy fix to interview profile setup screen"
```

---

### Task 2: `InterviewSlotPickerScreen.kt` — leading `Schedule` icon on each time slot row

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerScreen.kt`

**Interfaces:**
- Consumes: nothing new — `InterviewSlot`, `InterviewRole`, `BookingUiState` unchanged.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Update the import block**

Find:
```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import java.text.DateFormat
import java.util.Date
```

Replace with:
```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import java.text.DateFormat
import java.util.Date
```

- [ ] **Step 2: Add a leading `Schedule` icon to `TimeSlotRow`**

Find:
```kotlin
@Composable
private fun TimeSlotRow(slot: InterviewSlot, isBooking: Boolean, onBook: () -> Unit) {
    val formatter = remember { DateFormat.getTimeInstance(DateFormat.SHORT) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isBooking, onClick = onBook)
    ) {
        Text(
            text = formatter.format(Date(slot.startEpochMillis)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        )
    }
}
```

Replace with:
```kotlin
@Composable
private fun TimeSlotRow(slot: InterviewSlot, isBooking: Boolean, onBook: () -> Unit) {
    val formatter = remember { DateFormat.getTimeInstance(DateFormat.SHORT) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isBooking, onClick = onBook)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = Icons.Filled.Schedule, contentDescription = null)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = formatter.format(Date(slot.startEpochMillis)))
        }
    }
}
```

This is a passive list row, not an emphasized action, so the icon uses the default `LocalContentColor` (no explicit tint) rather than any action-intent color.

- [ ] **Step 3: Verify it compiles**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL, no unresolved reference errors.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerScreen.kt
git commit -m "feat: add schedule icon to interview slot picker rows"
```

---

### Task 3: `InterviewSessionDetailScreen.kt` — `Videocam`/tertiary on Join, `RateReview` on Submit Feedback

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/sessiondetail/InterviewSessionDetailScreen.kt`

**Interfaces:**
- Consumes: nothing new — `InterviewSessionPhase`, `statusChipColors()`, `JOIN_WINDOW_MILLIS`, `formatCountdown`, `phase()`, `rememberNowTicker()` unchanged.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Update the import block**

Find:
```kotlin
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.JOIN_WINDOW_MILLIS
import com.byteutility.dev.leetcode.plus.ui.screens.interview.formatCountdown
import com.byteutility.dev.leetcode.plus.ui.screens.interview.phase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.rememberNowTicker
import com.byteutility.dev.leetcode.plus.ui.screens.interview.statusChipColors
import java.text.DateFormat
import java.util.Date
```

Replace with:
```kotlin
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RateReview
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.JOIN_WINDOW_MILLIS
import com.byteutility.dev.leetcode.plus.ui.screens.interview.formatCountdown
import com.byteutility.dev.leetcode.plus.ui.screens.interview.phase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.rememberNowTicker
import com.byteutility.dev.leetcode.plus.ui.screens.interview.statusChipColors
import java.text.DateFormat
import java.util.Date
```

- [ ] **Step 2: Add icons + tertiary color to `SessionAction`'s two buttons**

Find:
```kotlin
    when (phase) {
        InterviewSessionPhase.CANCELLED -> Text("This session was cancelled.")
        InterviewSessionPhase.ENDED, InterviewSessionPhase.EXPIRED ->
            Button(onClick = onSubmitFeedback) { Text("Submit Feedback") }
        InterviewSessionPhase.JOINABLE ->
            Button(onClick = onJoin) { Text("Join Video Call") }
        InterviewSessionPhase.MATCHED_UPCOMING, InterviewSessionPhase.WAITING -> {
```

Replace with:
```kotlin
    when (phase) {
        InterviewSessionPhase.CANCELLED -> Text("This session was cancelled.")
        InterviewSessionPhase.ENDED, InterviewSessionPhase.EXPIRED ->
            Button(onClick = onSubmitFeedback) {
                Icon(
                    imageVector = Icons.Filled.RateReview,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Feedback")
            }
        InterviewSessionPhase.JOINABLE ->
            Button(
                onClick = onJoin,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                )
            ) {
                Icon(
                    imageVector = Icons.Filled.Videocam,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Join Video Call")
            }
        InterviewSessionPhase.MATCHED_UPCOMING, InterviewSessionPhase.WAITING -> {
```

The tertiary color ties this button visually to the `JOINABLE` phase's existing tertiary-container status chip (`statusChipColors()` in `InterviewSessionPhase.kt`) — same "live/active" meaning, now on both the chip and the action.

- [ ] **Step 3: Verify it compiles**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL, no unresolved reference errors.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/sessiondetail/InterviewSessionDetailScreen.kt
git commit -m "feat: add icons and tertiary join color to interview session detail screen"
```

---

### Task 4: `InterviewFeedbackScreen.kt` — real star icons, error-tinted checkbox, `Send` icon

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/feedback/InterviewFeedbackScreen.kt`

**Interfaces:**
- Consumes: nothing new — `SubmitUiState`, `InterviewFeedbackViewModel` unchanged.
- Produces: nothing consumed by other tasks.

- [ ] **Step 1: Update the import block**

Find:
```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

Replace with:
```kotlin
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
```

- [ ] **Step 2: Tint the "didn't show up" checkbox toward error when checked**

Find:
```kotlin
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = didNotShowUp, onCheckedChange = { didNotShowUp = it })
                Text("My peer didn't show up", modifier = Modifier.padding(top = 12.dp))
            }
```

Replace with:
```kotlin
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = didNotShowUp,
                    onCheckedChange = { didNotShowUp = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.error)
                )
                Text("My peer didn't show up", modifier = Modifier.padding(top = 12.dp))
            }
```

Note: the "Would match again" checkbox a few lines below (`Checkbox(checked = wouldMatchAgain, ...)`) is unchanged — it's a neutral preference, not a problem flag, so it keeps the default color.

- [ ] **Step 3: Add a leading `Send` icon to the Submit button**

Find:
```kotlin
            Button(
                onClick = {
                    viewModel.submit(
                        communicationRating,
                        problemSolvingRating,
                        wouldMatchAgain,
                        notes,
                        didNotShowUp
                    )
                },
                enabled = submitState !is SubmitUiState.Submitting
            ) {
                Text("Submit")
            }
```

Replace with:
```kotlin
            Button(
                onClick = {
                    viewModel.submit(
                        communicationRating,
                        problemSolvingRating,
                        wouldMatchAgain,
                        notes,
                        didNotShowUp
                    )
                },
                enabled = submitState !is SubmitUiState.Submitting
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit")
            }
```

- [ ] **Step 4: Replace the Unicode star glyphs in `RatingRow` with real `Star`/`StarBorder` icons**

Find:
```kotlin
@Composable
private fun RatingRow(label: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (value in 1..MAX_RATING) {
                val starColor = if (value <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Text(
                    text = if (value <= rating) "★" else "☆",
                    style = MaterialTheme.typography.headlineSmall.copy(color = starColor),
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onRatingChange(value) }
                )
            }
        }
    }
}
```

Replace with:
```kotlin
@Composable
private fun RatingRow(label: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (value in 1..MAX_RATING) {
                val starColor = if (value <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Icon(
                    imageVector = if (value <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onRatingChange(value) }
                )
            }
        }
    }
}
```

This keeps the exact same `starColor` computation, `clickable`, and `padding` — only the visual (`Text` glyph → `Icon`) changes.

- [ ] **Step 5: Verify it compiles**

Run: `./gradlew :app:compileDebugKotlin`
Expected: BUILD SUCCESSFUL, no unresolved reference errors.

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/feedback/InterviewFeedbackScreen.kt
git commit -m "feat: use real star icons and error-tinted checkbox in interview feedback screen"
```

---

### Task 5: Full verification + doc note

**Files:**
- Modify: `docs/mock-interview-feature-todo.md` (add a short dated section, following the file's existing pattern)

**Interfaces:**
- Consumes: Tasks 1-4's committed changes (this task only verifies and documents; no code changes).
- Produces: nothing — final task.

- [ ] **Step 1: Run the full unit test suite**

Run: `./gradlew :app:testDebugUnitTest`
Expected: BUILD SUCCESSFUL — no test touches these 4 files (presentational-only), so this confirms nothing elsewhere regressed.

- [ ] **Step 2: Run detekt**

Run: `JAVA_HOME=/Users/pathao/Library/Java/JavaVirtualMachines/corretto-17.0.16/Contents/Home ./gradlew detekt`
Expected: exactly 15 findings, all pre-existing (in `ProblemsDao.kt`, `ProblemsRepositoryImpl.kt`, `UserLoginViewModel.kt`, `NetworkLogInterceptor.kt`, `Extensions.kt`, and some algorithm-visualizer/code-editor files) — zero new findings in any of the 4 touched files.

- [ ] **Step 3: Add a doc note**

Find (top of `docs/mock-interview-feature-todo.md`):
```markdown
## In-app "how it works" explanation (2026-07-21)
```

Replace with:
```markdown
## UI polish: icons, action-intent color, copy (2026-07-21)

Added leading icons to every action button/toggle across 4 screens (profile setup,
slot picker, session detail, feedback), replaced the feedback screen's Unicode star
glyphs (★/☆) with real `Icon` composables, and applied two action-intent color
changes: "Join Video Call" now uses the tertiary color (matching the existing
`JOINABLE`-phase status chip), and the "peer didn't show up" checkbox tints toward
error when checked. One copy fix: "Save profile" → "Save & Continue" (it also
navigates forward). Full design: `docs/superpowers/specs/2026-07-21-mock-interview-ui-polish-design.md`.

## In-app "how it works" explanation (2026-07-21)
```

- [ ] **Step 4: Commit**

```bash
git add docs/mock-interview-feature-todo.md
git commit -m "docs: note mock interview UI polish pass"
```

- [ ] **Step 5: On-device manual check**

Build and install the debug APK, then walk all 4 screens:
1. Profile setup (signed out): confirm the `ExpandMore`/`ExpandLess` icon flips correctly when toggling "How does this work?", and the `Login` icon shows on the sign-in button (spinner still shows correctly while signing in).
2. Profile setup (roles step): confirm the `Check` icon and "Save & Continue" text on the save button.
3. Slot picker: confirm each time slot row shows a leading `Schedule` icon.
4. Session detail: book/reach a `JOINABLE` session and confirm the "Join Video Call" button renders in tertiary color with a `Videocam` icon; reach an `ENDED`/`EXPIRED` session and confirm "Submit Feedback" shows a `RateReview` icon in the default color.
5. Feedback screen: confirm star ratings render as real filled/outline star icons (not text glyphs) and respond to taps identically to before; check "My peer didn't show up" and confirm the checkbox tints toward the error color; confirm the Submit button shows a `Send` icon.

Report any visual issue found (e.g., icon/text misalignment, wrong color) back for a follow-up fix — this step is a manual gate, not something a reviewer subagent can perform.

---

## Self-Review

**1. Spec coverage:** All 9 rows of the spec's icon/color table are covered — Task 1 (Login, ExpandMore/Less, Check + copy), Task 2 (Schedule), Task 3 (Videocam/tertiary, RateReview), Task 4 (Star/StarBorder, checkbox error color, Send). The spec's single copy change ("Save profile" → "Save & Continue") is in Task 1 Step 4. No gaps found.

**2. Placeholder scan:** No TBD/TODO/"add appropriate" phrases — every step has complete, verbatim code matching the files read fresh this session.

**3. Type consistency:** No new functions, types, or signatures are introduced by this plan — every change is inline JSX-style Compose content inside existing composables, so there's nothing to drift across tasks. Each task is independent (different file), so there's no cross-task interface to verify beyond "don't touch the other 3 files," which the Files sections enforce.
