package com.byteutility.dev.leetcode.plus.ui.screens.home.model

import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.google.api.services.youtube.model.Video

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

data class DifficultyStatistics(
    val easyProblemCount: Int = 937,
    val mediumProblemCount: Int = 2037,
    val hardProblemCount: Int = 921
)
