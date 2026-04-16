package com.byteutility.dev.leetcode.plus.data.database.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Transaction
import androidx.sqlite.db.SupportSQLiteQuery
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity

/**
 * Created by Johny on 1/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
@Dao
interface ProblemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(problems: List<ProblemEntity>)

    @Query("SELECT COUNT(*) FROM all_problems")
    suspend fun getCount(): Int

    @Transaction
    suspend fun insertAll(problems: List<ProblemEntity>) {
        problems.chunked(500).forEach { chunk ->
            insertBatch(chunk)
        }
    }

    @Query("SELECT DISTINCT topic_tags FROM all_problems WHERE topic_tags IS NOT NULL")
    suspend fun getAllTopicTagsRaw(): List<String>

    @Query("SELECT DISTINCT difficulty FROM all_problems")
    suspend fun getUniqueDifficulties(): List<String>

    @Query("SELECT COUNT(*) FROM all_problems WHERE difficulty = 'Easy'")
    suspend fun getEasyCount(): Int

    @Query("SELECT COUNT(*) FROM all_problems WHERE difficulty = 'Medium'")
    suspend fun getMediumCount(): Int

    @Query("SELECT COUNT(*) FROM all_problems WHERE difficulty = 'Hard'")
    suspend fun getHardCount(): Int

    @RawQuery(observedEntities = [ProblemEntity::class])
    fun getProblems(query: SupportSQLiteQuery): PagingSource<Int, ProblemEntity>
}