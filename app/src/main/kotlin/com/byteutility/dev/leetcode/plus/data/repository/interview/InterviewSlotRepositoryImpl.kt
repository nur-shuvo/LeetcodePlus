package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.BookingStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val SLOT_BOOKINGS_COLLECTION = "slotBookings"
private const val CANDIDATES_SUBCOLLECTION = "candidates"

@Singleton
class InterviewSlotRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : InterviewSlotRepository {

    override fun getAvailableSlots(role: InterviewRole): List<InterviewSlot> =
        InterviewSlotCatalog.generateUpcomingSlots(role)

    override suspend fun bookSlot(slot: InterviewSlot) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val slotDocId = InterviewSlotCatalog.slotDocId(slot)
        val data = mapOf(
            "uid" to uid,
            "slotId" to slot.slotId,
            "role" to slot.role.firestoreValue,
            "status" to BookingStatus.WAITING.firestoreValue,
            "startEpochMillis" to slot.startEpochMillis,
            "endEpochMillis" to slot.endEpochMillis,
            "createdAt" to System.currentTimeMillis(),
        )
        firestore.collection(SLOT_BOOKINGS_COLLECTION)
            .document(slotDocId)
            .collection(CANDIDATES_SUBCOLLECTION)
            .document(uid)
            .set(data)
            .await()
    }
}
