package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class UserSolvedVo(
    @SerializedName("acSubmissionNum") val acSubmissionNumVo: List<AcSubmissionNumVo>,
    @SerializedName("easySolved") val easySolved: Int,
    @SerializedName("hardSolved") val hardSolved: Int,
    @SerializedName("mediumSolved") val mediumSolved: Int,
    @SerializedName("solvedProblem") val solvedProblem: Int,
    @SerializedName("totalSubmissionNum") val totalSubmissionNum: List<TotalSubmissionNumVo>
)

data class AcSubmissionNumVo(
    @SerializedName("count") val count: Int,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("submissions") val submissions: Int
)

data class TotalSubmissionNumVo(
    @SerializedName("count") val count: Int,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("submissions") val submissions: Int
)
