package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.LeetCodePlusApplication.Companion.appCoroutineScope
import com.byteutility.dev.leetcode.plus.data.datastore.CodeHistoryDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.repository.codeSubmit.CodeEditorSubmitRepository
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.utils.PrefKey
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

    private val _uiEvent: MutableSharedFlow<CodeEditorSubmitUIEvent?> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

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

    fun saveInitialCode(code: String) = viewModelScope.launch {
        codeHistoryDataStore.saveValue(
            key = PrefKey.INITIAL_CODE,
            value = code
        )
    }

    fun resetCode() = viewModelScope.launch {
        val code = codeHistoryDataStore.getValue(PrefKey.INITIAL_CODE, "")
        _uiEvent.emit(CodeEditorSubmitUIEvent.ResetCode(code))
    }

    fun clearAllLocalData() = viewModelScope.launch {
        codeHistoryDataStore.clearAll()
    }
}

sealed class SubmissionState {
    object Idle : SubmissionState()
    object Submitting : SubmissionState()
    data class Success(val response: SubmissionCheckResponse) : SubmissionState()
    data class Error(val exception: Exception) : SubmissionState()
}

sealed interface CodeEditorSubmitUIEvent {
    object NavigateToLeetcodeLogin : CodeEditorSubmitUIEvent
    data class ResetCode(val initialCode: String?) : CodeEditorSubmitUIEvent
}
