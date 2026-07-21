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
