package com.byteutility.dev.leetcode.plus.data.repository.problems

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.model.ProblemsModel
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.OfficialSolutionResponse
import com.byteutility.dev.leetcode.plus.utils.toProblemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    private val restApiService: RestApiService,
    private val dao: ProblemsDao
) : ProblemsRepository {
    override fun getProblems(
        query: String?,
        difficulty: List<String>?,
        tags: List<String>?
    ): PagingSource<Int, ProblemEntity> {
        val conditions = mutableListOf<String>()
        val args = mutableListOf<Any>()
        if (!query.isNullOrBlank()) {
            conditions.add(
                """
                (title LIKE '%' || ? || '%' 
                OR title_slug LIKE '%' || ? || '%' 
                OR topic_tags LIKE '%' || ? || '%')
            """.trimIndent()
            )
            args.add(query)
            args.add(query)
            args.add(query)
        }
        if (!difficulty.isNullOrEmpty()) {
            val placeholders = difficulty.joinToString(",") { "?" }
            conditions.add("difficulty IN ($placeholders)")
            args.addAll(difficulty)
        }
        if (!tags.isNullOrEmpty()) {
            val tagConditions = tags.map {
                "topic_tags LIKE '%' || ? || '%'"
            }
            conditions.add("(${tagConditions.joinToString(" OR ")})")
            args.addAll(tags)
        }

        val whereClause = if (conditions.isNotEmpty()) {
            "WHERE " + conditions.joinToString(" AND ")
        } else ""
        val sql = "SELECT * FROM all_problems $whereClause ORDER BY problem_id ASC"
        return dao.getProblems(SimpleSQLiteQuery(sql, args.toTypedArray()))
    }

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
                val spreadsheetId = "1sRWp95wqo3a7lLBbtNd_3KkTyGjx_9sctTOL5JOb6pA"
                val url =
                    "https://docs.google.com/spreadsheets/d/$spreadsheetId/export?format=csv&gid=0"
                val client = OkHttpClient()
                val request = Request.Builder().url(url).build()
                val response = client.newCall(request).execute()
                if (!response.isSuccessful) throw Exception("Failed: ${response.code}")
                val body = response.body?.string() ?: throw Exception("Empty response")
                val data = parseCsv(body)
                val problemSize = dao.getCount()
                if (data.size > problemSize){
                    if (data.isNotEmpty()){
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

    override suspend fun getAllTags(): List<String> {
        return dao.getUniqueTags()
    }

    override suspend fun getDifficulty(): List<String> {
        return dao.getUniqueDifficulties()
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
            } else null
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
        return this.equals("Yes", ignoreCase = true)
                || this.equals("true", ignoreCase = true)
    }

    private fun problemNameToSlug(name: String): String {
        return name.lowercase()
            .replace(Regex("[^a-z0-9\\s-]"), "")
            .trim()
            .replace(Regex("\\s+"), "-")
    }
}
