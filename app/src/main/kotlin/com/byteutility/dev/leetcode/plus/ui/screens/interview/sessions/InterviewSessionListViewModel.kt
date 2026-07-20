package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

@HiltViewModel
class InterviewSessionListViewModel @Inject constructor(
    interviewSessionRepository: InterviewSessionRepository,
) : ViewModel() {

    val sessions: StateFlow<List<InterviewSession>> = interviewSessionRepository.getMySessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyList())
}
