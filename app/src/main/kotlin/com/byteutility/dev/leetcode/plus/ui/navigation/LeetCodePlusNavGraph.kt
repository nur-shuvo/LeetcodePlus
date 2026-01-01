package com.byteutility.dev.leetcode.plus.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.byteutility.dev.leetcode.plus.troubleshoot.TroubleShootScreen
import com.byteutility.dev.leetcode.plus.ui.screens.MainScreen
import com.byteutility.dev.leetcode.plus.ui.screens.contest.details.ContestDetailScreen
import com.byteutility.dev.leetcode.plus.ui.screens.leetcodelogin.LeetCodeLoginWebView
import com.byteutility.dev.leetcode.plus.ui.screens.login.UserLoginScreen
import com.byteutility.dev.leetcode.plus.ui.screens.problem.details.ProblemDetailsScreen
import com.byteutility.dev.leetcode.plus.ui.screens.solutions.VideoSolutionsScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetset.SetWeeklyTargetScreen
import com.byteutility.dev.leetcode.plus.ui.screens.targetstatus.GoalProgressScreen
import com.byteutility.dev.leetcode.plus.ui.screens.webview.CommonWebViewScreen

@Composable
fun LeetCodePlusNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: Any = Login
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        val navigationActions = LeetCodePlusNavigation(navController)

        composable<Login> {
            UserLoginScreen {
                navigationActions.navigateToMainScreen()
            }
        }

        composable<Main> {
            MainScreen(navController)
        }

        composable<Goal> {
            SetWeeklyTargetScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { titleSLug ->
                    navigationActions.navigateToProblemDetails(
                        ProblemDetails(titleSLug)
                    )
                }
            )
        }

        composable<GoalStatus> {
            GoalProgressScreen(
                {
                    navigationActions.popCurrentDestination()
                },
                { titleSlug ->
                    navigationActions.navigateToProblemDetails(
                        ProblemDetails(
                            titleSlug
                        )
                    )
                }
            )
        }

        composable<TroubleShoot> {
            TroubleShootScreen()
        }

        composable<VideoSolution> {
            VideoSolutionsScreen {
                navigationActions.popCurrentDestination()
            }
        }

        composable<LeetCodeLoginWebView> {
            LeetCodeLoginWebView {
                navigationActions.popCurrentDestination()
            }
        }

        composable<CommonWebView> { backstackEntry ->
            val commonWebView = backstackEntry.toRoute<CommonWebView>()
            val url = commonWebView.url
            val decodedUrl = Uri.decode(url)
            CommonWebViewScreen(decodedUrl) {
                navigationActions.popCurrentDestination()
            }
        }

        composable<ProblemDetails> { backstackEntry ->
            val problemDetails = backstackEntry.toRoute<ProblemDetails>()
            val titleSlug = problemDetails.titleSlug
            ProblemDetailsScreen(titleSlug) {
                navigationActions.navigateLeetcodeLoginWebView()
            }
        }

        composable<ContestDetail> { backstackEntry ->
            val contestDetail = backstackEntry.toRoute<ContestDetail>()
            ContestDetailScreen(
                contestId = contestDetail.contestId,
                event = contestDetail.event,
                start = contestDetail.start,
                end = contestDetail.end,
                duration = contestDetail.duration,
                href = contestDetail.href,
                onBack = {
                    navigationActions.popCurrentDestination()
                }
            )
        }
    }
}
