package com.byteutility.dev.leetcode.plus.domain.model

enum class ExclusiveProblemSetType(val fileName: String) {
    Blind75("blind_75.json"),
    NeetCode150("neetcode_150.json"),
}

sealed interface ProblemSetType {
    val displayName: String

    data class ExclusiveProblemSet(
        override val displayName: String,
        val type: ExclusiveProblemSetType,
    ) : ProblemSetType

    data class UserDefinedProblemSet(
        override val displayName: String,
        val fileName: String,
    ) : ProblemSetType
}
