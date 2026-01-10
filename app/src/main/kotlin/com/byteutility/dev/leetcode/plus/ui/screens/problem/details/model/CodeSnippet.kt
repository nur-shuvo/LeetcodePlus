package com.byteutility.dev.leetcode.plus.ui.screens.problem.details.model

import kotlinx.serialization.Serializable

@Serializable
data class CodeSnippet(
    val lang: String,
    val langSlug: String,
    val code: String
)
