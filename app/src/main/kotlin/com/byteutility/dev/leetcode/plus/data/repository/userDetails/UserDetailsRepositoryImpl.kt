package com.byteutility.dev.leetcode.plus.data.repository.userDetails

import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailsRepositoryImpl @Inject constructor(
    private val userDatastore: UserDatastore,
) : UserDetailsRepository {

    override suspend fun getUserBasicInfo(): Flow<UserBasicInfo?> {
        return userDatastore.getUserBasicInfo()
    }

    override suspend fun getUserContestInfo(): Flow<UserContestInfo?> {
        return userDatastore.getUserContestInfo()
    }

    override suspend fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo?> {
        return userDatastore.getUserProblemSolvedInfo()
    }

    override suspend fun getUserRecentAcSubmissions(): Flow<List<UserSubmission>?> {
        return userDatastore.getUserAcSubmissions()
    }

    override suspend fun getUserLastSubmissions(): Flow<List<UserSubmission>?> {
        return userDatastore.getUserLastSubmissions()
    }

    override suspend fun getDailyProblem(): Flow<LeetCodeProblem?> {
        return userDatastore.getDailyProblem()
    }

    override suspend fun getUserRecentAcSubmissionsPaginated(
        page: Int,
        pageSize: Int
    ): Result<List<UserSubmission>> {
        delay(1000)
        val startingIndex = page * pageSize
        val submissions = userDatastore.getUserAcSubmissions().first() ?: emptyList()
        return if ((startingIndex + pageSize) <= submissions.size) {
            Result.success(
                userDatastore.getUserAcSubmissions().first()!!
                    .slice(startingIndex until startingIndex + pageSize)
            )
        } else Result.success(
            emptyList()
        )
    }
}
