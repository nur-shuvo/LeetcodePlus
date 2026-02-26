package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.LeetCodePlusApplication.Companion.appCoroutineScope
import com.byteutility.dev.leetcode.plus.data.datastore.CodeHistoryDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepository
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.byteutility.dev.leetcode.plus.network.responseVo.RunCodeCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class CodeEditorSubmitViewModel @Inject constructor(
    private val codeEditorSubmitRepository: CodeEditorSubmitRepository,
    private val codeHistoryDataStore: CodeHistoryDataStore,
    private val userDatastore: UserDatastore
) : ViewModel() {

    private val _submissionState = MutableStateFlow<SubmissionState>(SubmissionState.Idle)
    val submissionState: StateFlow<SubmissionState> = _submissionState

    private val _runCodeState = MutableStateFlow<RunCodeState>(RunCodeState.Idle)
    val runCodeState: StateFlow<RunCodeState> = _runCodeState

    private val _uiEvent: MutableSharedFlow<CodeEditorSubmitUIEvent?> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    var codeSnippet: CodeSnippet? = null
        private set
    fun setCodeSnippet(codeSnippet: CodeSnippet?){
        this.codeSnippet = codeSnippet
    }

    suspend fun getSavedCode(questionId: String, language: String): String? {
        return codeHistoryDataStore.getCode(questionId, language)
    }

    fun saveCode(questionId: String, language: String, code: String) {
        appCoroutineScope.launch {
            codeHistoryDataStore.saveCode(questionId, language, code)
        }
    }

    suspend fun getCsrfToken() = userDatastore.getLeetcodeCsrfToken()
    suspend fun getSessionToken() = userDatastore.getLeetcodeSessionToken()

    fun submit(titleSlug: String, language: String, code: String, questionId: String) {
        viewModelScope.launch {
            if (getCsrfToken().isNullOrEmpty() || getSessionToken().isNullOrEmpty()) {
                _uiEvent.emit(CodeEditorSubmitUIEvent.NavigateToLeetcodeLogin)
                return@launch
            }
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
            var shouldContinuePolling = true

            while (isActive && shouldContinuePolling) {
                delay(1.seconds)
                try {
                    val result = codeEditorSubmitRepository.getSubmissionResult(submissionId)
                    if (result.state == "SUCCESS") {
                        _submissionState.value = SubmissionState.Success(result)
                        shouldContinuePolling = false
                    }
                } catch (e: Exception) {
                    _submissionState.value = SubmissionState.Error(e)
                    shouldContinuePolling = false
                }
            }
        }
    }

    fun runCode(titleSlug: String, language: String, code: String, questionId: String, testCases: String) {
        viewModelScope.launch {
            if (getCsrfToken().isNullOrEmpty() || getSessionToken().isNullOrEmpty()) {
                _uiEvent.emit(CodeEditorSubmitUIEvent.NavigateToLeetcodeLogin)
                return@launch
            }
            _runCodeState.value = RunCodeState.Running
            try {
                val result = codeEditorSubmitRepository.interpretSolution(titleSlug, language, code, questionId, testCases)
                pollForRunCodeResult(result.interpretId, testCases)
            } catch (e: Exception) {
                _runCodeState.value = RunCodeState.Error(e)
            }
        }
    }

    private fun pollForRunCodeResult(interpretId: String, dataInput: String) {
        viewModelScope.launch {
            var shouldContinuePolling = true

            while (isActive && shouldContinuePolling) {
                delay(1.seconds)
                try {
                    val result = codeEditorSubmitRepository.getRunCodeResult(interpretId)
                    if (result.state == "SUCCESS") {
                        _runCodeState.value = RunCodeState.Success(result, dataInput)
                        shouldContinuePolling = false
                    }
                } catch (e: Exception) {
                    _runCodeState.value = RunCodeState.Error(e)
                    shouldContinuePolling = false
                }
            }
        }
    }

    fun resetCode() = viewModelScope.launch {
        _uiEvent.emit(CodeEditorSubmitUIEvent.ResetCode(codeSnippet))
    }
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    data class Success(val response: SubmissionCheckResponse) : SubmissionState()
    data class Error(val exception: Exception) : SubmissionState()
}

sealed class RunCodeState {
    object Idle : RunCodeState()
    object Running : RunCodeState()
    data class Success(val response: RunCodeCheckResponse, val dataInput: String) : RunCodeState()
    data class Error(val exception: Exception) : RunCodeState()
}

sealed interface CodeEditorSubmitUIEvent {
    object NavigateToLeetcodeLogin : CodeEditorSubmitUIEvent
    data class ResetCode(val codeSnippet: CodeSnippet?) : CodeEditorSubmitUIEvent
}
