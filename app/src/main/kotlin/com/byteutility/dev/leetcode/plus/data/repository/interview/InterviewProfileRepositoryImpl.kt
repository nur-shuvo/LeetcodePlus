package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewProfile
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

private const val INTERVIEW_PROFILES_COLLECTION = "interviewProfiles"

@Singleton
class InterviewProfileRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
) : InterviewProfileRepository {

    private fun profilesCollection() = firestore.collection(INTERVIEW_PROFILES_COLLECTION)

    override fun getMyProfile(): Flow<InterviewProfile?> {
        val uid = firebaseAuth.currentUser?.uid ?: return flowOf(null)
        return getProfile(uid)
    }

    override fun getProfile(uid: String): Flow<InterviewProfile?> = callbackFlow {
        val registration = profilesCollection().document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toInterviewProfile())
            }

        awaitClose { registration.remove() }
    }

    override suspend fun saveProfile(profile: InterviewProfile) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val data = mapOf(
            "uid" to uid,
            "displayName" to profile.displayName,
            "email" to profile.email,
            "leetcodeHandle" to profile.leetcodeHandle,
            "roles" to profile.roles.map { it.firestoreValue },
            "timeZoneId" to profile.timeZoneId,
        )
        profilesCollection().document(uid).set(data, SetOptions.merge()).await()
    }

    override suspend fun updateFcmToken(token: String) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        profilesCollection().document(uid)
            .set(mapOf("fcmToken" to token), SetOptions.merge())
            .await()
    }
}

private fun DocumentSnapshot.toInterviewProfile(): InterviewProfile? {
    if (!exists()) return null
    val rolesRaw = get("roles") as? List<*> ?: emptyList<Any>()
    return InterviewProfile(
        uid = getString("uid") ?: id,
        displayName = getString("displayName") ?: "",
        email = getString("email") ?: "",
        leetcodeHandle = getString("leetcodeHandle") ?: "",
        roles = rolesRaw.mapNotNull { InterviewRole.fromFirestoreValue(it as? String) },
        timeZoneId = getString("timeZoneId") ?: "",
        fcmToken = getString("fcmToken") ?: "",
    )
}
