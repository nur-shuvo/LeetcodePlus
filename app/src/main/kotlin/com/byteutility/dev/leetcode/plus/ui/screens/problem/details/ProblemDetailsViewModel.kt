package com.byteutility.dev.leetcode.plus.ui.screens.problem.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.ui.navigation.ProblemDetails
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.CodeSnippet
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model.ProblemDetailsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProblemDetailsViewModel @Inject constructor(
    private val repository: ProblemsRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val route = savedStateHandle.toRoute<ProblemDetails>()

    private val _uiState = MutableStateFlow(ProblemDetailsUiState())
    val uiState = _uiState.asStateFlow()

    private fun getQuestionDetails(titleSlug: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val result = repository.getSelectedRawQuestion(titleSlug)
                _uiState.update { state ->
                    state.copy(
                        questionId = result.question.questionId,
                        title = result.question.title,
                        difficulty = result.question.difficulty,
                        category = result.question.topicTags.joinToString(", ") { topicTag ->
                            topicTag.name
                        },
                        content = result.question.content ?: "",
                        codeSnippets = result.question.codeSnippets?.map {
                            CodeSnippet(
                                lang = it.lang,
                                langSlug = it.langSlug,
                                code = it.code
                            )
                        } ?: emptyList(),
                        isPremiumContent = result.question.content == null && result.question.codeSnippets == null && result.question.isPaidOnly,
                        isLoading = false,
                        exampleTestcases = result.question.exampleTestcases
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }

    init {
        getQuestionDetails(route.titleSlug)
    }
}
