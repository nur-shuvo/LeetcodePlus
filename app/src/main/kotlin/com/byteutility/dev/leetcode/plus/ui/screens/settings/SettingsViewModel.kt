package com.byteutility.dev.leetcode.plus.ui.screens.settings

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.WorkManager
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.data.worker.ReminderNotificationWorker
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.glance.LeetCodePlusGlanceAppWidgetReceiver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userDetailsRepository: UserDetailsRepository,
    private val notificationDataStore: NotificationDataStore,
    private val userDatastore: UserDatastore
) : ViewModel() {

    private val _userBasicInfo = MutableStateFlow(UserBasicInfo())
    val userBasicInfo: StateFlow<UserBasicInfo> = _userBasicInfo.asStateFlow()

    private val _lastSyncTime = MutableStateFlow("Not synced yet")
    val lastSyncTime: StateFlow<String> = _lastSyncTime.asStateFlow()

    private val _syncInterval = MutableStateFlow(30L)
    private val _notificationInterval = MutableStateFlow(180L)

    val syncInterval: StateFlow<Long> = _syncInterval.asStateFlow()
    val notificationInterval: StateFlow<Long> = _notificationInterval.asStateFlow()

    init {
        loadUserInfo()
        loadLastSyncTime()
        loadSyncInterval()
        loadNotificationInterval()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            userDetailsRepository.getUserBasicInfo().collect { userInfo ->
                _userBasicInfo.value = userInfo ?: UserBasicInfo()
            }
        }
    }

    private fun loadLastSyncTime() {
        viewModelScope.launch {
            userDatastore.getLastSyncTime().collect { timestamp ->
                _lastSyncTime.value = if (timestamp != null) {
                    val formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")
                    val dateTime = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(timestamp),
                        ZoneId.systemDefault()
                    )
                    dateTime.format(formatter)
                } else {
                    "Not synced yet"
                }
            }
        }
    }


    private fun loadNotificationInterval() {
        viewModelScope.launch {
            val interval = userDatastore.getNotificationInterval()
            _notificationInterval.value = interval
        }
    }

    private fun loadSyncInterval() {
        viewModelScope.launch {
            val interval = userDatastore.getSyncInterval()
            _syncInterval.value = interval
        }
    }

    fun updateSyncInterval(minutes: Long) {
        viewModelScope.launch {
            userDatastore.saveSyncInterval(minutes)
            _syncInterval.value = minutes
            UserDetailsSyncWorker.enqueuePeriodicWork(
                context,
                userDatastore,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            )
        }
    }

    fun updateNotificationInterval(minutes: Long) {
        viewModelScope.launch {
            userDatastore.saveNotificationInterval(minutes)
            _notificationInterval.value = minutes
            ReminderNotificationWorker.enqueuePeriodicWork(
                context,
                userDatastore,
                ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE
            )
        }
    }

    fun isDailyProblemWidgetPinned(): Boolean {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val ids = appWidgetManager.getAppWidgetIds(
            ComponentName(
                context,
                LeetCodePlusGlanceAppWidgetReceiver::class.java
            )
        )
        val isWidgetInstalled = ids.isNotEmpty()
        return isWidgetInstalled
    }

    fun requestPinWidget() {
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val myProvider = ComponentName(context, LeetCodePlusGlanceAppWidgetReceiver::class.java)

        if (appWidgetManager.isRequestPinAppWidgetSupported) {
            val successCallback = PendingIntent.getBroadcast(
                context, 0, Intent(context, LeetCodePlusGlanceAppWidgetReceiver::class.java),
                PendingIntent.FLAG_IMMUTABLE
            )

            appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
        } else {
            Log.e("SettingsViewModel", "Pinning widget is not supported on this device")
        }
    }

    fun logout() {
        viewModelScope.launch {
            WorkManager.getInstance(context).cancelAllWork()
            userDetailsRepository.clearAllData()
            notificationDataStore.clearAll()
        }
    }
}
