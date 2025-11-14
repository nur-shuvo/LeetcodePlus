package com.byteutility.dev.leetcode.plus.data.repository.problems

import android.content.Context
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.ProblemSetResponseVo
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val restApiService: RestApiService
) : ProblemsRepository {

    override suspend fun getProblems(
        limit: Long
    ): List<LeetCodeProblem> {
        var leetCodeProblems: List<LeetCodeProblem> = mutableListOf()
        withContext(Dispatchers.IO) {
            val response: ProblemSetResponseVo = parseProblemsJson(context)
            response.problemSetQuestionList.map {
                LeetCodeProblem(
                    title = it.title,
                    difficulty = it.difficulty,
                    tag = it.topicTags.firstOrNull()?.name ?: "NO_TAG",
                    titleSlug = it.titleSlug,
                )
            }.run {
                leetCodeProblems = this
            }
        }
        return leetCodeProblems
    }

    private fun parseProblemsJson(
        context: Context
    ): ProblemSetResponseVo {
        val jsonString = loadJsonFromAssets(context, "problems.json")
        val gson = Gson()
        return gson.fromJson(jsonString, ProblemSetResponseVo::class.java)
    }

    private fun loadJsonFromAssets(
        context: Context,
        fileName: String
    ): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }

    @Throws
    override suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse {
        return restApiService.getRawSelectedQuestionDetails(titleSlug)
    }
}
