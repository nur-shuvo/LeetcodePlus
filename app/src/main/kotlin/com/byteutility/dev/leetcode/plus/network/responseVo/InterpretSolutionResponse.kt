package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class InterpretSolutionResponse(
    @SerializedName("interpret_id") val interpretId: String,
    @SerializedName("test_case") val testCase: String,
)
