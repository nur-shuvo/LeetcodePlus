package com.byteutility.dev.leetcode.plus.data.model


data class ProblemStatus(
    val title: String,
    val titleSlug: String,
    var status: String,
    val difficulty: String,
    var attemptsCount: Int,
)