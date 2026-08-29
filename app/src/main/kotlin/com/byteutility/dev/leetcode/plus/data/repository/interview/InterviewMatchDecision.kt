package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.BookingStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking

const val MAX_CANDIDATES_PER_SLOT = 2

data class CandidateDoc(val id: String, val data: SlotBooking)

data class MatchDecision(val peerId: String, val newCandidateId: String)

/**
 * Pure matching logic, ported from the (now-removed) Cloud Function's matchDecision.ts so it can
 * run inside an on-device Firestore transaction instead. `candidates` must be every doc currently
 * in the slot's candidates subcollection, read inside the enclosing transaction (not before it) -
 * that's what prevents two near-simultaneous bookings from both seeing "no peer yet".
 */
fun decideMatch(candidates: List<CandidateDoc>, newCandidateId: String): MatchDecision? {
    val newCandidate = candidates.find { it.id == newCandidateId }
    val waiting = candidates.filter { it.data.status == BookingStatus.WAITING }
    val peer = waiting.filter { it.id != newCandidateId }.minByOrNull { it.data.createdAt }

    val eligible = newCandidate?.data?.status == BookingStatus.WAITING &&
        waiting.size <= MAX_CANDIDATES_PER_SLOT

    return peer.takeIf { eligible }?.let { MatchDecision(peerId = it.id, newCandidateId = newCandidateId) }
}
