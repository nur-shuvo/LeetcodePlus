package com.byteutility.dev.leetcode.plus.network.responseVo


data class UserContestVo(
    val contestAttend: Int,
    val contestBadges: Any,
    val contestGlobalRanking: Int,
    val contestParticipation: List<ContestParticipationVo>,
    val contestRating: Double,
    val contestTopPercentage: Double,
    val totalParticipants: Int
)

data class ContestParticipationVo(
    val attended: Boolean,
    val contestVo: ContestVo,
    val finishTimeInSeconds: Int,
    val problemsSolved: Int,
    val ranking: Int,
    val rating: Double,
    val totalProblems: Int,
    val trendDirection: String
)

data class ContestVo(
    val startTime: Int,
    val title: String
)
