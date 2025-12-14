package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class UserContestVo(
    @SerializedName("contestAttend") val contestAttend: Int,
    @SerializedName("contestBadges") val contestBadges: Any,
    @SerializedName("contestGlobalRanking") val contestGlobalRanking: Int,
    @SerializedName("contestParticipation") val contestParticipation: List<ContestParticipationVo>,
    @SerializedName("contestRating") val contestRating: Double,
    @SerializedName("contestTopPercentage") val contestTopPercentage: Double,
    @SerializedName("totalParticipants") val totalParticipants: Int
)

data class ContestParticipationVo(
    @SerializedName("attended") val attended: Boolean,
    @SerializedName("contest") val contestVo: ContestVo,
    @SerializedName("finishTimeInSeconds") val finishTimeInSeconds: Int,
    @SerializedName("problemsSolved") val problemsSolved: Int,
    @SerializedName("ranking") val ranking: Int,
    @SerializedName("rating") val rating: Double,
    @SerializedName("totalProblems") val totalProblems: Int,
    @SerializedName("trendDirection") val trendDirection: String
)

data class ContestVo(
    @SerializedName("startTime") val startTime: Int,
    @SerializedName("title") val title: String
)
