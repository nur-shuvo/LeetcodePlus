# Mock Interview Calendar Slot Picker Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Replace the mock interview slot picker's flat scrollable list with a month calendar
grid for picking a date, and a short tap-to-book list of that date's time slots below it.

**Architecture:** A new pure helper object (`InterviewSlotCalendar`) does all date-window math
(grouping slots by local calendar date, computing the Sunday-first month grid, clamping month
navigation) and is unit tested directly with no Android dependencies. The existing
`InterviewSlotPickerViewModel` exposes new derived `StateFlow`s built from that helper and the
already-existing `slots`/`getMyBookings()` data. A new `InterviewCalendarGrid.kt` file holds the
grid's composables. `InterviewSlotPickerScreen.kt` is rewired to render the grid plus a plain
tap-to-book list for the selected date, replacing the old flat `LazyColumn` with per-row "Book"
buttons.

**Tech Stack:** Kotlin, Jetpack Compose (Material 3), `java.time` (LocalDate/YearMonth — safe at
minSdk 24 because core library desugaring is already enabled in `app/build.gradle.kts`), JUnit4
for the pure-logic tests.

## Global Constraints

- Sunday-first week grid (matches the approved mockup: `Su Mo Tu We Th Fr Sa`).
- Month nav arrows are disabled (not hidden) at the 14-day booking window's edges — "previous"
  never goes before the month containing today; "next" never goes past the month containing the
  window's last bookable date.
- A calendar day is tappable only if it has ≥1 slot in `slotsByDate` (booking-filtered); this
  covers past days, days outside the window, and days where the user already booked all 3 role
  slots — all three collapse to the same "no open slot" untappable/dimmed state.
- Tapping a time slot row books it directly — no separate "Book" button.
- No changes to `InterviewSlotRepository`, the booking transaction, Firestore rules, security
  rules, or any other screen. This is presentation-only.
- No coroutines-test dependency exists in this project and no ViewModel is unit-tested anywhere
  today (`InterviewSlotCatalogTest.kt`/`InterviewMatchDecisionTest.kt` both test plain
  functions/objects) — do not introduce ViewModel-level tests; only the new pure
  `InterviewSlotCalendar` functions get unit tests, matching existing convention.
- `MaxLineLength` (120 chars) is NOT excluded for `ui/` paths in this repo's actual
  `detekt.yml` (only `LongMethod`/`LongParameterList`/`MagicNumber`/naming rules are) — keep
  every line, including in UI files, under 120 characters.
- `CyclomaticComplexMethod` and `NestedBlockDepth` exclude `**/*Screen.kt` but NOT other UI
  files — keep `InterviewCalendarGrid.kt`'s composables shallow (see Task 3's file-splitting).

---

### Task 1: `InterviewSlotCalendar` pure helper + unit tests

**Files:**
- Create: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendar.kt`
- Create: `app/src/test/java/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendarTest.kt`

**Interfaces:**
- Consumes: `com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot` (existing:
  `slotId: String`, `role: InterviewRole`, `startEpochMillis: Long`, `endEpochMillis: Long`).
- Produces (used by Task 2's ViewModel and Task 3's composables):
  - `InterviewSlotCalendar.groupByLocalDate(slots: List<InterviewSlot>, zoneId: ZoneId = ZoneId.systemDefault()): Map<LocalDate, List<InterviewSlot>>`
  - `InterviewSlotCalendar.clampMonth(requested: YearMonth, today: LocalDate, lastWindowDate: LocalDate): YearMonth`
  - `InterviewSlotCalendar.monthGridDates(month: YearMonth): List<List<LocalDate?>>` (Sunday-first
    weeks; `null` marks a leading/trailing blank cell outside `month`)

- [ ] **Step 1: Write the failing tests**

Create `app/src/test/java/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendarTest.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

class InterviewSlotCalendarTest {

