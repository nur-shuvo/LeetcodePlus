package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewProfile
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import com.byteutility.dev.leetcode.plus.ui.navigation.InterviewSessionDetail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InterviewSessionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    interviewSessionRepository: InterviewSessionRepository,
    interviewProfileRepository: InterviewProfileRepository,
    googleAuthRepository: GoogleAuthRepository,
) : ViewModel() {

    private val sessionId = savedStateHandle.toRoute<InterviewSessionDetail>().sessionId

    val session: StateFlow<InterviewSession?> = interviewSessionRepository.getSession(sessionId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    val peerProfile: StateFlow<InterviewProfile?> = combine(
        session,
        googleAuthRepository.currentUser
    ) { currentSession, user -> currentSession to user?.uid }
        .flatMapLatest { (currentSession, myUid) ->
            if (currentSession == null || myUid == null) {
                flowOf(null)
            } else {
                interviewProfileRepository.getProfile(currentSession.peerUid(myUid))
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)
}
