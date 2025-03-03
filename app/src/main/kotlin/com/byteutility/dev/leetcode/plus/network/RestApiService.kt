package com.byteutility.dev.leetcode.plus.network

import com.byteutility.dev.leetcode.plus.network.annotation.Format
import com.byteutility.dev.leetcode.plus.network.annotation.ResponseFormat
import com.byteutility.dev.leetcode.plus.network.responseVo.DailyQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.ProblemSetResponseVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserContestVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserProfileVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSolvedVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSubmissionVo
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RestApiService {

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

    @ResponseFormat(Format.JSON)
    @GET("/daily")
    suspend fun getDailyProblem(): DailyQuestionResponse
}
