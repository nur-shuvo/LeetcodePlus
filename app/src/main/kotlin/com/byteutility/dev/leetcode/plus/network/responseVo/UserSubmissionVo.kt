package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class UserSubmissionVo(
    @SerializedName("count")
    val count: Int,
    @SerializedName("submission")
    val submissionVo: List<SubmissionVo>
)

data class SubmissionVo(
    @SerializedName("lang")
    val lang: String,
    @SerializedName("statusDisplay")
    val statusDisplay: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("titleSlug")
    val titleSlug: String
)
