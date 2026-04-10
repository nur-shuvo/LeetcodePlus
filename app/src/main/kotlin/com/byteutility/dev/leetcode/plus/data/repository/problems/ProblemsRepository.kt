package com.byteutility.dev.leetcode.plus.data.repository.problems

import androidx.paging.PagingSource
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.OfficialSolutionResponse

interface ProblemsRepository {
    fun getProblems(query: String?, difficulty: List<String>?, tags: List<String>?): PagingSource<Int, ProblemEntity>
    suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse
    suspend fun getOfficialSolution(titleSlug: String): OfficialSolutionResponse
    suspend fun getRemoteProblems():Result<Unit>
    suspend fun getAllTags(): List<String>
    suspend fun getDifficulty(): List<String>
}
