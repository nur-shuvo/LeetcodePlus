package com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model

data class ProblemDetailsUiState(
    val questionId: String = "",
    val title: String = "",
    val difficulty: String = "",
    val category: String = "",
    val content: String = "",
    val codeSnippets: List<CodeSnippet> = emptyList(),
    val isPremiumContent: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
