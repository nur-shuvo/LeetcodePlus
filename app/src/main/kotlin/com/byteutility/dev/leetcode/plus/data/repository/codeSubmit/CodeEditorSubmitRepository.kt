package com.byteutility.dev.leetcode.plus.data.repository.codeSubmit

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
}