package com.byteutility.dev.leetcode.plus.ui

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination


object LeetCodePlusNavigationDestinations {
    const val USER_PROFILE_ROUTE = "user_profile"
    const val SET_GOAL_ROUTE = "set_goal"
    const val LOGIN_ROUTE = "login"
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


    val navigateToLogin: () -> Unit = {
        navController.navigate(LeetCodePlusNavigationDestinations.LOGIN_ROUTE) {
            launchSingleTop = true
        }
    }
}
