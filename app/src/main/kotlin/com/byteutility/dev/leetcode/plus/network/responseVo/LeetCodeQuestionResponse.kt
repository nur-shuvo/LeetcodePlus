package com.byteutility.dev.leetcode.plus.network.responseVo

data class LeetCodeQuestionResponse(
    val question: RawQuestion
)

data class RawQuestion(
    val questionId: String,
    val questionFrontendId: String,
    val boundTopicId: String? = null,
    val title: String,
    val titleSlug: String,
    val content: String,
    val translatedTitle: String? = null,
    val translatedContent: String? = null,
    val isPaidOnly: Boolean,
    val difficulty: String,
    val likes: Int,
    val dislikes: Int,
    val isLiked: Boolean? = null,
    val similarQuestions: String,
    val exampleTestcases: String,
    val contributors: List<String>,
    val topicTags: List<RawTopicTag> = emptyList(),
    val companyTagStats: String? = null,
    val codeSnippets: List<CodeSnippet> = emptyList(),
    val stats: String,
    val hints: List<String> = emptyList(),
    val solution: RawSolution? = null,
    val status: String?,
    val sampleTestCase: String,
    val metaData: String,
    val judgerAvailable: Boolean,
    val judgeType: String,
    val mysqlSchemas: List<String> = emptyList(),
    val enableRunCode: Boolean,
    val enableTestMode: Boolean,
    val enableDebugger: Boolean,
    val envInfo: String,
    val libraryUrl: String? = null,
    val adminUrl: String? = null,
    val note: String? = null
)

data class RawTopicTag(
    val name: String,
    val slug: String,
    val translatedName: String? = null
)

data class CodeSnippet(
    val lang: String,
    val langSlug: String,
    val code: String
)

data class RawSolution(
    val id: String,
    val canSeeDetail: Boolean,
    val paidOnly: Boolean,
    val hasVideoSolution: Boolean,
    val paidOnlyVideo: Boolean
)
