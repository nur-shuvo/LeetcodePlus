package com.byteutility.dev.leetcode.plus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.ui.screens.login.UserLoginScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetset.SetWeeklyTargetScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetstatus.GoalProgressScreen
import com.byteutility.dev.leetcode.plus.ui.screens.userdetails.UserProfileScreen


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
            SetWeeklyTargetScreen {
                navigationActions.popCurrentDestination()
            }
        }

        composable(route = LeetCodePlusNavigationDestinations.GOAL_STATUS_ROUTE) {
            GoalProgressScreen()
        }

        composable(route = LeetCodePlusNavigationDestinations.USER_PROFILE_ROUTE) {
            UserProfileScreen(
                {
                    navigationActions.navigateToSetGoal()
                }, {
                    navigationActions.navigateToGoalStatus()
                }
            )
        }
    }
}
