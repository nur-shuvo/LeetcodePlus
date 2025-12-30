package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class DailyQuestionResponse(
    @SerializedName("questionLink") val questionLink: String,
    @SerializedName("date") val date: String,
    @SerializedName("questionId") val questionId: String,
    @SerializedName("questionFrontendId") val questionFrontendId: String,
    @SerializedName("questionTitle") val questionTitle: String,
    @SerializedName("titleSlug") val titleSlug: String,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("isPaidOnly") val isPaidOnly: Boolean,
    @SerializedName("question") val question: String,
    @SerializedName("exampleTestcases") val exampleTestcases: String,
    @SerializedName("topicTags") val topicTags: List<DailyTopicTag>,
    @SerializedName("hints") val hints: List<String>,
    @SerializedName("solution") val solution: Solution?,
    @SerializedName("companyTagStats") val companyTagStats: Any?,
    @SerializedName("likes") val likes: Int,
    @SerializedName("dislikes") val dislikes: Int,
    @SerializedName("similarQuestions") val similarQuestions: String
)

data class DailyTopicTag(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("translatedName") val translatedName: String?
)

data class Solution(
    @SerializedName("id") val id: String,
    @SerializedName("canSeeDetail") val canSeeDetail: Boolean,
    @SerializedName("paidOnly") val paidOnly: Boolean,
    @SerializedName("hasVideoSolution") val hasVideoSolution: Boolean,
    @SerializedName("paidOnlyVideo") val paidOnlyVideo: Boolean
)
