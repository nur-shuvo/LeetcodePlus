package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse

interface ProblemsRepository {
    suspend fun getProblems(type: ProblemSetType? = null): List<LeetCodeProblem>
    suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse
}
