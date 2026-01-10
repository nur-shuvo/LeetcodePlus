package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class LeetCodeQuestionResponse(
    @SerializedName("question") val question: RawQuestion
)

data class RawQuestion(
    @SerializedName("questionId") val questionId: String,
    @SerializedName("questionFrontendId") val questionFrontendId: String,
    @SerializedName("boundTopicId") val boundTopicId: String? = null,
    @SerializedName("title") val title: String,
    @SerializedName("titleSlug") val titleSlug: String,
    @SerializedName("content") val content: String?,
    @SerializedName("translatedTitle") val translatedTitle: String? = null,
    @SerializedName("translatedContent") val translatedContent: String? = null,
    @SerializedName("isPaidOnly") val isPaidOnly: Boolean,
    @SerializedName("difficulty") val difficulty: String,
    @SerializedName("likes") val likes: Int,
    @SerializedName("dislikes") val dislikes: Int,
    @SerializedName("isLiked") val isLiked: Boolean? = null,
    @SerializedName("similarQuestions") val similarQuestions: String,
    @SerializedName("exampleTestcases") val exampleTestcases: String,
    @SerializedName("contributors") val contributors: List<String>,
    @SerializedName("topicTags") val topicTags: List<RawTopicTag> = emptyList(),
    @SerializedName("companyTagStats") val companyTagStats: String? = null,
    @SerializedName("codeSnippets") val codeSnippets: List<CodeSnippet>? = null,
    @SerializedName("stats") val stats: String,
    @SerializedName("hints") val hints: List<String> = emptyList(),
    @SerializedName("solution") val solution: RawSolution? = null,
    @SerializedName("status") val status: String?,
    @SerializedName("sampleTestCase") val sampleTestCase: String,
    @SerializedName("metaData") val metaData: String,
    @SerializedName("judgerAvailable") val judgerAvailable: Boolean,
    @SerializedName("judgeType") val judgeType: String,
    @SerializedName("mysqlSchemas") val mysqlSchemas: List<String> = emptyList(),
    @SerializedName("enableRunCode") val enableRunCode: Boolean,
    @SerializedName("enableTestMode") val enableTestMode: Boolean,
    @SerializedName("enableDebugger") val enableDebugger: Boolean,
    @SerializedName("envInfo") val envInfo: String,
    @SerializedName("libraryUrl") val libraryUrl: String? = null,
    @SerializedName("adminUrl") val adminUrl: String? = null,
    @SerializedName("note") val note: String? = null
)

data class RawTopicTag(
    @SerializedName("name") val name: String,
    @SerializedName("slug") val slug: String,
    @SerializedName("translatedName") val translatedName: String? = null
)

data class CodeSnippet(
    @SerializedName("lang") val lang: String,
    @SerializedName("langSlug") val langSlug: String,
    @SerializedName("code") val code: String
)

data class RawSolution(
    @SerializedName("id") val id: String,
    @SerializedName("canSeeDetail") val canSeeDetail: Boolean,
    @SerializedName("paidOnly") val paidOnly: Boolean,
    @SerializedName("hasVideoSolution") val hasVideoSolution: Boolean,
    @SerializedName("paidOnlyVideo") val paidOnlyVideo: Boolean
)
