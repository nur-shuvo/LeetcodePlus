package com.byteutility.dev.leetcode.plus.data.repository.problems

import androidx.paging.PagingSource
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity

/**
 * Created by Johny on 14/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
interface LocalProblemRepository {
    fun getProblems(query: String?, difficulty: List<String>?, tags: List<String>?): PagingSource<Int, ProblemEntity>
    suspend fun getAllTags(): List<String>
    suspend fun getDifficulty(): List<String>
    suspend fun difficultyStat(): Triple<Int, Int, Int>
    suspend fun getCount(): Int
}
