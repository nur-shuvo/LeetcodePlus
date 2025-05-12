package com.byteutility.dev.leetcode.plus.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.byteutility.dev.leetcode.plus.troubleshoot.TroubleShootScreen
import com.byteutility.dev.leetcode.plus.ui.screens.login.UserLoginScreen
import com.byteutility.dev.leetcode.plus.ui.screens.solutions.VideoSolutionsScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetset.SetWeeklyTargetScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetstatus.GoalProgressScreen
import com.byteutility.dev.leetcode.plus.ui.screens.userdetails.UserProfileScreen
import com.byteutility.dev.leetcode.plus.ui.screens.webview.WebViewScreen

@Composable
fun LeetCodePlusNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Any = Login
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        val navigationActions = LeetCodePlusNavigation(navController)

        composable<Login> {
            UserLoginScreen {
                navigationActions.navigateToUserProfile()
            }
        }

        composable<Goal> {
            SetWeeklyTargetScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { url ->
                    navigationActions.navigateToWebView(
                        WebView(
                            url
                        )
                    )
                }
            )
        }

        composable<GoalStatus> {
            GoalProgressScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { url ->
                    navigationActions.navigateToWebView(
                        WebView(
                            url
                        )
                    )
                }
            )
        }

        composable<Profile> {
            UserProfileScreen(
                {
                    navigationActions.navigateToSetGoal()
                }, {
                    navigationActions.navigateToGoalStatus()
                },
                {
                    navigationActions.navigateToTroubleShoot()
                },
                { url ->
                    navigationActions.navigateToWebView(
                        WebView(url),
                    )
                },
                {
                    navigationActions.navigateToVideoSolutions()
                }
            )
        }
        composable<TroubleShoot> {
            TroubleShootScreen()
        }

        composable(route = LeetCodePlusNavigationDestinations.VIDEO_SOLUTIONS_ROUTE) {
            VideoSolutionsScreen() {
                navigationActions.popCurrentDestination()
            }
        }

        composable<WebView> { backstackEntry ->
            val webView = backstackEntry.toRoute<WebView>()
            val url = webView.url
            val decodedUrl = Uri.decode(url)
            WebViewScreen(decodedUrl) {
                navigationActions.popCurrentDestination()
            }
        }
    }
}
