package com.byteutility.dev.leetcode.plus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.worker.ReminderNotificationWorker
import com.byteutility.dev.leetcode.plus.data.worker.UserDetailsSyncWorker
import com.byteutility.dev.leetcode.plus.monitor.WeeklyGoalStatusMonitor
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodePlusNavGraph
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodePlusNavigationDestinations
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userDatastore: UserDatastore

    @Inject
    lateinit var goalStatusMonitor: WeeklyGoalStatusMonitor

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
        } else {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        lifecycleScope.launch {
            val userLoggedIn =
                userDatastore.getUserBasicInfo().first()?.userName?.isNotEmpty() == true
            setContent {
                LeetcodePlusTheme {
                    val navController = rememberNavController()

                    val startDestination =
                        if (userLoggedIn) {
                            LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE
                        } else {
                            LeetCodePlusNavigationDestinations.LOGIN_ROUTE
                        }

                    LeetCodePlusNavGraph(navController, startDestination)
                }
            }
        }
        UserDetailsSyncWorker.enqueuePeriodicWork(this)
        ReminderNotificationWorker.enqueuePeriodicWork(this)
        goalStatusMonitor.start()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPostNotificationsPermission()) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
            }
        } else {
        }
    }

    @SuppressLint("InlinedApi")
    private fun hasPostNotificationsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
