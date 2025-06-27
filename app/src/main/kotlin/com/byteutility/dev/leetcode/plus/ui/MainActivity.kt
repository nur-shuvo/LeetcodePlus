package com.byteutility.dev.leetcode.plus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.worker.ReminderNotificationWorker
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import com.byteutility.dev.leetcode.plus.monitor.WeeklyGoalStatusMonitor
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodePlusNavGraph
import com.byteutility.dev.leetcode.plus.ui.navigation.Login
import com.byteutility.dev.leetcode.plus.ui.navigation.Home
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

    @Inject
    lateinit var dailyProblemStatusMonitor: DailyProblemStatusMonitor

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        lifecycleScope.launch {
            val userLoggedIn =
                userDatastore.getUserBasicInfo().first()?.userName?.isNotEmpty() == true
            setContent {
                LeetcodePlusTheme {
                    val navController = rememberNavController()

                    val startDestination =
                        if (userLoggedIn) {
                            Home
                        } else {
                            Login
                        }

                    LeetCodePlusNavGraph(navController, startDestination)
                }
            }
        }
        ReminderNotificationWorker.enqueuePeriodicWork(this)
        goalStatusMonitor.start()
        dailyProblemStatusMonitor.start()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!hasPostNotificationsPermission()) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    @SuppressLint("InlinedApi")
    private fun hasPostNotificationsPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
