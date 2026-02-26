package com.byteutility.dev.leetcode.plus.data.repository.problems

import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.sets.ProblemSet
import com.byteutility.dev.leetcode.plus.data.sets.ProblemSetFactory
import com.byteutility.dev.leetcode.plus.domain.model.ProblemSetType
import com.byteutility.dev.leetcode.plus.network.RestApiService
import com.byteutility.dev.leetcode.plus.network.responseVo.LeetCodeQuestionResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ProblemsRepositoryImpl @Inject constructor(
    private val factory: ProblemSetFactory,
    private val restApiService: RestApiService
) : ProblemsRepository {

    private val masterList: List<LeetCodeProblem> by lazy {
        factory.loadMasterList()
    }
    private val setCache = mutableMapOf<ProblemSetType, ProblemSet>()

    override suspend fun getProblems(
        type: ProblemSetType?
    ): List<LeetCodeProblem> {
        return withContext(Dispatchers.IO) {
            if (type == null) return@withContext masterList
            val problemSet = setCache.getOrPut(type) {
                factory.createSet(type)
            }
            masterList.filter { problemSet.isIncluded(it) }
        }
    }

    @Throws
    override suspend fun getSelectedRawQuestion(titleSlug: String): LeetCodeQuestionResponse {
        return restApiService.getRawSelectedQuestionDetails(titleSlug)
    }
}
