package com.byteutility.dev.leetcode.plus.data.repository.userDetails

import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserDetailsRepositoryImpl @Inject constructor(
    private val userDatastore: UserDatastore,
) : UserDetailsRepository {

    override suspend fun getUserBasicInfo(): Flow<UserBasicInfo> {
        return userDatastore.getUserBasicInfo()
    }

    override suspend fun getUserContestInfo(): Flow<UserContestInfo> {
        return userDatastore.getUserContestInfo()
    }

    override suspend fun getUserProblemSolvedInfo(): Flow<UserProblemSolvedInfo> {
        return userDatastore.getUserProblemSolvedInfo()
    }

    override suspend fun getUserRecentSubmissions(): Flow<List<UserSubmission>> {
        return userDatastore.getUserSubmissions()
    }
}
