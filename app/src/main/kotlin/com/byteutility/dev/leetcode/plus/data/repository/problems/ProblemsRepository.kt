package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.OfficialSolutionResponse

interface ProblemsRepository {
    suspend fun getProblems(limit: Long): List<LeetCodeProblem>
    suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse
    suspend fun getOfficialSolution(titleSlug: String): OfficialSolutionResponse
}
