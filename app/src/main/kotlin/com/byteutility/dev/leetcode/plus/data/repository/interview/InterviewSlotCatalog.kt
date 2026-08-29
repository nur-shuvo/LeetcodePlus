package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Fixed slot catalog: 3 times/day for the next 14 days, generated deterministically so the
 * client doesn't need a round-trip to read a server-maintained catalog. Both peers booking the
 * same role at the same wall-clock time land on the same Firestore slot document id.
 */
object InterviewSlotCatalog {
    private const val DAYS_AHEAD = 14
    private const val MORNING_SLOT_HOUR_UTC = 8
    private const val AFTERNOON_SLOT_HOUR_UTC = 13
    private const val EVENING_SLOT_HOUR_UTC = 19
    private val DAILY_HOURS_UTC =
        listOf(MORNING_SLOT_HOUR_UTC, AFTERNOON_SLOT_HOUR_UTC, EVENING_SLOT_HOUR_UTC)
    private const val SLOT_DURATION_MINUTES = 60L

    fun generateUpcomingSlots(
        role: InterviewRole,
        now: Instant = Instant.now()
    ): List<InterviewSlot> {
        val today = now.atZone(ZoneOffset.UTC).toLocalDate()
        return (0 until DAYS_AHEAD).flatMap { dayOffset ->
            val day = today.plusDays(dayOffset.toLong())
            DAILY_HOURS_UTC.mapNotNull { hour ->
                val start = day.atTime(hour, 0).atZone(ZoneOffset.UTC).toInstant()
                if (start.isBefore(now)) return@mapNotNull null
                val end = start.plus(SLOT_DURATION_MINUTES, ChronoUnit.MINUTES)
                InterviewSlot(
                    slotId = start.toEpochMilli().toString(),
                    role = role,
                    startEpochMillis = start.toEpochMilli(),
                    endEpochMillis = end.toEpochMilli(),
                )
            }
        }
    }

    fun slotDocId(slot: InterviewSlot): String = "${slot.slotId}_${slot.role.firestoreValue}"
}
