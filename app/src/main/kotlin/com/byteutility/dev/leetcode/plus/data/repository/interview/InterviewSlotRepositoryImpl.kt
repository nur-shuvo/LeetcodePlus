package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.BookingStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.byteutility.dev.leetcode.plus.data.model.interview.SessionStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Transaction
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val SLOT_BOOKINGS_COLLECTION = "slotBookings"
private const val CANDIDATES_SUBCOLLECTION = "candidates"
private const val SESSIONS_COLLECTION = "sessions"
private const val STATUS_FIELD = "status"
private const val UID_FIELD = "uid"

@Singleton
class InterviewSlotRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : InterviewSlotRepository {

    override fun getAvailableSlots(role: InterviewRole): List<InterviewSlot> =
        InterviewSlotCatalog.generateUpcomingSlots(role)

    /**
     * Books [slot] and, if it completes a pair, matches the two candidates and creates the
     * session - all inside one Firestore transaction.
     *
     * The Firestore Android SDK's `Transaction.get()` only accepts a `DocumentReference`, not a
     * `Query`, so the sibling candidate can't be discovered from inside the transaction. Instead
     * a plain (non-transactional) query finds *which* document might be a peer, and the
     * transaction re-reads that specific document to get its authoritative state at commit time -
     * that re-read is what prevents two near-simultaneous bookings from both matching against a
     * peer that already matched with someone else (see [decideMatch]).
     *
     * The own candidate doc is written with exactly one `tx.set()` call - never a `set()` +
     * `update()` pair on the same document. Firestore evaluates a transaction's security rules
     * against the *final* coalesced value of each document, so writing "waiting" then
     * immediately updating the same doc to "matched" gets evaluated as a single create with
     * `status == 'matched'`, which fails a rule that only allows creating as "waiting"
     * (confirmed against the Firestore emulator - this crashed with PERMISSION_DENIED).
     *
     * Re-booking a slot the caller already has a doc for is a no-op (see `ownSnapshot.exists()`
     * below). A genuinely new booking is rejected with [TooManyActiveBookingsException] if the
     * caller already has [MAX_ACTIVE_BOOKINGS_PER_USER] upcoming (not-yet-ended) bookings - this
     * is enforced client-side only, not by `firestore.rules`, since Firestore Security Rules
     * can't count/aggregate across a query. Same accepted trust trade-off as the rest of the
     * client-side matching design.
     */
    override suspend fun bookSlot(slot: InterviewSlot) {
        val uid = firebaseAuth.currentUser?.uid ?: throw NotSignedInException()
        val slotDocId = InterviewSlotCatalog.slotDocId(slot)
        val candidatesRef = firestore.collection(SLOT_BOOKINGS_COLLECTION)
            .document(slotDocId)
            .collection(CANDIDATES_SUBCOLLECTION)
        val ownRef = candidatesRef.document(uid)
        val candidatePeerId = candidatesRef.get().await().documents
            .firstOrNull { it.id != uid }
            ?.id
        val upcomingBookingCount = countUpcomingBookings(uid)

        firestore.runTransaction<Void?> { tx ->
            val ownSnapshot = tx.get(ownRef)
            val peerCandidate = candidatePeerId
                ?.let { tx.get(candidatesRef.document(it)) }
                ?.takeIf { it.exists() }
                ?.toCandidateDoc()

            if (!ownSnapshot.exists()) {
                if (upcomingBookingCount >= MAX_ACTIVE_BOOKINGS_PER_USER) {
                    throw TooManyActiveBookingsException()
                }
                val ownBooking = SlotBooking(
                    uid = uid,
                    slotId = slot.slotId,
                    role = slot.role,
                    status = BookingStatus.WAITING,
                    startEpochMillis = slot.startEpochMillis,
                    endEpochMillis = slot.endEpochMillis,
                    createdAt = System.currentTimeMillis(),
                )
                val decision = peerCandidate?.let {
                    decideMatch(listOf(it, CandidateDoc(uid, ownBooking)), uid)
                }
                if (decision != null) {
                    applyMatch(tx, candidatesRef, decision, ownBooking, slotDocId)
                } else {
                    tx.set(ownRef, ownBooking.toFirestoreMap())
                }
            }
            null
        }.await()
    }

    /** Counts the caller's own candidate docs (any status, any role) whose slot hasn't ended
     * yet - deliberately not restricted to `waiting`, since a `matched` booking still occupies
     * one of the [MAX_ACTIVE_BOOKINGS_PER_USER] slots until its time passes. */
    private suspend fun countUpcomingBookings(uid: String): Int {
        val now = System.currentTimeMillis()
        return firestore.collectionGroup(CANDIDATES_SUBCOLLECTION)
            .whereEqualTo(UID_FIELD, uid)
            .get()
            .await()
            .documents
            .count { (it.getLong("endEpochMillis") ?: 0L) > now }
    }

    override fun getMyBookings(): Flow<List<SlotBooking>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collectionGroup(CANDIDATES_SUBCOLLECTION)
            .whereEqualTo(UID_FIELD, uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.map { it.toCandidateDoc().data } ?: emptyList())
            }

        awaitClose { registration.remove() }
    }

    private fun applyMatch(
        tx: Transaction,
        candidatesRef: CollectionReference,
        decision: MatchDecision,
        newBooking: SlotBooking,
        sessionId: String,
    ) {
        tx.set(
            candidatesRef.document(decision.newCandidateId),
            newBooking.copy(status = BookingStatus.MATCHED).toFirestoreMap(),
        )
        tx.update(candidatesRef.document(decision.peerId), STATUS_FIELD, BookingStatus.MATCHED.firestoreValue)

        val sessionData = mapOf(
            "sessionId" to sessionId,
            "uidA" to decision.peerId,
            "uidB" to decision.newCandidateId,
            "participantUids" to listOf(decision.peerId, decision.newCandidateId),
            "role" to newBooking.role.firestoreValue,
            "startEpochMillis" to newBooking.startEpochMillis,
            "endEpochMillis" to newBooking.endEpochMillis,
            "status" to SessionStatus.MATCHED.firestoreValue,
        )
        tx.set(firestore.collection(SESSIONS_COLLECTION).document(sessionId), sessionData)
    }
}

private fun SlotBooking.toFirestoreMap(): Map<String, Any> = mapOf(
    "uid" to uid,
    "slotId" to slotId,
    "role" to role.firestoreValue,
    "status" to status.firestoreValue,
    "startEpochMillis" to startEpochMillis,
    "endEpochMillis" to endEpochMillis,
    "createdAt" to createdAt,
)

private fun DocumentSnapshot.toCandidateDoc(): CandidateDoc = CandidateDoc(
    id = id,
    data = SlotBooking(
        uid = getString("uid") ?: id,
        slotId = getString("slotId") ?: "",
        role = InterviewRole.fromFirestoreValue(getString("role")) ?: InterviewRole.ANDROID,
        status = BookingStatus.fromFirestoreValue(getString(STATUS_FIELD)),
        startEpochMillis = getLong("startEpochMillis") ?: 0L,
        endEpochMillis = getLong("endEpochMillis") ?: 0L,
        createdAt = getLong("createdAt") ?: 0L,
    ),
)
