package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.BookingStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

private fun booking(uid: String, createdAt: Long, status: BookingStatus = BookingStatus.WAITING) =
    SlotBooking(
        uid = uid,
        slotId = "slot1",
        role = InterviewRole.ANDROID,
        status = status,
        startEpochMillis = 0L,
        endEpochMillis = 0L,
        createdAt = createdAt,
    )

private fun candidate(id: String, createdAt: Long, status: BookingStatus = BookingStatus.WAITING) =
    CandidateDoc(id, booking(id, createdAt, status))

class InterviewMatchDecisionTest {

    @Test
    fun returnsNullWhenNewCandidateIsOnlyOneWaiting() {
        val candidates = listOf(candidate("a", createdAt = 1))
        assertNull(decideMatch(candidates, "a"))
    }

    @Test
    fun matchesNewCandidateWithEarliestOtherWaitingCandidate() {
        val candidates = listOf(candidate("a", createdAt = 1), candidate("b", createdAt = 2))
        assertEquals(MatchDecision(peerId = "a", newCandidateId = "b"), decideMatch(candidates, "b"))
    }

    @Test
    fun preventsDoubleMatchingWhenNewCandidateAlreadyMatchedByConcurrentRun() {
        val candidates = listOf(
            candidate("a", createdAt = 1),
            candidate("b", createdAt = 2, status = BookingStatus.MATCHED),
        )
        assertNull(decideMatch(candidates, "b"))
    }

    @Test
    fun returnsNullWhenSlotIsAlreadyFull() {
        val candidates = listOf(
            candidate("a", createdAt = 1),
            candidate("b", createdAt = 2),
            candidate("c", createdAt = 3),
        )
        assertNull(decideMatch(candidates, "c"))
    }

    @Test
    fun returnsNullWhenNewCandidateDocumentIsMissing() {
        val candidates = listOf(candidate("a", createdAt = 1))
        assertNull(decideMatch(candidates, "ghost"))
    }
}
