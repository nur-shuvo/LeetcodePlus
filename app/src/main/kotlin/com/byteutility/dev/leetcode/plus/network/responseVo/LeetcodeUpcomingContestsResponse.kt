
package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class LeetcodeUpcomingContestsResponse(
    @SerializedName("meta") val meta: Meta,
    @SerializedName("objects") val objects: List<Contest>
)

data class Meta(
    @SerializedName("estimated_count") val estimatedCount: Any?,
    @SerializedName("limit") val limit: Int,
    @SerializedName("next") val next: String,
    @SerializedName("offset") val offset: Int,
    @SerializedName("previous") val previous: Any?,
    @SerializedName("total_count") val totalCount: Any?
)

data class Contest(
    @SerializedName("duration") val duration: Int,
    @SerializedName("end") val end: String,
    @SerializedName("event") val event: String,
    @SerializedName("host") val host: String,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: Int,
    @SerializedName("n_problems") val nProblems: Any?,
    @SerializedName("n_statistics") val nStatistics: Any?,
    @SerializedName("parsed_at") val parsedAt: Any?,
    @SerializedName("problems") val problems: Any?,
    @SerializedName("resource") val resource: String,
    @SerializedName("resource_id") val resourceId: Int,
    @SerializedName("start") val start: String
)
