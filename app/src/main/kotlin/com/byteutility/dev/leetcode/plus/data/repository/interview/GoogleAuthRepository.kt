package com.byteutility.dev.leetcode.plus.data.repository.interview

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

/**
 * Separate Google Sign-In / Firebase Auth identity layer for the mock-interview feature only.
 * Deliberately independent from the existing LeetCode WebView cookie auth in
 * [com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository].
 */
interface GoogleAuthRepository {

    val currentUser: Flow<FirebaseUser?>

    suspend fun signIn(context: Context): Result<FirebaseUser>

    fun signOut()
}
