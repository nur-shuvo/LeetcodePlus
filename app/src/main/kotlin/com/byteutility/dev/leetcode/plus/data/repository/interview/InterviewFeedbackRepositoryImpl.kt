package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewFeedback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val SESSIONS_COLLECTION = "sessions"
private const val FEEDBACK_SUBCOLLECTION = "feedback"

@Singleton
class InterviewFeedbackRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : InterviewFeedbackRepository {

    override suspend fun submitFeedback(sessionId: String, feedback: InterviewFeedback) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val data = mapOf(
            "raterUid" to uid,
            "rateeUid" to feedback.rateeUid,
            "communicationRating" to feedback.communicationRating,
            "problemSolvingRating" to feedback.problemSolvingRating,
            "wouldMatchAgain" to feedback.wouldMatchAgain,
            "notes" to feedback.notes,
            "didNotShowUp" to feedback.didNotShowUp,
            "submittedAt" to System.currentTimeMillis(),
        )
        firestore.collection(SESSIONS_COLLECTION).document(sessionId)
            .collection(FEEDBACK_SUBCOLLECTION).document(uid)
            .set(data)
            .await()
    }

    override fun getFeedbackForSession(sessionId: String): Flow<List<InterviewFeedback>> =
        callbackFlow {
            val registration = firestore.collection(SESSIONS_COLLECTION).document(sessionId)
                .collection(FEEDBACK_SUBCOLLECTION)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    trySend(
                        snapshot?.documents?.mapNotNull { it.toInterviewFeedback() } ?: emptyList()
                    )
                }

            awaitClose { registration.remove() }
        }
}

private fun DocumentSnapshot.toInterviewFeedback(): InterviewFeedback? {
    if (!exists()) return null
    return InterviewFeedback(
        raterUid = getString("raterUid") ?: id,
        rateeUid = getString("rateeUid") ?: "",
        communicationRating = (getLong("communicationRating") ?: 0L).toInt(),
        problemSolvingRating = (getLong("problemSolvingRating") ?: 0L).toInt(),
        wouldMatchAgain = getBoolean("wouldMatchAgain") ?: false,
        notes = getString("notes") ?: "",
        didNotShowUp = getBoolean("didNotShowUp") ?: false,
        submittedAt = getLong("submittedAt") ?: 0L,
    )
}
