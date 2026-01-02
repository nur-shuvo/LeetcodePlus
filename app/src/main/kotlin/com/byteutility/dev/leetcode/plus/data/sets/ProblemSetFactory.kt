package com.byteutility.dev.leetcode.plus.data.sets

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType

interface ProblemSetFactory {
    fun createSet(type: ProblemSetType): ProblemSet
    fun loadMasterList(): List<LeetCodeProblem>
}
