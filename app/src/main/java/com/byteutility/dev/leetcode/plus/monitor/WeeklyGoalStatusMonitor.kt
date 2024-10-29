package com.byteutility.dev.leetcode.plus.monitor

import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WeeklyGoalStatusMonitor @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun start() {
        scope.launch {
            userDetailsRepository.getUserRecentSubmissions().collect {
                
            }
        }
    }
}