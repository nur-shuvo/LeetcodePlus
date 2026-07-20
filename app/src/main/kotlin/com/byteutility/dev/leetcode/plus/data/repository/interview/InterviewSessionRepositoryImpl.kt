package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SessionStatus
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

private const val SESSIONS_COLLECTION = "sessions"
private const val PARTICIPANT_UIDS_FIELD = "participantUids"

@Singleton
class InterviewSessionRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : InterviewSessionRepository {

    override fun getMySessions(): Flow<List<InterviewSession>> = callbackFlow {
        val uid = firebaseAuth.currentUser?.uid
        if (uid == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val registration = firestore.collection(SESSIONS_COLLECTION)
            .whereArrayContains(PARTICIPANT_UIDS_FIELD, uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.documents?.mapNotNull { it.toInterviewSession() } ?: emptyList())
            }

        awaitClose { registration.remove() }
    }

    override fun getSession(sessionId: String): Flow<InterviewSession?> = callbackFlow {
        val registration = firestore.collection(SESSIONS_COLLECTION).document(sessionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toInterviewSession())
            }

        awaitClose { registration.remove() }
    }
}

private fun DocumentSnapshot.toInterviewSession(): InterviewSession? {
    if (!exists()) return null
    return InterviewSession(
        sessionId = getString("sessionId") ?: id,
        uidA = getString("uidA") ?: "",
        uidB = getString("uidB") ?: "",
        participantUids = (get("participantUids") as? List<*>)?.mapNotNull { it as? String }
            ?: emptyList(),
        role = InterviewRole.fromFirestoreValue(getString("role")) ?: InterviewRole.ANDROID,
        startEpochMillis = getLong("startEpochMillis") ?: 0L,
        endEpochMillis = getLong("endEpochMillis") ?: 0L,
        status = SessionStatus.fromFirestoreValue(getString("status")),
        meetLink = getString("meetLink") ?: "",
        calendarEventId = getString("calendarEventId") ?: "",
    )
}
