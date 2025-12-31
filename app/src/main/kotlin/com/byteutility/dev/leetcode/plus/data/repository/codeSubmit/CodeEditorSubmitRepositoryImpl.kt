package com.byteutility.dev.leetcode.plus.data.repository.codeSubmit

import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.requestVO.ProblemSubmitRequest
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmitLeetcodeProblemResponse
import javax.inject.Inject

class CodeEditorSubmitRepositoryImpl @Inject constructor(
    private val restApiService: RestApiService,
    private val userDatastore: UserDatastore
) : CodeEditorSubmitRepository {

    override suspend fun submit(
        titleSlug: String,
        language: String,
        code: String,
        questionId: String
    ): SubmitLeetcodeProblemResponse {
        return restApiService.submitLeetcodeProblem(
            titleSlug,
            userDatastore.getLeetcodeCsrfToken()!!,
            "LEETCODE_SESSION=${userDatastore.getLeetcodeSessionToken()!!}; " +
                "csrftoken=${userDatastore.getLeetcodeCsrfToken()!!}",
            ProblemSubmitRequest(
                language,
                code,
                questionId
            )
        )
    }

    override suspend fun getSubmissionResult(submissionId: Long): SubmissionCheckResponse {
        return restApiService.getSubmissionResult(
            submissionId,
            userDatastore.getLeetcodeCsrfToken()!!,
            "LEETCODE_SESSION=${userDatastore.getLeetcodeSessionToken()!!}; " +
                "csrftoken=${userDatastore.getLeetcodeCsrfToken()!!}",
        )
    }
}
