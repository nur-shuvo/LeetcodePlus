package com.byteutility.dev.leetcode.plus.ui.screens.home

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.byteutility.dev.leetcode.plus.core.settings.config.IntervalConfigurations
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.pagination.DefaultPaginator
import com.byteutility.dev.leetcode.plus.data.repository.problems.LocalProblemRepository
import com.byteutility.dev.leetcode.plus.data.repository.problems.ProblemsRepository
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import com.byteutility.dev.leetcode.plus.data.worker.ContestReminderWorker
import com.byteutility.dev.leetcode.plus.data.worker.ReminderNotificationWorker
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.byteutility.dev.leetcode.plus.network.responseVo.sortByStartTime
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.DifficultyStatistics
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.LeetcodeUpcomingContestsState
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.UserSubmissionState
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.VideosByPlayListState
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDetailsRepository: UserDetailsRepository,
    private val goalRepository: WeeklyGoalRepository,
    private val notificationDataStore: NotificationDataStore,
    private val userDatastore: UserDatastore,
    dailyProblemStatusMonitor: DailyProblemStatusMonitor,
    private val problemsRepository: ProblemsRepository,
    private val localProblemRepo: LocalProblemRepository
) : ViewModel() {

    // Submissions
    private val _userSubmissionState =
        MutableStateFlow(UserSubmissionState())
    val userSubmissionState = _userSubmissionState.asStateFlow()
    private val userSubmissionPagination = getUserSubmissionPaginator()

    // Videos
    private val _videosByPlayListState = MutableStateFlow(VideosByPlayListState())
    val videosByPlayListState = _videosByPlayListState.asStateFlow()
    private var pageTokenForPlayList: String? = null
    private val videosByPlayListPagination = getVideosPaginator()

    private val _leetcodeUpcomingContestsState = MutableStateFlow(LeetcodeUpcomingContestsState())
    val leetcodeUpcomingContestsState = _leetcodeUpcomingContestsState.asStateFlow()

    val userBasicInfo: StateFlow<UserBasicInfo> = flow {
        emitAll(userDetailsRepository.getUserBasicInfo().filterNotNull())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserBasicInfo()
    )

    private val _syncInterval =
        MutableStateFlow<Long>(IntervalConfigurations.DATA_SYNC_DEFAULT_INTERVAL.minutes)
    val syncInterval = _syncInterval.asStateFlow()

    val userContestInfo: StateFlow<UserContestInfo> = flow {
        emitAll(userDetailsRepository.getUserContestInfo().filterNotNull())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserContestInfo()
    )

    val userProblemSolvedInfo: StateFlow<UserProblemSolvedInfo> = flow {
        emitAll(userDetailsRepository.getUserProblemSolvedInfo().filterNotNull())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserProblemSolvedInfo()
    )

    val isWeeklyGoalSet: StateFlow<Boolean> = goalRepository.weeklyGoal
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val dailyProblem: StateFlow<LeetCodeProblem> = flow {
        emitAll(userDetailsRepository.getDailyProblem().filterNotNull())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LeetCodeProblem("", "", "")
    )

    private val _difficultyStat = MutableStateFlow(DifficultyStatistics())
    val difficultyStat = _difficultyStat.asStateFlow()

    val dailyProblemSolved = dailyProblemStatusMonitor.dailyProblemSolved.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )

    init {
        loadNextAcSubmissions()

        viewModelScope.launch {
            _syncInterval.value = userDatastore.getSyncInterval()
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
            _leetcodeUpcomingContestsState.update {
                it.copy(isLoading = true)
            }
            runCatching {
                val contests = userDetailsRepository.getLeetcodeUpcomingContests()
                _leetcodeUpcomingContestsState.update {
                    it.copy(isLoading = false, contests = contests.objects.sortByStartTime())
                }
            }.onFailure {
                _leetcodeUpcomingContestsState.update {
                    it.copy(isLoading = false, error = it.error)
                }
            }
        }

        loadNextVideos()

        scheduleBackgroundTasks()

        getRemoteProblems()

        getDifficultyStat()
    }

    fun refreshUiState() {
        refreshUserSettings()
    }

    private fun getUserSubmissionPaginator() = DefaultPaginator(
        initialKey = _userSubmissionState.value.page,
        onLoadUpdated = { isLoading ->
            _userSubmissionState.update {
                it.copy(isLoading = isLoading)
            }
        },
        onRequest = { nextPage ->
            userDetailsRepository.getUserRecentAcSubmissionsPaginated(nextPage, 5)
        },
        getNextKey = {
            _userSubmissionState.value.page + 1
        },
        onError = { error ->
            _userSubmissionState.update {
                it.copy(error = error?.message)
            }
        },
        onSuccess = { items, newKey ->
            _userSubmissionState.update {
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
            _videosByPlayListState.update {
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
            _videosByPlayListState.update {
                it.copy(error = error?.message)
            }
        },
        onSuccess = { items, newKey ->
            _videosByPlayListState.update {
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

    private fun scheduleBackgroundTasks() {
        viewModelScope.launch {
            UserDetailsSyncWorker.enqueuePeriodicWork(context, userDatastore)
            ReminderNotificationWorker.enqueuePeriodicWork(context, userDatastore)
        }
    }

    private fun refreshUserSettings() {
        viewModelScope.launch {
            _syncInterval.value = userDatastore.getSyncInterval()
        }
    }

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

    suspend fun checkInAppContestReminderStatus(contest: Contest): Boolean =
        withContext(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            val workInfos =
                workManager.getWorkInfosForUniqueWork("contest-reminder-${contest.id}").get()
            workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        }

    fun getRemoteProblems() = viewModelScope.launch {
        val timeStamp = userDatastore.getAllProblemFetchingInterval()
        if (shouldFetching(timeStamp)) {
            problemsRepository.getRemoteProblems().onSuccess {
                userDatastore.saveAllProblemFetchingInterval(System.currentTimeMillis())
            }.onFailure {
                Log.e("Error", "Unable to Fetch $it")
            }
        }
    }

    private fun shouldFetching(timeStamp: Long): Boolean {
        return System.currentTimeMillis() - timeStamp >= (7 * 24 * 60 * 60 * 1000L)
    }

    private fun getDifficultyStat() = viewModelScope.launch(Dispatchers.IO) {
        val stat = localProblemRepo.difficultyStat()
        if (stat.first != 0 && stat.second != 0 && stat.third != 0) {
            _difficultyStat.value = DifficultyStatistics(
                easyProblemCount = stat.first,
                mediumProblemCount = stat.second,
                hardProblemCount = stat.third
            )
        }
    }
}
