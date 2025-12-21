package com.byteutility.dev.leetcode.plus.ui.screens.home.model

import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.google.api.services.youtube.model.Video

/**
 * Created by Shuvo on 11/06/2025.
 */
data class UserDetailsUiState(
    val userBasicInfo: UserBasicInfo = UserBasicInfo(),
    val userContestInfo: UserContestInfo = UserContestInfo(),
    val userProblemSolvedInfo: UserProblemSolvedInfo = UserProblemSolvedInfo(),
    val userSubmissionState: UserSubmissionState = UserSubmissionState(),
    val isWeeklyGoalSet: Boolean = false,
    val syncInterval: Long = 30,
    val videosByPlayListState: VideosByPlayListState = VideosByPlayListState(),
    val leetcodeUpcomingContestsState: LeetcodeUpcomingContestsState = LeetcodeUpcomingContestsState()
)

data class LeetcodeUpcomingContestsState(
    val isLoading: Boolean = false,
    val contests: List<Contest> = emptyList(),
    val error: String? = null
)

data class UserSubmissionState(
    val isLoading: Boolean = false,
    val submissions: List<UserSubmission> = emptyList(),
    val error: String? = null,
    val endReached: Boolean = false,
    val page: Int = 0
)

data class VideosByPlayListState(
    val isLoading: Boolean = false,
    val videos: List<Video> = mutableListOf(),
    val error: String? = null,
    val endReached: Boolean = false,
)