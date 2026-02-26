package com.byteutility.dev.leetcode.plus.data.sets

import android.content.res.AssetManager
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType.PredefinedProblemSet
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType.UserDefinedProblemSet
import com.byteutility.dev.leetcode.plus.network.responseVo.ProblemSetResponseVo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

class AssetProblemSetFactory @Inject constructor(
    private val assetManager: AssetManager,
    private val gson: Gson
) : ProblemSetFactory {
    override fun createSet(type: ProblemSetType): ProblemSet {
        return when (type) {
            is PredefinedProblemSet -> {
                val titles = loadTitlesFromAssets(type.metadata.file)
                PredefinedProblemSet(displayName = type.displayName, titles = titles)
            }

            is UserDefinedProblemSet -> {
                UserDefinedProblemSet(
                    displayName = type.displayName,
                    titles = emptyList()
                )
            }
        }
    }

    override fun loadMasterList(): List<LeetCodeProblem> {
        return loadAllProblemFromAssets("problems.json").toList()
    }

    private fun loadTitlesFromAssets(fileName: String): List<String> {
        val json = assetManager.open("list/$fileName").bufferedReader().use { it.readText() }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson<List<String>>(json, listType)
    }

    private fun loadAllProblemFromAssets(fileName: String): List<LeetCodeProblem> {
        val json = assetManager.open(fileName).bufferedReader().use { it.readText() }
        val response = gson.fromJson(json, ProblemSetResponseVo::class.java)
        val allProblems = response.problemSetQuestionList.map {
            LeetCodeProblem(
                title = it.title,
                difficulty = it.difficulty,
                tag = it.topicTags.firstOrNull()?.name ?: "NO_TAG",
                titleSlug = it.titleSlug,
            )
        }
        return allProblems
    }
}
