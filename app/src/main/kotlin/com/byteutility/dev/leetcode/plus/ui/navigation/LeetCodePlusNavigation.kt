package com.byteutility.dev.leetcode.plus.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Goal

@Serializable
object GoalStatus

@Serializable
object Login

@Serializable
object TroubleShoot

@Serializable
object VideoSolution

@Serializable
data class WebView(
    val url: String
)

class LeetCodePlusNavigation(navController: NavController) {

    val navigateToUserProfile: () -> Unit = {
        navController.navigate(Home) {
            launchSingleTop = true
            popUpTo(Login) {
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
        navController.navigate(VideoSolution) {
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
