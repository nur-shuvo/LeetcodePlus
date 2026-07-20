package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import kotlinx.coroutines.flow.Flow

/** Thrown by [InterviewSlotRepository.bookSlot] when there's no signed-in Google account. */
class NotSignedInException : IllegalStateException("Sign in with Google to book a mock interview")

/** A user may not have more than this many upcoming (not-yet-ended) bookings at once. */
const val MAX_ACTIVE_BOOKINGS_PER_USER = 3

/** Thrown by [InterviewSlotRepository.bookSlot] when the user already has
 * [MAX_ACTIVE_BOOKINGS_PER_USER] upcoming bookings and this would be a new one (not a re-tap on
 * a slot they've already booked). */
class TooManyActiveBookingsException : IllegalStateException(
    "You already have $MAX_ACTIVE_BOOKINGS_PER_USER upcoming mock interviews - " +
        "finish or wait for one before booking another."
)

interface InterviewSlotRepository {

    fun getAvailableSlots(role: InterviewRole): List<InterviewSlot>

    suspend fun bookSlot(slot: InterviewSlot)

    /** The signed-in user's own candidate docs across all slots - includes bookings still
     * waiting for a peer (with no matching Cloud Function anymore, this is the only place
     * that state is visible). */
    fun getMyBookings(): Flow<List<SlotBooking>>
}
