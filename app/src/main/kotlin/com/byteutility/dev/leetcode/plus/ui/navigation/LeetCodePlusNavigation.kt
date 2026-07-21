package com.byteutility.dev.leetcode.plus.ui.navigation

import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object Home

@Serializable
object Main

@Serializable
object AllProblems

@Serializable
object Settings

@Serializable
object AlgoVisualizer

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

@Serializable
data class ContestDetail(
    val contestId: Int,
    val event: String,
    val start: String,
    val end: String,
    val duration: Int,
    val href: String
)

@Serializable
object InterviewProfileSetup

@Serializable
object InterviewSlotPicker

@Serializable
object InterviewSessionList

@Serializable
data class InterviewSessionDetail(
    val sessionId: String
)

@Serializable
data class InterviewFeedback(
    val sessionId: String,
    val rateeUid: String
)

class LeetCodePlusNavigation(navController: NavController) {

    val navigateToMainScreen: () -> Unit = {
        navController.navigate(Main) {
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

    val navigateToAllProblems: () -> Unit = {
        navController.navigate(AllProblems) {
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
            popUpTo(Main) {
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

    val navigateToContestDetail: (ContestDetail) -> Unit = { contestDetail ->
        navController.navigate(contestDetail) {
            launchSingleTop = true
        }
    }

    val navigateToInterviewProfileSetup: () -> Unit = {
        navController.navigate(InterviewProfileSetup) {
            // Also reached after signing out from InterviewSessionList - pop back to Main so a
            // stale session list isn't left underneath on the back stack.
            popUpTo(Main) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    val navigateToInterviewSlotPicker: () -> Unit = {
        navController.navigate(InterviewSlotPicker) {
            launchSingleTop = true
        }
    }

    val navigateToInterviewSessionList: () -> Unit = {
        navController.navigate(InterviewSessionList) {
            // Reached from either InterviewProfileSetup (after sign-in/save) or
            // InterviewSlotPicker (after booking) - pop both of those (and any earlier session
            // list instance) so the back stack is always just Main -> InterviewSessionList,
            // never a pile of intermediate screens to back through.
            popUpTo(Main) {
                inclusive = false
            }
            launchSingleTop = true
        }
    }

    val navigateToInterviewSessionDetail: (InterviewSessionDetail) -> Unit = { sessionDetail ->
        navController.navigate(sessionDetail) {
            launchSingleTop = true
        }
    }

    val navigateToInterviewFeedback: (InterviewFeedback) -> Unit = { feedback ->
        navController.navigate(feedback) {
            launchSingleTop = true
        }
    }
}
