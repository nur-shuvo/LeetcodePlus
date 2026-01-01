package com.byteutility.dev.leetcode.plus.domain.model

sealed interface ProblemSetType {
    val displayName: String

    data class PredefinedProblemSet(val metadata: SetMetadata) : ProblemSetType {
        override val displayName: String = metadata.name
    }

    data class UserDefinedProblemSet(
        override val displayName: String,
        val fileName: String,
    ) : ProblemSetType
}
