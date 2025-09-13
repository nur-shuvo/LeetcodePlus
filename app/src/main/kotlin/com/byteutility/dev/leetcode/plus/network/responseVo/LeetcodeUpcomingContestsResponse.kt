
package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class LeetcodeUpcomingContestsResponse(
    val meta: Meta,
    val objects: List<Contest>
)

data class Meta(
    @SerializedName("estimated_count")
    val estimatedCount: Any?,
    val limit: Int,
    val next: String,
    val offset: Int,
    val previous: Any?,
    @SerializedName("total_count")
    val totalCount: Any?
)

data class Contest(
    val duration: Int,
    val end: String,
    val event: String,
    val host: String,
    val href: String,
    val id: Int,
    @SerializedName("n_problems")
    val nProblems: Any?,
    @SerializedName("n_statistics")
    val nStatistics: Any?,
    @SerializedName("parsed_at")
    val parsedAt: Any?,
    val problems: Any?,
    val resource: String,
    @SerializedName("resource_id")
    val resourceId: Int,
    val start: String
)
