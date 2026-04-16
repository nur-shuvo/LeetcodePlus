package com.byteutility.dev.leetcode.plus.data.repository.problems

import androidx.paging.PagingSource
import androidx.sqlite.db.SimpleSQLiteQuery
import com.byteutility.dev.leetcode.plus.data.database.dao.ProblemsDao
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import javax.inject.Inject

/**
 * Created by Johny on 14/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
class LocalProblemRepositoryImpl @Inject constructor(
    private val dao: ProblemsDao
): LocalProblemRepository {
    override fun getProblems(query: String?, difficulty: List<String>?, tags: List<String>?): PagingSource<Int, ProblemEntity> {
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

    override suspend fun getAllTags(): List<String> {
        val type = object : TypeToken<List<String>>(){}.type
        val gson = Gson()
        return dao.getAllTopicTagsRaw()
            .flatMap { json -> gson.fromJson<List<String>>(json,type) }
            .distinct()
    }

    override suspend fun getDifficulty(): List<String> {
        return dao.getUniqueDifficulties()
    }

    override suspend fun difficultyStat(): Triple<Int, Int, Int> {
        val easy = dao.getEasyCount()
        val medium = dao.getMediumCount()
        val hard = dao.getHardCount()
        return Triple(easy, medium, hard)
    }

    override suspend fun getCount(): Int {
        return dao.getCount()
    }
}