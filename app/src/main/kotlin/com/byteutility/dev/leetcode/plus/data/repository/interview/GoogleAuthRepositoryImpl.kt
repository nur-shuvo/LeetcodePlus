package com.byteutility.dev.leetcode.plus.data.repository.interview

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleAuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
) : GoogleAuthRepository {

    override val currentUser: Flow<FirebaseUser?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth -> trySend(auth.currentUser) }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    override suspend fun signIn(context: Context): Result<FirebaseUser> = runCatching {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(defaultWebClientId(context))
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        val response = CredentialManager.create(context).getCredential(context, request)
        val credential = response.credential
        check(
            credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) { "Unexpected credential type returned from Credential Manager" }

        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
        val firebaseCredential = GoogleAuthProvider.getCredential(
            googleIdTokenCredential.idToken,
            null
        )
        val authResult = firebaseAuth.signInWithCredential(firebaseCredential).await()
        authResult.user ?: error("Firebase sign-in returned no user")
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }

    private fun defaultWebClientId(context: Context): String {
        val resId = context.resources.getIdentifier(
            "default_web_client_id",
            "string",
            context.packageName
        )
        check(resId != 0) {
            "default_web_client_id resource not found - has google-services.json been added?"
        }
        return context.getString(resId)
    }
}
