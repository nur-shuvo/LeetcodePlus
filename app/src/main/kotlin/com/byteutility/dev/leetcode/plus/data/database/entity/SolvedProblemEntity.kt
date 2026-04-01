package com.byteutility.dev.leetcode.plus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Johny on 31/3/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */
@Entity(tableName = "solved_problem")
data class SolvedProblemEntity(
    @PrimaryKey
    @ColumnInfo(name = "problem_id")
    val problemId: Int
)
