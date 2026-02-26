package com.byteutility.dev.leetcode.plus.data.sets

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem

interface ProblemSet {
    val displayName: String

    /**
     * Returns true if the given problem belongs to this specific set.
     */
    fun isIncluded(problem: LeetCodeProblem): Boolean
}

class PredefinedProblemSet(
    override val displayName: String,
    private val titles: List<String>
) : ProblemSet {

    override fun isIncluded(problem: LeetCodeProblem): Boolean {
        return titles.contains(problem.title.trim())
    }
}

class UserDefinedProblemSet(
    override val displayName: String,
    private val titles: List<String>
) : ProblemSet {
    override fun isIncluded(problem: LeetCodeProblem): Boolean {
        return titles.contains(problem.title)
    }
}
