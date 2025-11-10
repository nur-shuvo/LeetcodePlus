package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepositoryImpl
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CodeEditorSubmitViewModel @Inject constructor(
    private val codeEditorSubmitRepository: CodeEditorSubmitRepositoryImpl
) : ViewModel() {

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState

    fun submit(titleSlug: String, language: String, code: String, questionId: String) {
        viewModelScope.launch {
            _submissionState.value = SubmissionState.Submitting
            try {
                val result =
                    codeEditorSubmitRepository.submit(titleSlug, language, code, questionId)
                pollForSubmissionResult(result.submissionId)
            } catch (e: Exception) {
                _submissionState.value = SubmissionState.Error(e)
            }
        }
    }

    private fun pollForSubmissionResult(submissionId: Long) {
        viewModelScope.launch {
            while (isActive) {
                delay(1.seconds)
                try {
                    val result = codeEditorSubmitRepository.getSubmissionResult(submissionId)
                    if (result.state == SUCCESS_STATE) {
                        _submissionState.value = SubmissionState.Success(result)
                        break
                    }
                } catch (e: Exception) {
                    _submissionState.value = SubmissionState.Error(e)
                    break
                }
            }
        }
    }

    companion object {
        private const val SUCCESS_STATE = "SUCCESS"
    }
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    data class Success(val response: SubmissionCheckResponse) : SubmissionState()
    data class Error(val exception: Exception) : SubmissionState()
}
