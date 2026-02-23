package com.byteutility.dev.leetcode.plus.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import com.byteutility.dev.leetcode.plus.BuildConfig
import com.byteutility.dev.leetcode.plus.ui.networkmonitor.NetworkMonitorActivity
import com.byteutility.dev.leetcode.plus.ui.networkmonitor.ShakeDetector
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.util.Consumer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.monitor.DailyProblemStatusMonitor
import com.byteutility.dev.leetcode.plus.monitor.WeeklyGoalStatusMonitor
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodeLoginWebView
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodePlusNavGraph
import com.byteutility.dev.leetcode.plus.ui.navigation.Login
import com.byteutility.dev.leetcode.plus.ui.navigation.Main
import com.byteutility.dev.leetcode.plus.ui.navigation.ProblemDetails
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

    private var extraStartDestination: String? = null
    private var dailyProblemTitleSlug: String? = null

    private lateinit var sensorManager: SensorManager
    private lateinit var shakeDetector: ShakeDetector

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean -> }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
//        enableEdgeToEdge()
        installSplashScreen()
        lifecycleScope.launch {
            val userLoggedIn =
                userDatastore.getUserBasicInfo().first()?.userName?.isNotEmpty() == true
            setContent {
                LeetcodePlusTheme {
                    val navController = rememberNavController()
                    val context = LocalContext.current

                    LaunchedEffect(Unit) {
                        val activity = context as ComponentActivity
                        handleShareIntent(activity.intent, navController)
                    }

                    val startDestination =
                        if (userLoggedIn) {
                            if (extraStartDestination == null) {
                                Main
                            } else {
                                when (extraStartDestination) {
                                    "leetcode_login_webview" -> LeetCodeLoginWebView
                                    else -> Login
                                }
                            }
                        } else {
                            Login
                        }

                    DisposableEffect(Unit) {
                        val listener = Consumer<Intent> { intent ->
                            handleShareIntent(intent, navController)
                        }
                        (context as ComponentActivity).addOnNewIntentListener(listener)
                        onDispose {
                            (context as ComponentActivity).removeOnNewIntentListener(
                                listener
                            )
                        }
                    }

                    LeetCodePlusNavGraph(navController, startDestination)

                    // Navigate to problem details if opened from daily problem notification
                    dailyProblemTitleSlug?.let { titleSlug ->
                        if (titleSlug.isNotEmpty() && userLoggedIn) {
                            LaunchedEffect(titleSlug) {
                                navController.navigate(ProblemDetails(titleSlug))
                            }
                        }
                    }
                }
            }
        }
        goalStatusMonitor.start()
        dailyProblemStatusMonitor.start()
        initShakeDetector()
    }

    private fun initShakeDetector() {
        sensorManager = getSystemService()!!
        shakeDetector = ShakeDetector {
            startActivity(Intent(this, NetworkMonitorActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        if (BuildConfig.DEBUG) {
            sensorManager.registerListener(
                shakeDetector,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_UI,
            )
        }
    }

    override fun onPause() {
        super.onPause()
        if (BuildConfig.DEBUG) {
            sensorManager.unregisterListener(shakeDetector)
        }
    }

    private fun handleShareIntent(intent: Intent?, navController: NavController) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val url = intent.getStringExtra(Intent.EXTRA_TEXT)
            val slug = url?.substringAfter("/problems/")?.substringBefore("/")
            if (!slug.isNullOrEmpty() && url.contains("leetcode.com")) {
                navController.navigate(ProblemDetails(slug))
            }
        }
    }

    private fun init() {
        extraStartDestination = intent.getStringExtra("startDestination")
        dailyProblemTitleSlug = intent.getStringExtra("dailyProblemTitleSlug")
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
