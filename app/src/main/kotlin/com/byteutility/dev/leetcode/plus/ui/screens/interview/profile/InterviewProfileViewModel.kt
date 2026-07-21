package com.byteutility.dev.leetcode.plus.ui.screens.interview.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewProfile
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepository
import com.byteutility.dev.leetcode.plus.data.worker.InterviewSessionWorker
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.TimeZone
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

@HiltViewModel
class InterviewProfileViewModel @Inject constructor(
    private val googleAuthRepository: GoogleAuthRepository,
    private val interviewProfileRepository: InterviewProfileRepository,
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = googleAuthRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    val profile: StateFlow<InterviewProfile?> = interviewProfileRepository.getMyProfile()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    private val _isSigningIn = MutableStateFlow(false)
    val isSigningIn: StateFlow<Boolean> = _isSigningIn.asStateFlow()

    fun signIn(context: Context) {
        viewModelScope.launch {
            _isSigningIn.value = true
            val result = googleAuthRepository.signIn(context)
            _isSigningIn.value = false
            result.onSuccess { user ->
                // Fetch fresh rather than trusting this ViewModel's own `profile` StateFlow -
                // it was subscribed before sign-in completed, so it's the pre-sign-in (null)
                // snapshot. Using that here would wipe a returning user's saved roles.
                val existing = interviewProfileRepository.getProfile(user.uid).first()
                interviewProfileRepository.saveProfile(
                    InterviewProfile(
                        uid = user.uid,
                        displayName = user.displayName.orEmpty(),
                        email = user.email.orEmpty(),
                        leetcodeHandle = existing?.leetcodeHandle.orEmpty(),
                        roles = existing?.roles ?: emptyList(),
                        timeZoneId = TimeZone.getDefault().id,
                    )
                )
                InterviewSessionWorker.enqueuePeriodicWork(context.applicationContext)
            }
        }
    }

    fun saveRoles(roles: List<InterviewRole>, leetcodeHandle: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            interviewProfileRepository.saveProfile(
                InterviewProfile(
                    uid = user.uid,
                    displayName = user.displayName.orEmpty(),
                    email = user.email.orEmpty(),
                    leetcodeHandle = leetcodeHandle,
                    roles = roles,
                    timeZoneId = TimeZone.getDefault().id,
                )
            )
        }
    }

    fun signOut() {
        googleAuthRepository.signOut()
    }
}
