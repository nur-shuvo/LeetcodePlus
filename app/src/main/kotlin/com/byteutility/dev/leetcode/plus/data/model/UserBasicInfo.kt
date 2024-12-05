package com.byteutility.dev.leetcode.plus.data.model

data class UserBasicInfo(
    val name: String = "",
    val userName: String = "",
    val avatar: String = "",
    val ranking: Int = -1,
    val country: String = "",
)

data class UserContestInfo(
    val rating: Double = 0.0,
    val globalRanking: Int = -1,
    val attend: Int = -1,
)

data class UserProblemSolvedInfo(
    val easy: Int = -1,
    val medium: Int = -1,
    val hard: Int = -1,
    val totalSolved: Int = -1
) {
    fun getEasyPercentage() = (easy.toFloat() / totalSolved.toFloat()) * 100
    fun getMediumPercentage() = (medium.toFloat() / totalSolved.toFloat()) * 100
    fun getHardPercentage() = (hard.toFloat() / totalSolved.toFloat()) * 100
}


data class UserSubmission(
    val lang: String = "",
    val statusDisplay: String = "",
    val timestamp: String = "",
    val title: String = "",
    val titleSlug: String = ""
)
