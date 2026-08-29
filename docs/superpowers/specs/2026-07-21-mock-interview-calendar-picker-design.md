# Mock Interview Slot Picker — Calendar UI Redesign

## Context

The mock-interview slot picker (`InterviewSlotPickerScreen.kt`) currently shows every bookable
slot for the selected role as one flat scrollable list — up to 42 rows (14 days × 3 fixed
times/day), each just a full date/time string with a "Book" button. The user found this hard to
scan and asked for a "calendar like simple UI" instead.

Slots are always fixed at 3 times/day (morning/afternoon/evening) across a rolling 14-day
forward window, generated deterministically client-side by `InterviewSlotCatalog` — there's no
per-slot capacity or arbitrary time entry to support, which keeps this redesign narrowly scoped
to *how the same fixed slots are presented and selected*, not the underlying booking model.

## Goal

Replace the flat list with a standard month calendar grid for picking a date, and a short list of
that date's (up to 3) time slots below it, each directly tappable to book. No changes to
`InterviewSlotRepository`, the booking transaction, Firestore rules, or any other screen.

## Data model & ViewModel changes

`InterviewSlotPickerViewModel` (existing file) gains:

- `visibleMonth: StateFlow<YearMonth>` — the month currently shown in the grid. Clamped: "previous
  month" never goes before the month containing today; "next month" never goes past the month
  containing the last slot in the 14-day window. The nav arrows are disabled (not hidden) at
  these edges, so the boundary is visible rather than the button silently vanishing.
- `slotsByDate: StateFlow<Map<LocalDate, List<InterviewSlot>>>` — the existing booking-filtered
  `slots` list (unchanged upstream logic: role's catalog minus the user's existing bookings),
  grouped by local calendar date. Grouping converts each slot's `startEpochMillis` via
  `Instant.ofEpochMilli(...).atZone(ZoneId.systemDefault()).toLocalDate()` — **not** the catalog's
  internal UTC anchoring — so day boundaries match what the user actually sees when the time is
  displayed (a slot generated for 19:00 UTC can fall on the next local calendar day in
  timezones ahead of UTC, and must group under that day, not the UTC one).
- `selectedDate: StateFlow<LocalDate?>` — defaults to the earliest key in `slotsByDate` (i.e. the
  first day with ≥1 open slot) whenever the screen loads or the role changes. Tapping a valid day
  in the grid updates it directly.

To compute the 14-day window's start/end month for clamping `visibleMonth`, the ViewModel also
reads the *raw* (pre-booking-filter) catalog via the same `getAvailableSlots(role)` call already
used today — cheap, in-memory, no new repository method. This determines only the navigable month
range; whether an individual day within that range is tappable is still governed purely by
`slotsByDate` (a day with zero remaining open slots — whether from being fully booked by the user,
in the past, or simply outside the 14-day window — is inert either way).

`selectRole()` (existing) additionally resets `selectedDate` to the new role's first open day and
`visibleMonth` to the month containing it.

## Calendar grid component

New file `InterviewCalendarGrid.kt` in the same `ui/screens/interview/slots/` package. A standard
7-column month grid:

- Header row: `< July 2026 >` with nav arrows, disabled at the clamped window edges per above.
- Weekday label row (Sun–Sat, matching `DateFormat`/locale conventions already used elsewhere in
  the app).
- Day cells filled Sunday-first for the visible month, with blank leading/trailing cells for the
  month's calendar offset (standard grid layout, no slot data behind the blanks).

Each in-month day cell is styled by whether `slotsByDate` has an entry for it:
- **Has ≥1 open slot** → normal text color, filled circle background when it equals
  `selectedDate`, tappable (calls back up to set `selectedDate`).
- **No open slot** → dimmed/muted color (`MaterialTheme.colorScheme.onSurfaceVariant` at reduced
  emphasis, consistent with other disabled-state styling already in the interview screens), not
  tappable.

## Time-slot list, booking, and error handling

Below the grid, for `selectedDate`: up to three rows (Morning/Afternoon/Evening, each showing its
local time, e.g. "8:00 AM") pulled from `slotsByDate[selectedDate]`. Each row is a single
tappable `Card` — the current separate "Book" `TextButton` is removed — calling
`viewModel.bookSlot(slot)` directly. Rows are disabled while `bookingState is
BookingUiState.Booking` to prevent double-taps, matching the existing disable-while-booking
behavior.

If `selectedDate` has no entries (only reachable transiently, e.g. right after a booking removes
that day's last slot before recomposition catches up), show the existing "You've booked all
available `<role>` slots" empty-state message, scoped to the day instead of the whole role.

`BookingUiState.Error` handling is unchanged — the existing inline error `Text` (sign-in required,
too-many-active-bookings, generic failure) stays, shown above the day/time area. Success
(`BookingUiState.Booked`) still triggers `onBooked()` exactly as today; no navigation changes.

## Testing

The project has no coroutines-test dependency and no existing ViewModel-level tests — every test
under `app/src/test/` (`InterviewSlotCatalogTest.kt`, `InterviewMatchDecisionTest.kt`) exercises a
plain top-level function or object, not a `StateFlow`. This design follows that same pattern
rather than introducing a new testing style: the date-grouping and month-clamping math is pulled
out of the ViewModel into a plain object, `InterviewSlotCalendar` (new file, same package as
`InterviewSlotCatalog`), with pure functions:

- `groupByLocalDate(slots: List<InterviewSlot>): Map<LocalDate, List<InterviewSlot>>`
- `clampMonth(requested: YearMonth, today: LocalDate, lastWindowDate: LocalDate): YearMonth`

The ViewModel calls these but contains no logic of its own worth testing beyond that. New test
file `InterviewSlotCalendarTest.kt` covers both functions directly:

- `groupByLocalDate`: a slot generated at 19:00 UTC lands under the correct *local* calendar date
  for a timezone ahead of UTC (the day-boundary-shift case called out above), and an empty input
  produces an empty map (from which "earliest key" naturally yields no default date).
- `clampMonth`: requesting a month before `today`'s month clamps to `today`'s month; requesting a
  month after `lastWindowDate`'s month clamps to `lastWindowDate`'s month; a request already
  inside the window passes through unchanged; and the case where `today` and `lastWindowDate`
  fall in different calendar months (the 14-day window spanning a month boundary) is covered
  explicitly.

## Out of scope

- No new Firestore reads/writes, repository methods, or security rule changes.
- No change to the underlying slot catalog (still fixed 3×/day, 14-day window) or the booking
  transaction/limits (max 3 active bookings, no double-booking) — this is presentation-only.
- No change to the role filter chips above the calendar, or to any other interview screen.
