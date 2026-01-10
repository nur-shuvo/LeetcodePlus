package com.byteutility.dev.leetcode.plus.ui.screens.contest.details

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.byteutility.dev.leetcode.plus.data.worker.ContestReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.OffsetDateTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class ContestDetailViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isInAppReminderSet = MutableStateFlow(false)
    val isInAppReminderSet = _isInAppReminderSet.asStateFlow()

    fun checkInAppContestReminderStatus(contestId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val workManager = WorkManager.getInstance(context)
            val workInfos =
                workManager.getWorkInfosForUniqueWork("contest-reminder-$contestId").get()
            _isInAppReminderSet.value =
                workInfos.any { it.state == WorkInfo.State.ENQUEUED || it.state == WorkInfo.State.RUNNING }
        }
    }

    fun setInAppReminder(contestId: Int, event: String, start: String, href: String) {
        val workManager = WorkManager.getInstance(context)

        val contestStartTime = OffsetDateTime.parse(start + "Z").toInstant().toEpochMilli()
        val currentTime = System.currentTimeMillis()
        val delay = contestStartTime - currentTime - TimeUnit.MINUTES.toMillis(15)

        if (delay > 0) {
            val inputData = workDataOf(
                ContestReminderWorker.KEY_CONTEST_TITLE to event,
                ContestReminderWorker.KEY_CONTEST_URL to href
            )

            val contestReminderRequest = OneTimeWorkRequestBuilder<ContestReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(inputData)
                .build()

            workManager.enqueueUniqueWork(
                "contest-reminder-$contestId",
                ExistingWorkPolicy.REPLACE,
                contestReminderRequest
            )

            val days = TimeUnit.MILLISECONDS.toDays(delay)
            val hours = TimeUnit.MILLISECONDS.toHours(delay) % 24
            val minutes = TimeUnit.MILLISECONDS.toMinutes(delay) % 60

            val toastMessage =
                "Reminder set for $event\nWill alarm in $days days, $hours hours, and $minutes minutes."

            viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show()
                _isInAppReminderSet.value = true
            }
        } else {
            viewModelScope.launch(Dispatchers.Main) {
                Toast.makeText(
                    context,
                    "Contest has already started or is less than 15 minutes away.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}
