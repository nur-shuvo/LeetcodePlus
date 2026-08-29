package com.byteutility.dev.leetcode.plus.ui.screens.interview.feedback

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewFeedback
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewFeedbackRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.byteutility.dev.leetcode.plus.ui.navigation.InterviewFeedback as InterviewFeedbackRoute

sealed interface SubmitUiState {
    data object Idle : SubmitUiState
    data object Submitting : SubmitUiState
    data object Submitted : SubmitUiState
    data object ValidationError : SubmitUiState
    data object Error : SubmitUiState
}

@HiltViewModel
class InterviewFeedbackViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val interviewFeedbackRepository: InterviewFeedbackRepository,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<InterviewFeedbackRoute>()
    val sessionId: String = route.sessionId
    val rateeUid: String = route.rateeUid

    private val _submitState = MutableStateFlow<SubmitUiState>(SubmitUiState.Idle)
    val submitState: StateFlow<SubmitUiState> = _submitState.asStateFlow()

    fun submit(
        communicationRating: Int,
        problemSolvingRating: Int,
        wouldMatchAgain: Boolean,
        notes: String,
        didNotShowUp: Boolean
    ) {
        if (!didNotShowUp && (communicationRating == 0 || problemSolvingRating == 0)) {
            _submitState.value = SubmitUiState.ValidationError
            return
        }
        viewModelScope.launch {
            _submitState.value = SubmitUiState.Submitting
            runCatching {
                interviewFeedbackRepository.submitFeedback(
                    sessionId,
                    InterviewFeedback(
                        rateeUid = rateeUid,
                        communicationRating = communicationRating,
                        problemSolvingRating = problemSolvingRating,
                        wouldMatchAgain = wouldMatchAgain,
                        notes = notes,
                        didNotShowUp = didNotShowUp,
                    )
                )
            }.onSuccess {
                _submitState.value = SubmitUiState.Submitted
            }.onFailure {
                _submitState.value = SubmitUiState.Error
            }
        }
    }
}
