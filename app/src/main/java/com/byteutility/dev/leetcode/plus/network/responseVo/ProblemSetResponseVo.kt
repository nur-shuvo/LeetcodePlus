package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class ProblemSetResponseVo(
    @SerializedName("totalQuestions")
    val totalQuestions: Int,
    @SerializedName("count")
    val count: Int,
    @SerializedName("problemsetQuestionList")
    val problemSetQuestionList: List<Question>
)

data class Question(
    @SerializedName("acRate")
    val acRate: Double,
    @SerializedName("difficulty")
    val difficulty: String,
    @SerializedName("freqBar")
    val freqBar: Any?,
    @SerializedName("questionFrontendId")
    val questionFrontendId: String,
    @SerializedName("isFavor")
    val isFavor: Boolean,
    @SerializedName("isPaidOnly")
    val isPaidOnly: Boolean,
    @SerializedName("status")
    val status: Any?,
    @SerializedName("title")
    val title: String,
    @SerializedName("titleSlug")
    val titleSlug: String,
    @SerializedName("topicTags")
    val topicTags: List<TopicTag>,
    @SerializedName("hasSolution")
    val hasSolution: Boolean,
    @SerializedName("hasVideoSolution")
    val hasVideoSolution: Boolean
)

data class TopicTag(
    @SerializedName("name")
    val name: String,
    @SerializedName("id")
    val id: String,
    @SerializedName("slug")
    val slug: String
)
