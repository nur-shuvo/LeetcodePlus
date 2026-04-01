package com.byteutility.dev.leetcode.plus.data.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by Johny on 31/3/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */

@Entity(tableName = "all_problems")
data class ProblemEntity(
    @PrimaryKey
    @ColumnInfo(name = "problem_id")
    val problemId: Int,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "title_slug")
    val titleSlug: String?,
    @ColumnInfo(name = "difficulty")
    val difficulty: String,
    @ColumnInfo(name = "acceptance")
    val acceptance: Double,
    @ColumnInfo(name = "isPaidOnly")
    val isPaidOnly: Boolean,
    @ColumnInfo(name = "has_solution")
    val hasSolution: Boolean,
    @ColumnInfo(name = "has_video_solution")
    val hasVideoSolution: Boolean,
    @ColumnInfo(name = "topic_tags")
    val topicTags: List<TopicTag>?
)

data class TopicTag(
    val name: String,
    val id: String,
    val slug: String
)


