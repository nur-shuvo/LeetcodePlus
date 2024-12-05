package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem

interface ProblemsRepository {
    suspend fun getProblems(limit: Long): List<LeetCodeProblem>
}
