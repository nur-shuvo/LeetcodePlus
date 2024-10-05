package com.byteutility.dev.leetcode.plus.network

import com.byteutility.dev.leetcode.plus.network.annotation.Format
import com.byteutility.dev.leetcode.plus.network.annotation.RequestFormat
import com.byteutility.dev.leetcode.plus.network.annotation.ResponseFormat
import com.byteutility.dev.leetcode.plus.network.requestVO.SampleRequestVo
import com.byteutility.dev.leetcode.plus.network.responseVo.ProblemSetResponseVo
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
    ) : ProblemSetResponseVo
}