package com.byteutility.dev.leetcode.plus.data.repository.codeSubmit

import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.requestVO.InterpretSolutionRequest
import com.byteutility.dev.leetcode.plus.network.requestVO.ProblemSubmitRequest
import com.byteutility.dev.leetcode.plus.network.responseVo.InterpretSolutionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.RunCodeCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionCheckResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmitLeetcodeProblemResponse
import javax.inject.Inject

class CodeEditorSubmitRepositoryImpl @Inject constructor(
    private val restApiService: RestApiService,
    private val userDatastore: UserDatastore
) : CodeEditorSubmitRepository {

    private suspend fun cookieHeader(): String =
        "LEETCODE_SESSION=${userDatastore.getLeetcodeSessionToken()!!}; " +
            "csrftoken=${userDatastore.getLeetcodeCsrfToken()!!}"

    override suspend fun submit(
        titleSlug: String,
        language: String,
        code: String,
        questionId: String
    ): SubmitLeetcodeProblemResponse {
        return restApiService.submitLeetcodeProblem(
            titleSlug,
            userDatastore.getLeetcodeCsrfToken()!!,
            cookieHeader(),
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
            cookieHeader(),
        )
    }

    override suspend fun interpretSolution(
        titleSlug: String,
        language: String,
        code: String,
        questionId: String,
        testCases: String
    ): InterpretSolutionResponse {
        return restApiService.interpretSolution(
            titleSlug,
            userDatastore.getLeetcodeCsrfToken()!!,
            cookieHeader(),
            InterpretSolutionRequest(
                lang = language,
                questionId = questionId,
                typedCode = code,
                dataInput = testCases
            )
        )
    }

    override suspend fun getRunCodeResult(interpretId: String): RunCodeCheckResponse {
        return restApiService.getRunCodeResult(
            interpretId,
            userDatastore.getLeetcodeCsrfToken()!!,
            cookieHeader(),
        )
    }
}
