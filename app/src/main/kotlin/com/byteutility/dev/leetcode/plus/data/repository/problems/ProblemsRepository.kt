package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.OfficialSolutionResponse

interface ProblemsRepository {
    suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse
    suspend fun getOfficialSolution(titleSlug: String): OfficialSolutionResponse
    suspend fun getRemoteProblems():Result<Unit>
}