    @Test
    fun groupByLocalDateGroupsByTheGivenZoneNotUtc() {
        // 2026-07-21T19:00:00Z is 2026-07-22T01:00 in UTC+6, so it must group under July 22
        // when grouped in that zone, even though its UTC calendar date is July 21.
        val slot = InterviewSlot(
            slotId = "slot1",
            role = InterviewRole.ANDROID,
            startEpochMillis = Instant.parse("2026-07-21T19:00:00Z").toEpochMilli(),
            endEpochMillis = Instant.parse("2026-07-21T20:00:00Z").toEpochMilli(),
        )
        val zone = ZoneId.of("Asia/Dhaka")

        val grouped = InterviewSlotCalendar.groupByLocalDate(listOf(slot), zone)

        assertEquals(setOf(LocalDate.of(2026, 7, 22)), grouped.keys)
    }

    @Test
    fun groupByLocalDateReturnsEmptyMapForEmptyInput() {
        assertTrue(InterviewSlotCalendar.groupByLocalDate(emptyList()).isEmpty())
    }

    @Test
    fun clampMonthPassesThroughARequestInsideTheWindow() {
        val today = LocalDate.of(2026, 7, 21)
        val lastWindowDate = LocalDate.of(2026, 8, 3)
        val requested = YearMonth.of(2026, 7)

        assertEquals(requested, InterviewSlotCalendar.clampMonth(requested, today, lastWindowDate))
    }

    @Test
    fun clampMonthClampsToTodaysMonthWhenRequestedIsEarlier() {
        val today = LocalDate.of(2026, 7, 21)
        val lastWindowDate = LocalDate.of(2026, 8, 3)
        val requested = YearMonth.of(2026, 6)

        assertEquals(
            YearMonth.of(2026, 7),
            InterviewSlotCalendar.clampMonth(requested, today, lastWindowDate)
        )
    }

    @Test
    fun clampMonthClampsToLastWindowMonthWhenRequestedIsLater() {
        val today = LocalDate.of(2026, 7, 21)
        val lastWindowDate = LocalDate.of(2026, 8, 3)
        val requested = YearMonth.of(2026, 9)

        assertEquals(
            YearMonth.of(2026, 8),
            InterviewSlotCalendar.clampMonth(requested, today, lastWindowDate)
        )
    }

    @Test
    fun clampMonthHandlesWindowSpanningTwoCalendarMonths() {
        val today = LocalDate.of(2026, 7, 21)
        val lastWindowDate = LocalDate.of(2026, 8, 3)

        assertEquals(
            YearMonth.of(2026, 8),
            InterviewSlotCalendar.clampMonth(YearMonth.of(2026, 8), today, lastWindowDate)
        )
        assertEquals(
            YearMonth.of(2026, 7),
            InterviewSlotCalendar.clampMonth(YearMonth.of(2026, 7), today, lastWindowDate)
        )
    }

    @Test
    fun monthGridDatesHasThreeLeadingBlanksForJuly2026WhichStartsOnAWednesday() {
        // Verified: July 1, 2026 is a Wednesday. Sunday-first grid -> Su, Mo, Tu are blank.
        val grid = InterviewSlotCalendar.monthGridDates(YearMonth.of(2026, 7))

        assertEquals(
            listOf(null, null, null, LocalDate.of(2026, 7, 1)),
            grid.first().take(4)
        )
    }

    @Test
    fun monthGridDatesIncludesEveryDayOfTheMonthExactlyOnceInOrder() {
        val month = YearMonth.of(2026, 7)

        val allDates = InterviewSlotCalendar.monthGridDates(month).flatten().filterNotNull()

        assertEquals((1..month.lengthOfMonth()).map(month::atDay), allDates)
    }

