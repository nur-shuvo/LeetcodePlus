package com.byteutility.dev.leetcode.plus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.byteutility.dev.leetcode.plus.ui.navigation.AllProblems
import com.byteutility.dev.leetcode.plus.ui.navigation.ContestDetail
import com.byteutility.dev.leetcode.plus.ui.navigation.Home
import com.byteutility.dev.leetcode.plus.ui.navigation.LeetCodePlusNavigation
import com.byteutility.dev.leetcode.plus.ui.navigation.ProblemDetails
import com.byteutility.dev.leetcode.plus.ui.navigation.Settings
import com.byteutility.dev.leetcode.plus.ui.screens.allproblems.AllProblemsScreen
import com.byteutility.dev.leetcode.plus.ui.screens.home.HomeScreen
import com.byteutility.dev.leetcode.plus.ui.screens.settings.SettingsScreen

sealed class BottomNavScreen(val route: Any, val label: String, val icon: ImageVector) {
    object HomeWithLabel : BottomNavScreen(
        route = Home,
        label = "Home",
        icon = Icons.Default.Home
    )

    object AllProblemsWithLabel : BottomNavScreen(
        route = AllProblems,
        label = "Problems",
        icon = Icons.AutoMirrored.Filled.List
    )

    object SettingsWithLabel : BottomNavScreen(
        route = Settings,
        label = "Settings",
        icon = Icons.Default.Settings
    )
}

val bottomNavItems = listOf(
    BottomNavScreen.HomeWithLabel,
    BottomNavScreen.AllProblemsWithLabel,
    BottomNavScreen.SettingsWithLabel
)

@Composable
fun MainScreen(mainNavController: NavHostController) {
    val navController = rememberNavController()
    val navigationActions = LeetCodePlusNavigation(mainNavController)

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 14.dp,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            ) {
                NavigationBar(
                    modifier = Modifier
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFBECEC3),
                                    Color(0xFF498A5C),
                                    Color(0xFFBECEC3),
                                )
                            )
                        ),
                    containerColor = Color.Transparent
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    bottomNavItems.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route::class.qualifiedName } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Home,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Home> {
                HomeScreen(
                    onSetGoal = {
                        navigationActions.navigateToSetGoal()
                    }, onGoalStatus = {
                        navigationActions.navigateToGoalStatus()
                    },
                    onTroubleShoot = {
                        navigationActions.navigateToTroubleShoot()
                    },
                    onNavigateToProblemDetails = { titleSlug ->
                        navigationActions.navigateToProblemDetails(
                            ProblemDetails(titleSlug)
                        )
                    },
                    onNavigateToVideoSolutions = {
                        navigationActions.navigateToVideoSolutions()
                    },
                    onNavigateToAllProblems = {
                        navController.navigate(AllProblems) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToContestDetail = { contest ->
                        navigationActions.navigateToContestDetail(
                            ContestDetail(
                                contestId = contest.id,
                                event = contest.event,
                                start = contest.start,
                                end = contest.end,
                                duration = contest.duration,
                                href = contest.href
                            )
                        )
                    },
                    onLogout = {
                        navigationActions.navigateToLogin()
                    }
                )
            }
            composable<AllProblems> {
                AllProblemsScreen(
                    {
                        navController.popBackStack()
                    },
                    { titleSLug ->
                        navigationActions.navigateToProblemDetails(
                            ProblemDetails(titleSLug)
                        )
                    }
                )
            }
            composable<Settings> {
                SettingsScreen(
                    onLogout = {
                        navigationActions.navigateToLogin()
                    }
                )
            }
        }
    }
}
