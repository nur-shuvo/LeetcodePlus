package com.byteutility.dev.leetcode.plus.network.responseVo


data class UserSolvedVo(
    val acSubmissionNumVo: List<AcSubmissionNumVo>,
    val easySolved: Int,
    val hardSolved: Int,
    val mediumSolved: Int,
    val solvedProblem: Int,
    val totalSubmissionNum: List<TotalSubmissionNumVo>
)

data class AcSubmissionNumVo(
    val count: Int,
    val difficulty: String,
    val submissions: Int
)

data class TotalSubmissionNumVo(
    val count: Int,
    val difficulty: String,
    val submissions: Int
)
