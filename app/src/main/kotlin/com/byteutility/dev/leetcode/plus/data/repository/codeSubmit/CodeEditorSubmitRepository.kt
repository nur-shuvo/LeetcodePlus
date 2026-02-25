package com.byteutility.dev.leetcode.plus.data.repository.codeSubmit

import com.byteutility.dev.leetcode.plus.network.responseVo.InterpretSolutionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.RunCodeCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmitLeetcodeProblemResponse

/**
 * Created by Shuvo on 11/10/2025.
 */
interface CodeEditorSubmitRepository {
    suspend fun submit(
        titleSlug: String,
        language: String,
        code: String,
        questionId: String
    ): SubmitLeetcodeProblemResponse

    suspend fun getSubmissionResult(submissionId: Long): SubmissionCheckResponse

    suspend fun interpretSolution(
        titleSlug: String,
        language: String,
        code: String,
        questionId: String,
        testCases: String
    ): InterpretSolutionResponse

    suspend fun getRunCodeResult(interpretId: String): RunCodeCheckResponse
}
