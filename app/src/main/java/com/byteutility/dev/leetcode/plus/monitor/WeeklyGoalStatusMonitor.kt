package com.byteutility.dev.leetcode.plus.monitor

import android.util.Log
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyGoalStatusMonitor @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var _showNotification = MutableSharedFlow<String>()
    val showNotification = _showNotification.asSharedFlow()

    fun start() {
        scope.launch {
            userDetailsRepository.getUserLastSubmissions().collect {
                Log.i("WeeklyGoalStatusMonitor", "last submissions ${it?.joinToString(" ")}")
            }
        }
    }
}