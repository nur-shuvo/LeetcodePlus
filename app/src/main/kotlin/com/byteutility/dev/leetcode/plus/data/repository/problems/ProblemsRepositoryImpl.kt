package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.model.ProblemsModel
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.OfficialSolutionResponse
import com.byteutility.dev.leetcode.plus.utils.toProblemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    private val restApiService: RestApiService,
    private val dao: ProblemsDao
) : ProblemsRepository {

    @Throws
    override suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse {
        return restApiService.getRawSelectedQuestionDetails(titleSlug)
    }

    @Throws
    override suspend fun getOfficialSolution(titleSlug: String): OfficialSolutionResponse {
        return restApiService.getOfficialSolution(titleSlug)
    }

    override suspend fun getRemoteProblems(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = restApiService.syncRemoteProblems()
                val body = response.string()
                val data = parseCsv(body)
                val problemSize = dao.getCount()
                if (data.size > problemSize) {
                    if (data.isNotEmpty()) {
                        data.let {
                            dao.insertAll(data.map { it.toProblemEntity() })
                        }
                    }
                }
                Result.success(Unit)
            } catch (ex: Exception) {
                Result.failure(ex)
            }
        }
    }

    private fun parseCsv(csv: String): List<ProblemsModel> {
        val lines = csv.lines().filter { it.isNotBlank() }
        if (lines.size < 4) return emptyList()
        return lines.drop(3).mapNotNull { line ->
            val cols = parseCsvLine(line)
            if (cols.size >= 14) {
                ProblemsModel(
                    id = cols[0].toInt(),
                    problemName = cols[1],
                    problemNameSlug = problemNameToSlug(cols[1]),
                    likes = cols[2],
                    dislikes = cols[3],
                    likeRatio = cols[4],
                    topics = cols[5],
                    difficulty = cols[6],
                    accepted = cols[7],
                    submissions = cols[8],
                    acceptRate = cols[9],
                    isFree = cols[10].toSheetBoolean(),
                    hasSolution = cols[11].toSheetBoolean(),
                    hasVideoSolution = cols[12].toSheetBoolean(),
                    category = cols[13]
                )
            } else {
                null
            }
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false

        for (char in line) {
            when {
                char == '"' -> inQuotes = !inQuotes
                char == ',' && !inQuotes -> {
                    result.add(current.toString().trim())
                    current = StringBuilder()
                }

                else -> current.append(char)
            }
        }
        result.add(current.toString().trim())
        return result
    }

    private fun String.toSheetBoolean(): Boolean {
        return this.equals("Yes", ignoreCase = true) ||
                this.equals("true", ignoreCase = true)
    }

    private fun problemNameToSlug(name: String): String {
        return name.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
    }
}
