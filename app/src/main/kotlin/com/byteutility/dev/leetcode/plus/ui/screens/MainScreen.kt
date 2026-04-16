package com.byteutility.dev.leetcode.plus.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.byteutility.dev.leetcode.plus.ui.theme.LabelColor
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemsGreen

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
                modifier = Modifier
                    .graphicsLayer {
                        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        clip = true
                    },
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                NavigationBar(
                    modifier = Modifier.height(72.dp),
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination

                    bottomNavItems.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any {
                            it.route == screen.route::class.qualifiedName
                        } == true

                        NavigationBarItem(
                            icon = {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(if (isSelected) 26.dp else 24.dp)
                                    )
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .padding(top = 4.dp)
                                                .size(4.dp)
                                                .background(ProblemsGreen, CircleShape)
                                        )
                                    }
                                }
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    style = TextStyle(
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = ProblemsGreen,
                                selectedTextColor = ProblemsGreen,
                                unselectedIconColor = LabelColor.copy(alpha = 0.6f),
                                unselectedTextColor = LabelColor.copy(alpha = 0.6f),
                                indicatorColor = Color.Transparent
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
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