    @Test
    fun monthGridDatesEveryWeekHasSevenCells() {
        val grid = InterviewSlotCalendar.monthGridDates(YearMonth.of(2026, 7))

        assertTrue(grid.all { it.size == 7 })
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `./gradlew :app:testDebugUnitTest --tests "*.InterviewSlotCalendarTest" --console=plain`
Expected: FAIL — `InterviewSlotCalendar` is unresolved (the class doesn't exist yet).

- [ ] **Step 3: Write the implementation**

Create `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendar.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId

private const val DAYS_IN_WEEK = 7

/**
 * Pure date/calendar math backing the mock-interview slot picker's calendar grid - kept free of
 * Android/Compose/Firestore dependencies so it's directly unit-testable, matching
 * [InterviewSlotCatalog]'s existing style.
 */
object InterviewSlotCalendar {

    /** Groups [slots] by the local calendar date of their `startEpochMillis` in [zoneId] - not
     * the catalog's internal UTC anchoring, since a slot generated for 19:00 UTC can fall on the
     * next local calendar day in timezones ahead of UTC and must group under that day. */
    fun groupByLocalDate(
        slots: List<InterviewSlot>,
        zoneId: ZoneId = ZoneId.systemDefault(),
    ): Map<LocalDate, List<InterviewSlot>> =
        slots.groupBy { Instant.ofEpochMilli(it.startEpochMillis).atZone(zoneId).toLocalDate() }

    /** Clamps [requested] so it's never before the month containing [today] and never after the
     * month containing [lastWindowDate] - used to keep month navigation inside the 14-day
     * booking window. */
    fun clampMonth(requested: YearMonth, today: LocalDate, lastWindowDate: LocalDate): YearMonth {
        val minMonth = YearMonth.from(today)
        val maxMonth = YearMonth.from(lastWindowDate)
        return when {
            requested.isBefore(minMonth) -> minMonth
            requested.isAfter(maxMonth) -> maxMonth
            else -> requested
        }
    }

    /** Sunday-first weeks covering [month]; each week has exactly 7 entries, with `null` for the
     * leading/trailing cells that fall outside [month]. */
    fun monthGridDates(month: YearMonth): List<List<LocalDate?>> {
        val firstOfMonth = month.atDay(1)
        val leadingBlanks = firstOfMonth.dayOfWeek.value % DAYS_IN_WEEK
        val daysInMonth = month.lengthOfMonth()
        val totalCells = leadingBlanks + daysInMonth
        val weekCount = (totalCells + DAYS_IN_WEEK - 1) / DAYS_IN_WEEK

        return (0 until weekCount).map { week ->
            (0 until DAYS_IN_WEEK).map { dayOfWeekIndex ->
                val cellIndex = week * DAYS_IN_WEEK + dayOfWeekIndex
                val dayOfMonth = cellIndex - leadingBlanks + 1
                if (dayOfMonth in 1..daysInMonth) month.atDay(dayOfMonth) else null
            }
        }
    }
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `./gradlew :app:testDebugUnitTest --tests "*.InterviewSlotCalendarTest" --console=plain`
Expected: PASS — all 8 tests green.

- [ ] **Step 5: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendar.kt \
        app/src/test/java/com/byteutility/dev/leetcode/plus/data/repository/interview/InterviewSlotCalendarTest.kt
git commit -m "$(cat <<'EOF'
Add InterviewSlotCalendar: pure date-grid helper for the slot picker

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 2: Wire calendar state into `InterviewSlotPickerViewModel`

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerViewModel.kt` (currently 71 lines — full replacement below)

**Interfaces:**
- Consumes: `InterviewSlotCalendar.groupByLocalDate`/`clampMonth` (Task 1);
  `InterviewSlotRepository.getAvailableSlots(role: InterviewRole): List<InterviewSlot>` and
  `getMyBookings(): Flow<List<SlotBooking>>` (existing, unchanged).
- Produces (used by Task 4's Screen):
  - `slotsByDate: StateFlow<Map<LocalDate, List<InterviewSlot>>>`
  - `selectedDate: StateFlow<LocalDate?>`
  - `visibleMonth: StateFlow<YearMonth>`
  - `windowEndDate: StateFlow<LocalDate>`
  - `fun selectDate(date: LocalDate)`
  - `fun navigateMonth(monthDelta: Int)`
  - `selectedRole`, `slots`, `bookingState`, `selectRole(role)`, `bookSlot(slot)` — all unchanged
    from the existing file.

- [ ] **Step 1: Replace the ViewModel file**

Replace the full contents of
`app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerViewModel.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotCalendar
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.NotSignedInException
import com.byteutility.dev.leetcode.plus.data.repository.interview.TooManyActiveBookingsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

sealed interface BookingUiState {
    data object Idle : BookingUiState
    data object Booking : BookingUiState
    data object Booked : BookingUiState
    data class Error(val message: String) : BookingUiState
}

@HiltViewModel
class InterviewSlotPickerViewModel @Inject constructor(
    private val interviewSlotRepository: InterviewSlotRepository,
) : ViewModel() {

    private val _selectedRole = MutableStateFlow(InterviewRole.ANDROID)
    val selectedRole: StateFlow<InterviewRole> = _selectedRole.asStateFlow()

    /** Slots for the selected role, minus any the user already has a booking for (waiting or
     * matched) - re-booking the same slot was already a harmless no-op, but showing it as
     * "available" was confusing since tapping Book on it visibly did nothing. */
    val slots: StateFlow<List<InterviewSlot>> = combine(
        _selectedRole.map { role -> interviewSlotRepository.getAvailableSlots(role) },
        interviewSlotRepository.getMyBookings(),
    ) { availableSlots, myBookings ->
        val bookedKeys = myBookings.map { it.slotId to it.role }.toSet()
        availableSlots.filterNot { (it.slotId to it.role) in bookedKeys }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyList())

    /** [slots] grouped by local calendar date - drives which grid days are tappable and
     * supplies the time rows for [selectedDate]. */
    val slotsByDate: StateFlow<Map<LocalDate, List<InterviewSlot>>> = slots
        .map { InterviewSlotCalendar.groupByLocalDate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyMap())

    /** The local calendar date of the last slot in the current role's 14-day window,
     * regardless of booking state - used only to clamp [visibleMonth] navigation forward. */
    val windowEndDate: StateFlow<LocalDate> = _selectedRole
        .map { role ->
            InterviewSlotCalendar.groupByLocalDate(interviewSlotRepository.getAvailableSlots(role))
                .keys
                .maxOrNull() ?: LocalDate.now()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), LocalDate.now())

    /** Null unless the user has explicitly tapped a day; falls back to the earliest date in
     * [slotsByDate] otherwise. Cleared back to null by [selectRole]. */
    private val _selectedDateOverride = MutableStateFlow<LocalDate?>(null)

    val selectedDate: StateFlow<LocalDate?> = combine(
        slotsByDate,
        _selectedDateOverride,
    ) { byDate, override ->
        override ?: byDate.keys.minOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    /** Null unless the user has explicitly navigated the month view; follows [selectedDate]'s
     * month otherwise. Cleared back to null by [selectRole] and [selectDate]. */
    private val _visibleMonthOverride = MutableStateFlow<YearMonth?>(null)

    val visibleMonth: StateFlow<YearMonth> = combine(
        selectedDate,
        _visibleMonthOverride,
    ) { selected, override ->
        override ?: selected?.let { YearMonth.from(it) } ?: YearMonth.now()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), YearMonth.now())

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    fun selectRole(role: InterviewRole) {
        _selectedRole.value = role
        _selectedDateOverride.value = null
        _visibleMonthOverride.value = null
    }

    fun selectDate(date: LocalDate) {
        _selectedDateOverride.value = date
        _visibleMonthOverride.value = null
    }

    fun navigateMonth(monthDelta: Int) {
        val requested = visibleMonth.value.plusMonths(monthDelta.toLong())
        _visibleMonthOverride.value =
            InterviewSlotCalendar.clampMonth(requested, LocalDate.now(), windowEndDate.value)
    }

    fun bookSlot(slot: InterviewSlot) {
        viewModelScope.launch {
            _bookingState.value = BookingUiState.Booking
            runCatching { interviewSlotRepository.bookSlot(slot) }
                .onSuccess { _bookingState.value = BookingUiState.Booked }
                .onFailure { e ->
                    val message = when (e) {
                        is NotSignedInException -> "Sign in with Google first to book a mock interview."
                        is TooManyActiveBookingsException -> e.message.orEmpty()
                        else -> "Couldn't book that slot, please try again."
                    }
                    _bookingState.value = BookingUiState.Error(message)
                }
        }
    }
}
```

Note on why `windowEndDate` is computed this way rather than as a lazily-collected side value:
`navigateMonth()` reads `windowEndDate.value` and `visibleMonth.value` directly. Both are safe to
read this way only because both are `StateFlow`s that the Screen actively collects via
`collectAsStateWithLifecycle()` in Task 4 — `SharingStarted.WhileSubscribed` only keeps a
`StateFlow`'s upstream running while something is collecting it, so a `StateFlow` nobody
collects would silently freeze at its seed value. Do not add any `StateFlow` here that only
`navigateMonth()` reads via `.value` without the Screen also collecting it.

- [ ] **Step 2: Run the build to verify it compiles**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL` (Task 4 wires the Screen to actually use the new state — this step
only confirms the ViewModel itself is valid Kotlin with correct types).

- [ ] **Step 3: Run existing unit tests to confirm nothing broke**

Run: `./gradlew :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL` — all existing tests (including Task 1's) still pass.

- [ ] **Step 4: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerViewModel.kt
git commit -m "$(cat <<'EOF'
Add calendar-grid state to InterviewSlotPickerViewModel

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 3: `InterviewCalendarGrid` composable

**Files:**
- Create: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewCalendarGrid.kt`

**Interfaces:**
- Consumes: `InterviewSlotCalendar.monthGridDates(month: YearMonth): List<List<LocalDate?>>`
  (Task 1).
- Produces (used by Task 4's Screen):
  - `@Composable fun InterviewCalendarGrid(visibleMonth: YearMonth, selectedDate: LocalDate?, datesWithSlots: Set<LocalDate>, canGoToPreviousMonth: Boolean, canGoToNextMonth: Boolean, onPreviousMonth: () -> Unit, onNextMonth: () -> Unit, onSelectDate: (LocalDate) -> Unit, modifier: Modifier = Modifier)`

This file is split into five small composables (`InterviewCalendarGrid`, `CalendarHeader`,
`WeekdayLabelsRow`, `WeekRow`, `DayCell`) specifically to keep each function's nesting shallow —
`CyclomaticComplexMethod`/`NestedBlockDepth` exclude `**/*Screen.kt` in this repo's `detekt.yml`
but NOT other UI files, so a single monolithic grid-building function here risks tripping those
rules.

- [ ] **Step 1: Create the file**

Create `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewCalendarGrid.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotCalendar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val WEEKDAY_LABELS = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
private const val DAY_CELL_MUTED_ALPHA = 0.4f

@Composable
fun InterviewCalendarGrid(
    visibleMonth: YearMonth,
    selectedDate: LocalDate?,
    datesWithSlots: Set<LocalDate>,
    canGoToPreviousMonth: Boolean,
    canGoToNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weeks = remember(visibleMonth) { InterviewSlotCalendar.monthGridDates(visibleMonth) }

    Column(modifier = modifier.fillMaxWidth()) {
        CalendarHeader(
            visibleMonth = visibleMonth,
            canGoToPreviousMonth = canGoToPreviousMonth,
            canGoToNextMonth = canGoToNextMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
        )
        WeekdayLabelsRow()
        weeks.forEach { week ->
            WeekRow(
                week = week,
                selectedDate = selectedDate,
                datesWithSlots = datesWithSlots,
                onSelectDate = onSelectDate,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    visibleMonth: YearMonth,
    canGoToPreviousMonth: Boolean,
    canGoToNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonth, enabled = canGoToPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(text = "$monthName ${visibleMonth.year}", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onNextMonth, enabled = canGoToNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun WeekdayLabelsRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        WEEKDAY_LABELS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekRow(
    week: List<LocalDate?>,
    selectedDate: LocalDate?,
    datesWithSlots: Set<LocalDate>,
    onSelectDate: (LocalDate) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        week.forEach { date ->
            if (date != null) {
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    hasSlots = date in datesWithSlots,
                    onClick = { onSelectDate(date) },
                    modifier = Modifier.weight(1f),
                )
            } else {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasSlots: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val cellModifier = modifier
        .aspectRatio(1f)
        .padding(2.dp)
        .clip(CircleShape)
        .background(backgroundColor)
        .then(if (hasSlots) Modifier.clickable(onClick = onClick) else Modifier)

    Box(modifier = cellModifier, contentAlignment = Alignment.Center) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                hasSlots -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DAY_CELL_MUTED_ALPHA)
            },
        )
    }
}
```

- [ ] **Step 2: Run the build to verify it compiles**

Run: `./gradlew :app:compileDebugKotlin --console=plain`
Expected: `BUILD SUCCESSFUL`. This file isn't wired into the Screen yet (Task 4 does that), so
this step only confirms it's valid, self-contained Compose code.

- [ ] **Step 3: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewCalendarGrid.kt
git commit -m "$(cat <<'EOF'
Add InterviewCalendarGrid composable for the mock interview slot picker

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```

---

### Task 4: Rewire `InterviewSlotPickerScreen` and verify end-to-end

**Files:**
- Modify: `app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerScreen.kt` (currently 117 lines — full replacement below)

**Interfaces:**
- Consumes: everything Task 2 added to `InterviewSlotPickerViewModel`, plus
  `InterviewCalendarGrid` (Task 3). No new interfaces produced — this is the final consumer.

- [ ] **Step 1: Replace the Screen file**

Replace the full contents of
`app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerScreen.kt`:

```kotlin
package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import java.time.YearMonth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSlotPickerScreen(
    onBooked: () -> Unit = {},
    viewModel: InterviewSlotPickerViewModel = hiltViewModel()
) {
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
    val slotsByDate by viewModel.slotsByDate.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val visibleMonth by viewModel.visibleMonth.collectAsStateWithLifecycle()
    val windowEndDate by viewModel.windowEndDate.collectAsStateWithLifecycle()
    val bookingState by viewModel.bookingState.collectAsStateWithLifecycle()

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.Booked) {
            onBooked()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Book a Mock Interview") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(InterviewRole.entries) { role ->
                    FilterChip(
                        selected = selectedRole == role,
                        onClick = { viewModel.selectRole(role) },
                        label = { Text(role.displayName) }
                    )
                }
            }

            val bookingError = bookingState as? BookingUiState.Error
            if (bookingError != null) {
                Text(text = bookingError.message, color = MaterialTheme.colorScheme.error)
            }

            InterviewCalendarGrid(
                visibleMonth = visibleMonth,
                selectedDate = selectedDate,
                datesWithSlots = slotsByDate.keys,
                canGoToPreviousMonth = visibleMonth.isAfter(YearMonth.now()),
                canGoToNextMonth = visibleMonth.isBefore(YearMonth.from(windowEndDate)),
                onPreviousMonth = { viewModel.navigateMonth(-1) },
                onNextMonth = { viewModel.navigateMonth(1) },
                onSelectDate = { viewModel.selectDate(it) },
            )

            val slotsForSelectedDate = selectedDate?.let { slotsByDate[it] }.orEmpty()
            if (slotsForSelectedDate.isEmpty()) {
                Text(
                    text = "You've booked all available ${selectedRole.displayName} slots.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    slotsForSelectedDate.forEach { slot ->
                        TimeSlotRow(
                            slot = slot,
                            isBooking = bookingState is BookingUiState.Booking,
                            onBook = { viewModel.bookSlot(slot) }
                        )
                    }
                }
            }
        }
    }
}

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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatter.format(Date(slot.startEpochMillis)))
        }
    }
}
```

Note: rows show each slot's actual local time (e.g. "8:00 AM") rather than fixed
"Morning/Afternoon/Evening" labels. In timezones offset from UTC, the catalog's fixed
08:00/13:00/19:00 UTC slots don't always land in that same relative order once grouped by local
date near the window's edges — showing the real local time avoids a label that could be wrong
for some users, and it's what the spec's own example ("8:00 AM") was actually showing.

- [ ] **Step 2: Run the full verification suite**

Run: `./gradlew :app:compileDebugKotlin :app:testDebugUnitTest --console=plain`
Expected: `BUILD SUCCESSFUL`.

Run detekt (on this machine, `JAVA_HOME` must point at a JDK ≤ 22 — the system default JDK 24 is
rejected by detekt's embedded compiler):
`JAVA_HOME=/Users/pathao/Library/Java/JavaVirtualMachines/corretto-17.0.16/Contents/Home ./gradlew detekt --console=plain`
Expected: the same 15 pre-existing findings this project already had before this change (in
`ProblemsDao.kt`, `ProblemsRepositoryImpl.kt`, `UserLoginViewModel.kt`,
`NetworkLogInterceptor.kt`, `Extensions.kt`, and the algorithm-visualizer/code-editor files) —
zero *new* findings introduced by this plan's files. If detekt reports anything in
`InterviewSlotCalendar.kt`, `InterviewSlotPickerViewModel.kt`, `InterviewCalendarGrid.kt`, or
`InterviewSlotPickerScreen.kt`, fix it before continuing (most likely a stray line over 120
characters, per the Global Constraints note above).

- [ ] **Step 3: Build and install the debug APK on a connected device**

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

- [ ] **Step 4: Manually verify on-device**

Navigate: Home → "Practice a mock interview" → (sign in if needed) → Mock Interviews list → tap
the **+** FAB to open the slot picker. Using `adb -s <SERIAL> shell screencap -p /sdcard/x.png`
+ `adb -s <SERIAL> pull /sdcard/x.png <local-path>` to inspect each state, confirm:

- A month grid renders with a weekday header row (`Su Mo Tu We Th Fr Sa`) and the current month's
  days filled in Sunday-first.
- The default-selected day (highlighted with a filled circle) is the earliest day that still has
  an open slot for the currently-selected role — not necessarily today, if today's slots are
  already gone.
- Below the grid, up to 3 tappable time rows show for the selected day, each with a real local
  time (e.g. "8:00 AM").
- Days with no open slot (past days, or any day where all 3 role slots are already booked/gone)
  render dimmed and tapping them does nothing.
- Tapping the "›" (next month) arrow advances the grid; tapping it repeatedly stops advancing
  (button visibly disables) once the month containing the 14-day window's last bookable date is
  reached. Tapping "‹" (previous month) is disabled immediately if already on the current month.
- Switching the role `FilterChip` at the top resets the grid to that role's first open day (and
  its own window-end clamping).
- Tapping an open time-slot row books it directly (no separate "Book" button anywhere) and
  navigates back to the Mock Interviews list on success, exactly as before.
- If the user already has 3 upcoming bookings, tapping a new day/slot still shows the existing
  "You already have 3 upcoming mock interviews..." inline error message.

Fix anything that doesn't match before proceeding — this is the actual feature verification, not
just a compile check.

- [ ] **Step 5: Update the feature doc**

In `docs/mock-interview-feature-todo.md`, find this exact text (the file currently opens with it):

```
## UX additions (2026-07-21)

