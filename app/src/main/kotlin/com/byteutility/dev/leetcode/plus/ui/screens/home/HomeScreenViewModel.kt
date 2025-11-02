package com.byteutility.dev.leetcode.plus.ui.screens.home

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.data.pagination.DefaultPaginator
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.ContestReminderWorker
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.google.api.services.youtube.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

data class UserDetailsUiState(
    val userBasicInfo: UserBasicInfo = UserBasicInfo(),
    val userContestInfo: UserContestInfo = UserContestInfo(),
    val userProblemSolvedInfo: UserProblemSolvedInfo = UserProblemSolvedInfo(),
    val userSubmissionState: UserSubmissionState = UserSubmissionState(),
    val isWeeklyGoalSet: Boolean = false,
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

@HiltViewModel
class UserDetailsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDetailsRepository: UserDetailsRepository,
    private val goalRepository: WeeklyGoalRepository,
    private val notificationDataStore: NotificationDataStore,
    dailyProblemStatusMonitor: DailyProblemStatusMonitor
) : ViewModel() {

    // Submissions
    private val userSubmissionState =
        MutableStateFlow(UserSubmissionState())
    private val userSubmissionPagination = getUserSubmissionPaginator()

    // Videos
    private val videosByPlayListState = MutableStateFlow(VideosByPlayListState())
    private var pageTokenForPlayList: String? = null
    private val videosByPlayListPagination = getVideosPaginator()


    private val _leetcodeUpcomingContestsState = MutableStateFlow(LeetcodeUpcomingContestsState())


    private val userBasicInfo =
        MutableStateFlow(UserBasicInfo())

    private val userContestInfo =
        MutableStateFlow(UserContestInfo())

    private val userProblemSolvedInfo =
        MutableStateFlow(UserProblemSolvedInfo())

    private val isWeeklyGoalSet = MutableStateFlow(false)

    private val _dailyProblem =
        MutableStateFlow(LeetCodeProblem("", "", ""))
    val dailyProblem = _dailyProblem.asStateFlow()

    val dailyProblemSolved = dailyProblemStatusMonitor.dailyProblemSolved.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    val uiState: StateFlow<UserDetailsUiState> =
        combine(
            listOf(
                userBasicInfo,
                userContestInfo,
                userProblemSolvedInfo,
                userSubmissionState,
                isWeeklyGoalSet,
                videosByPlayListState,
                _leetcodeUpcomingContestsState
            )
        ) { values ->
            UserDetailsUiState(
                userBasicInfo = values[0] as UserBasicInfo,
                userContestInfo = values[1] as UserContestInfo,
                userProblemSolvedInfo = values[2] as UserProblemSolvedInfo,
                userSubmissionState = values[3] as UserSubmissionState,
                isWeeklyGoalSet = values[4] as Boolean,
                videosByPlayListState = values[5] as VideosByPlayListState,
                leetcodeUpcomingContestsState = values[6] as LeetcodeUpcomingContestsState
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserDetailsUiState()
        )

    init {
        loadNextAcSubmissions()

        viewModelScope.launch {
            userDetailsRepository
                .getUserBasicInfo()
                .collect {
                    if (it != null) {
                        userBasicInfo.value = it
                    }
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserContestInfo()
                .collect {
                    if (it != null) {
                        userContestInfo.value = it
                    }
                }
        }

        viewModelScope.launch {
            userDetailsRepository
                .getUserProblemSolvedInfo()
                .collect {
                    if (it != null) {
                        userProblemSolvedInfo.value = it
                    }
                }
        }

        viewModelScope.launch(Dispatchers.IO) {
            goalRepository.weeklyGoal.collect {
                isWeeklyGoalSet.value = (it != null)
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
            goalRepository.weeklyGoal.collect { goal ->
                if (goal != null) {
                    val endDate = LocalDate.parse(goal.toWeeklyGoalPeriod().endDate, formatter)
                    if (LocalDate.now().isAfter(endDate)) {
                        goalRepository.deleteWeeklyGoal()
                    }
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            userDetailsRepository.getDailyProblem().collect {
                if (it != null) {
                    _dailyProblem.value = it
                }
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            _leetcodeUpcomingContestsState.update {
                it.copy(isLoading = true)
            }
            runCatching {
                val contests = userDetailsRepository.getLeetcodeUpcomingContests()
                _leetcodeUpcomingContestsState.update {
                    it.copy(isLoading = false, contests = contests.objects)
                }
            }.onFailure {
                _leetcodeUpcomingContestsState.update {
                    it.copy(isLoading = false, error = it.error)
                }
            }
        }

        loadNextVideos()
    }

    private fun getUserSubmissionPaginator() = DefaultPaginator(
        initialKey = userSubmissionState.value.page,
        onLoadUpdated = { isLoading ->
            userSubmissionState.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            userDetailsRepository.getUserRecentAcSubmissionsPaginated(nextPage, 5)
        },
        getNextKey = {
            userSubmissionState.value.page + 1
        },
        onError = { error ->
            userSubmissionState.update {
                it.copy(error = error?.message)
            }
        },
        onSuccess = { items, newKey ->
            userSubmissionState.update {
                it.copy(
                    submissions = it.submissions + items,
                    page = newKey,
                    endReached = items.isEmpty()
                )
            }
        }
    )

    private fun getVideosPaginator() = DefaultPaginator(
        initialKey = pageTokenForPlayList,
        onLoadUpdated = { isLoading ->
            videosByPlayListState.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            runCatching {
                val result = userDetailsRepository.getVideosByPlayList(
                    nextPage,
                    "PLiX7zQQX6FZMwPNeACDFvPDsUiu7AbZ8R"
                )
                pageTokenForPlayList = result.nextPageToken
                return@DefaultPaginator Result.success(result.videos)
            }.onFailure {
                return@DefaultPaginator Result.failure(it)
            }
        },
        getNextKey = {
            pageTokenForPlayList
        },
        onError = { error ->
            videosByPlayListState.update {
                it.copy(error = error?.message)
            }
        },
        onSuccess = { items, newKey ->
            videosByPlayListState.update {
                it.copy(
                    videos = it.videos + items,
                    endReached = items.isEmpty()
                )
            }
        }
    )

    fun loadNextVideos() {
        viewModelScope.launch(Dispatchers.IO) {
            videosByPlayListPagination.loadNextItems()
        }
    }

    fun loadNextAcSubmissions() {
        viewModelScope.launch(Dispatchers.IO) {
            userSubmissionPagination.loadNextItems()
        }
    }

    fun startsSync(context: Context) = UserDetailsSyncWorker.enqueuePeriodicWork(context)

    fun logout() {
        viewModelScope.launch {
            WorkManager.getInstance(context).cancelAllWork()
            userDetailsRepository.clearAllData()
            notificationDataStore.clearAll()
        }
    }

    fun setInAppReminder(contest: Contest) {
        val workManager = WorkManager.getInstance(context)

        val contestStartTime = OffsetDateTime.parse(contest.start + "Z").toInstant().toEpochMilli()
        val currentTime = System.currentTimeMillis()
        val delay = contestStartTime - currentTime - TimeUnit.MINUTES.toMillis(15)

        if (delay > 0) {
            val inputData = workDataOf(
                ContestReminderWorker.KEY_CONTEST_TITLE to contest.event,
                ContestReminderWorker.KEY_CONTEST_URL to contest.href
            )

            val contestReminderRequest = OneTimeWorkRequestBuilder<ContestReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            workManager.enqueueUniqueWork(
                "contest-reminder-${contest.id}",
                ExistingWorkPolicy.REPLACE,
                contestReminderRequest
            )

            val days = TimeUnit.MILLISECONDS.toDays(delay)
            val hours = TimeUnit.MILLISECONDS.toHours(delay) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(delay) % 60

            val toastMessage =
                "Reminder set for ${contest.event}\nWill alarm in $days days, $hours hours, and $minutes minutes."
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(
                context,
                "Contest has already started or is less than 15 minutes away.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
