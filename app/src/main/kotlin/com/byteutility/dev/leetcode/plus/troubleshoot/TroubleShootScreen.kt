package com.byteutility.dev.leetcode.plus.troubleshoot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.repository.weeklyGoal.WeeklyGoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

/**
 * This screen is for troubleshooting debug purpose, so implementation is kept as much as trivial
 */
@Composable
@Preview
fun TroubleShootScreen(viewModel: TroubleShootScreenViewModel = hiltViewModel()) {
    var goalOutput by remember { mutableStateOf("") }
    var dailyProblemOutput by remember { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.Center) {
        Spacer(modifier = Modifier.height(50.dp))
        TextButton({
            goalOutput = ""
            goalOutput = runBlocking { viewModel.getWeeklyGoal() }
        }) {
            Text("Weekly goal data")
        }
        Text(text = goalOutput)

        TextButton({
            dailyProblemOutput = ""
            dailyProblemOutput = runBlocking { viewModel.getDailyProblem() }
        }) {
            Text("Daily Problem data")
        }
        Text(text = dailyProblemOutput)
    }
}

@HiltViewModel
class TroubleShootScreenViewModel @Inject constructor(
    private val goalRepository: WeeklyGoalRepository,
    private val userDatastore: UserDatastore,
) : ViewModel() {
    suspend fun getWeeklyGoal(): String {
        return goalRepository.weeklyGoal.firstOrNull().toString()
    }

    suspend fun getDailyProblem(): String {
        return userDatastore.getDailyProblem().firstOrNull().toString()
    }
}