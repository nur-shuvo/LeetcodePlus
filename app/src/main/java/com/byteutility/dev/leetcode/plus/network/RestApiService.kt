package com.byteutility.dev.leetcode.plus.network

import com.byteutility.dev.leetcode.plus.network.annotation.Format
import com.byteutility.dev.leetcode.plus.network.annotation.RequestFormat
import com.byteutility.dev.leetcode.plus.network.annotation.ResponseFormat
import com.byteutility.dev.leetcode.plus.network.requestVO.SampleRequestVo
import com.byteutility.dev.leetcode.plus.network.responseVo.ProblemSetResponseVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserContestVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserProfileVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSolvedVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSubmissionVo
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

interface RestApiService {
    @ResponseFormat(Format.JSON)
    @RequestFormat(Format.JSON)
    @POST
    suspend fun samplePostRequest(
        @Url fullUrl: String,
        @HeaderMap headerMap: Map<String, String>,
        @Body body: SampleRequestVo
    )

    @ResponseFormat(Format.JSON)
    @GET("/problems")
    suspend fun getProblemsWithLimit(
        @Query("limit") limit: Long
    ): ProblemSetResponseVo

    @ResponseFormat(Format.JSON)
    @GET("/{username}/submission")
    suspend fun getLastSubmissions(
        @Path("username") username: String,
        @Query("limit") limit: Int,
    ): UserSubmissionVo

    @ResponseFormat(Format.JSON)
    @GET("/{username}")
    suspend fun getUserProfile(
        @Path("username") username: String
    ): UserProfileVo

    @ResponseFormat(Format.JSON)
    @GET("/{username}/contest")
    suspend fun getUserContest(
        @Path("username") username: String
    ): UserContestVo

    @ResponseFormat(Format.JSON)
    @GET("/{username}/acSubmission")
    suspend fun getAcSubmission(
        @Path("username") username: String,
        @Query("limit") limit: Int,
    ): UserSubmissionVo

    @ResponseFormat(Format.JSON)
    @GET("/{username}/solved")
    suspend fun getSolved(
        @Path("username") username: String,
    ): UserSolvedVo
}
