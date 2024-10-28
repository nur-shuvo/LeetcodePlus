package com.byteutility.dev.leetcode.plus.ui.targetstatus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalProgressScreen() {
    val viewmodel: GoalProgressViewModel = hiltViewModel()
    viewmodel.init()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Progress") },
            )
        },
    ) { paddingValues ->
        ProgressScreenContent(uiState.problemsWithStatus, Modifier.padding(paddingValues))
    }
}

@Composable
fun ProgressScreenContent(
    problemsWithStatus: List<ProblemStatus>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .then(modifier)
    ) {
        for (problem in problemsWithStatus) {
            ProblemCard(problem)
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProblemCard(problemStatus: ProblemStatus) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(16.dp)
            ) {
                Text(
                    text = problemStatus.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge
                )
                Text("Status: ${problemStatus.status}")
                Text("Attempts Count: ${problemStatus.attemptsCount}")
            }
            if (problemStatus.status == "In Progress" || problemStatus.status == "Not Started") {
                Image(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(10.dp),
                    painter = painterResource(R.drawable.baseline_pending_24),
                    contentDescription = ""
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .padding(10.dp)
                        .background(Color.Green, shape = CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.Center),
                        imageVector = done,
                        contentDescription = ""
                    )
                }
            }
        }
    }
}

@Composable
@Preview
fun LeetCodeProgressScreenPreview() {
    val problemStatuses = remember {
        listOf(
            ProblemStatus("Two Sum", "Not Started", "Easy", 0),
            ProblemStatus(
                "Longest Substring Without Repeating Characters",
                "In Progress",
                "Medium",
                1,
            ),
            ProblemStatus(
                "Median of Two Sorted Arrays",
                "Completed",
                "Hard",
                1,
            ),
            ProblemStatus("Add Two Numbers", "Not Started", "Medium", 0),
            ProblemStatus("Valid Parentheses", "In Progress", "Easy", 5),
            ProblemStatus("Merge Two Sorted Lists", "Not Started", "Easy", 0),
            ProblemStatus("Climbing Stairs", "Completed", "Easy", 2)
        )
    }
    ProgressScreenContent(problemStatuses)
}
