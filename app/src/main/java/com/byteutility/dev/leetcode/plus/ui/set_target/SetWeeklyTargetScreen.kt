package com.byteutility.dev.leetcode.plus.ui.set_target

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SetWeeklyTargetScreen() {
    val viewModel: SetWeeklyTargetViewModel = hiltViewModel()
    viewModel.getProblemsByLimit(5L)
}