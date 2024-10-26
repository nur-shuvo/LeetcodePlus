package com.byteutility.dev.leetcode.plus.ui

import androidx.navigation.NavController


object LeetCodePlusNavigationDestinations {
    const val USER_PROFILE_ROUTE = "user_profile"
    const val SET_GOAL_ROUTE = "set_goal"
    const val LOGIN_ROUTE = "login"
    const val GOAL_STATUS_ROUTE = "goal_status"
}

class LeetCodePlusNavigation(navController: NavController) {

    val navigateToUserProfile: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE) {
            launchSingleTop = true
            popUpTo(LeetCodePlusNavigationDestinations.LOGIN_ROUTE) {
                inclusive = true
            }
        }
    }

    val navigateToSetGoal: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.SET_GOAL_ROUTE) {
            launchSingleTop = true
        }
    }

    val navigateToGoalStatus: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.GOAL_STATUS_ROUTE) {
            launchSingleTop = true
        }
    }


    val navigateToLogin: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.LOGIN_ROUTE) {
            launchSingleTop = true
        }
    }

    val popCurrentDestination: () -> Unit = {
        navController.popBackStack()
    }
}
