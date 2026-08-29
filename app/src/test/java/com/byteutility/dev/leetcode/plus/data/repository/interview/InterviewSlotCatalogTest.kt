package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

private const val DAYS_AHEAD = 14
private const val SLOTS_PER_DAY = 3

class InterviewSlotCatalogTest {

    @Test
    fun generateUpcomingSlotsOnlyReturnsFutureSlots() {
        val now = Instant.parse("2026-07-20T12:00:00Z")
        val slots = InterviewSlotCatalog.generateUpcomingSlots(InterviewRole.ANDROID, now)

        assertTrue(slots.isNotEmpty())
        assertTrue(slots.all { it.startEpochMillis > now.toEpochMilli() })
    }

    @Test
    fun generateUpcomingSlotsReturnsThreeSlotsPerDayForFourteenDays() {
        val now = Instant.parse("2026-07-20T00:00:00Z")
        val slots = InterviewSlotCatalog.generateUpcomingSlots(InterviewRole.BACKEND, now)

        assertEquals(DAYS_AHEAD * SLOTS_PER_DAY, slots.size)
    }

    @Test
    fun slotsAreTaggedWithTheRequestedRole() {
        val slots = InterviewSlotCatalog.generateUpcomingSlots(InterviewRole.FRONTEND)
        assertTrue(slots.all { it.role == InterviewRole.FRONTEND })
    }

    @Test
    fun slotDocIdCombinesSlotIdAndRole() {
        val slot = InterviewSlotCatalog.generateUpcomingSlots(InterviewRole.ML).first()
        val docId = InterviewSlotCatalog.slotDocId(slot)

        assertEquals("${slot.slotId}_ml", docId)
    }
}
