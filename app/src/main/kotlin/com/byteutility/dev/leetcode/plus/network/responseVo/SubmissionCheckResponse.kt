package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class SubmissionCheckResponse(
    @SerializedName("status_msg")
    val statusMessage: String,
    val state: String,
    @SerializedName("compile_error")
    val compileError: String? = null,
)