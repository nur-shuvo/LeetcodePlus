package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class OfficialSolutionResponse(
    @SerializedName("question") val question: OfficialSolutionQuestion?
)

data class OfficialSolutionQuestion(
    @SerializedName("solution") val solution: OfficialSolution?
)

data class OfficialSolution(
    @SerializedName("id") val id: String?,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("paidOnly") val paidOnly: Boolean = false,
    @SerializedName("hasVideoSolution") val hasVideoSolution: Boolean = false,
    @SerializedName("paidOnlyVideo") val paidOnlyVideo: Boolean = false,
    @SerializedName("canSeeDetail") val canSeeDetail: Boolean = false
)
