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

    @Test
    fun monthGridDatesHasSixWeeksForAugust2026WhichStartsOnASaturday() {
        // Verified: August 1, 2026 is a Saturday, so the Sunday-first grid has 6 leading blanks
        // (Su-Fr) before day 1, giving 6 + 31 = 37 cells -> 6 weeks, unlike July 2026's 5 weeks.
        val grid = InterviewSlotCalendar.monthGridDates(YearMonth.of(2026, 8))

        assertEquals(6, grid.size)
        assertTrue(grid.all { it.size == 7 })
    }

    @Test
    fun groupByLocalDateGroupsMultipleSlotsFallingOnTheSameLocalDate() {
        val zone = ZoneId.of("UTC")
        val slot1 = InterviewSlot(
            slotId = "slot1",
            role = InterviewRole.ANDROID,
            startEpochMillis = Instant.parse("2026-07-22T09:00:00Z").toEpochMilli(),
            endEpochMillis = Instant.parse("2026-07-22T10:00:00Z").toEpochMilli(),
        )
        val slot2 = InterviewSlot(
            slotId = "slot2",
            role = InterviewRole.ANDROID,
            startEpochMillis = Instant.parse("2026-07-22T14:00:00Z").toEpochMilli(),
            endEpochMillis = Instant.parse("2026-07-22T15:00:00Z").toEpochMilli(),
        )
        val slot3 = InterviewSlot(
            slotId = "slot3",
            role = InterviewRole.ANDROID,
            startEpochMillis = Instant.parse("2026-07-22T20:00:00Z").toEpochMilli(),
            endEpochMillis = Instant.parse("2026-07-22T21:00:00Z").toEpochMilli(),
        )

        val grouped = InterviewSlotCalendar.groupByLocalDate(listOf(slot1, slot2, slot3), zone)

        assertEquals(setOf(LocalDate.of(2026, 7, 22)), grouped.keys)
        assertEquals(listOf(slot1, slot2, slot3), grouped.getValue(LocalDate.of(2026, 7, 22)))
    }
}
