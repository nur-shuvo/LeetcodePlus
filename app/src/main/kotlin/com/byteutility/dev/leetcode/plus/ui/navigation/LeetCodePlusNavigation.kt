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
data class CommonWebView(
    val url: String
)

@Serializable
object LeetCodeLoginWebView

@Serializable
data class ProblemDetails(
    val titleSlug: String
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

    val navigateLeetcodeLoginWebView: () -> Unit = {
        navController.navigate(LeetCodeLoginWebView) {
            launchSingleTop = true
        }
    }

    val navigateToWebView: (CommonWebView) -> Unit = { webView ->
        navController.navigate(
            webView
        ) {
            launchSingleTop = true
        }
    }

    val navigateToLogin: () -> Unit = {
        navController.navigate(Login) {
            launchSingleTop = true
            popUpTo(Home) {
                inclusive = true
            }
        }
    }

    val navigateToProblemDetails: (ProblemDetails) -> Unit = { problemDetails ->
        navController.navigate(
            problemDetails
        ) {
            launchSingleTop = true
        }
    }
}
