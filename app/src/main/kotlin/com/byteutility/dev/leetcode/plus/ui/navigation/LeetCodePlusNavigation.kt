package com.byteutility.dev.leetcode.plus.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable


object LeetCodePlusNavigationDestinations {
    const val USER_PROFILE_ROUTE = "user_profile"
    const val SET_GOAL_ROUTE = "set_goal"
    const val LOGIN_ROUTE = "login"
    const val GOAL_STATUS_ROUTE = "goal_status"
    const val VIDEO_SOLUTIONS_ROUTE = "video_solutions"
}

@Serializable
object Home

@Serializable
object Profile

@Serializable
object Goal

@Serializable
object GoalStatus

@Serializable
object Login

@Serializable
object TroubleShoot

@Serializable
data class WebView(
    val url: String
)

class LeetCodePlusNavigation(navController: NavController) {

    val navigateToUserProfile: () -> Unit = {
        navController.navigate(Profile) {
            launchSingleTop = true
            popUpTo(LeetCodePlusNavigationDestinations.LOGIN_ROUTE) {
                inclusive = true
            }
        }
    }

    val navigateToSetGoal: () -> Unit = {
        navController.navigate(Goal) {
            launchSingleTop = true
        }
    }

    val navigateToGoalStatus: () -> Unit = {
        navController.navigate(GoalStatus) {
            launchSingleTop = true
        }
    }

    val popCurrentDestination: () -> Unit = {
        navController.popBackStack()
    }

    val navigateToTroubleShoot: () -> Unit = {
        navController.navigate(TroubleShoot) {
            launchSingleTop = true
        }
    }

    val navigateToVideoSolutions: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.VIDEO_SOLUTIONS_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToWebView: (WebView) -> Unit = { webView ->
        navController.navigate(
            webView
        ) {
            launchSingleTop = true
        }
    }
}
