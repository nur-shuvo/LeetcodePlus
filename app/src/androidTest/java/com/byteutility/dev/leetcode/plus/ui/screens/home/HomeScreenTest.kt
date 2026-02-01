package com.byteutility.dev.leetcode.plus.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test


class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun buttonShowsSetGoal_whenGoalIsNotSet() {
        var setGoalClicked = false

        composeTestRule.setContent {
            MainTopActions(
                isWeeklyGoalSet = false,
                avatarUrl = "",
                onSetGoal = { setGoalClicked = true },
                onGoalStatus = { },
                onLogoutClick = {}
            )
        }

        composeTestRule.onNodeWithText("Set Goal").assertIsDisplayed()

        composeTestRule.onNodeWithTag("goal_action_button")
            .assertTextContains("Set Goal")
            .performClick()

        assert(setGoalClicked)
    }

    @Test
    fun buttonShowsSeeStatus_whenGoalIsSet() {
        var statusClicked = false

        composeTestRule.setContent {
            MainTopActions(
                isWeeklyGoalSet = true,
                avatarUrl = "",
                onSetGoal = {},
                onGoalStatus = { statusClicked = true },
                onLogoutClick = {}
            )
        }

        composeTestRule.onNodeWithText("See Goal Status").assertIsDisplayed()

        composeTestRule.onNodeWithTag("goal_action_button")
            .assertTextContains("See Goal Status")
            .performClick()
        assert(statusClicked)
    }
}
