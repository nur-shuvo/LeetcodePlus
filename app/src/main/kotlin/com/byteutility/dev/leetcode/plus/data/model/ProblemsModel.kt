package com.byteutility.dev.leetcode.plus.data.model

/**
 * Created by Johny on 1/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */

data class ProblemsModel(
    val id: Int,
    val problemName: String,
    val problemNameSlug: String,
    val likes: String,
    val dislikes: String,
    val likeRatio: String,
    val topics: String?,
    val difficulty: String,
    val accepted: String,
    val submissions: String,
    val acceptRate: String,
    val isFree: Boolean,
    val hasSolution: Boolean,
    val hasVideoSolution: Boolean,
    val category: String
)