- **Live match detection**: `InterviewMatchWatcherViewModel`
```

Replace it with (inserting a new section directly above the existing one, changing nothing else):

```
## Calendar-style slot picker (2026-07-21)

The slot picker (`InterviewSlotPickerScreen.kt`) was redesigned from a flat scrollable list of
~42 rows (14 days x 3 fixed times/day) into a month calendar grid for picking a date, with a
short tap-to-book list of that day's times below it — the flat list was hard to scan. New pure
date/grid math lives in `InterviewSlotCalendar.kt` (unit tested); the grid itself is
`InterviewCalendarGrid.kt`. Full design:
`docs/superpowers/specs/2026-07-21-mock-interview-calendar-picker-design.md`.

## UX additions (2026-07-21)

- **Live match detection**: `InterviewMatchWatcherViewModel`
```

- [ ] **Step 6: Commit**

```bash
git add app/src/main/kotlin/com/byteutility/dev/leetcode/plus/ui/screens/interview/slots/InterviewSlotPickerScreen.kt \
        docs/mock-interview-feature-todo.md
git commit -m "$(cat <<'EOF'
Redesign mock interview slot picker as a calendar grid

Replaces the flat 42-row list (14 days x 3 fixed times) with a month
calendar for picking a date and a short tap-to-book list of that
day's times, per the approved design spec.

Co-Authored-By: Claude Sonnet 5 <noreply@anthropic.com>
EOF
)"
```
