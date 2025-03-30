package com.byteutility.dev.leetcode.plus.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    startDestination: String = LeetCodePlusNavigationDestinations.LOGIN_ROUTE
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        val navigationActions = LeetCodePlusNavigation(navController)

        composable(route = LeetCodePlusNavigationDestinations.LOGIN_ROUTE) {
            UserLoginScreen {
                navigationActions.navigateToUserProfile()
            }
        }

        composable(route = LeetCodePlusNavigationDestinations.SET_GOAL_ROUTE) {
            SetWeeklyTargetScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { url ->
                    navigationActions.navigateToWebView(
                        url,
                        LeetCodePlusNavigationDestinations.SET_GOAL_ROUTE
                    )
                }
            )
        }

        composable(route = LeetCodePlusNavigationDestinations.GOAL_STATUS_ROUTE) {
            GoalProgressScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { url ->
                    navigationActions.navigateToWebView(
                        url,
                        LeetCodePlusNavigationDestinations.GOAL_STATUS_ROUTE
                    )
                }
            )
        }

        composable(route = LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE) {
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
                        url,
                        LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE
                    )
                },
                {
                    navigationActions.navigateToVideoSolutions()
                }
            )
        }

        composable(route = LeetCodePlusNavigationDestinations.TROUBLE_SHOOT_ROUTE) {
            TroubleShootScreen()
        }

        composable(route = LeetCodePlusNavigationDestinations.VIDEO_SOLUTIONS_ROUTE) {
            VideoSolutionsScreen() {
                navigationActions.popCurrentDestination()
            }
        }

        composable(
            route = LeetCodePlusNavigationDestinations.WEB_VIEW_ROUTE + "/" + "{url}" + "/" + "{caller}",
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("caller") { type = NavType.StringType })
        ) { backstackEntry ->
            val url = backstackEntry.arguments?.getString("url") ?: ""
            val caller = backstackEntry.arguments?.getString("caller") ?: ""
            val decodedUrl = Uri.decode(url)
            WebViewScreen(decodedUrl, caller) {
                navigationActions.popCurrentDestination()
            }
        }
    }
}
