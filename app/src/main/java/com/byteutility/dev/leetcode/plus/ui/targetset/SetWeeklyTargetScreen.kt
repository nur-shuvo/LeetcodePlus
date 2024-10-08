package com.byteutility.dev.leetcode.plus.ui.targetset

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetWeeklyTargetScreen() {
    val viewModel: SetWeeklyTargetViewModel = hiltViewModel()
    viewModel.getProblemsByLimit(5L)
    val problems = remember { getProblemsFromApi() }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Set Weekly Goals") })
        },
    ) { innerPadding ->
        ProblemSelection(Modifier.padding(innerPadding), problems = problems) { selectedProblems ->
            println("Confirmed Problems: $selectedProblems")
        }
    }
}

@Composable
fun ProblemSelection(modifier: Modifier = Modifier, problems: List<LeetCodeProblem>, onConfirm: (List<LeetCodeProblem>) -> Unit) {
    var selectedProblems by remember { mutableStateOf<List<LeetCodeProblem>>(emptyList()) }

    Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .then(modifier),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(problems.size) { index ->
                val problem = problems[index]
                ProblemItem(
                    problem = problem,
                    isSelected = selectedProblems.contains(problem),
                    onProblemSelected = { selected ->
                        if (selectedProblems.size < 7 || selectedProblems.contains(problem)) {
                            selectedProblems = if (selected) {
                                selectedProblems + problem
                            } else {
                                selectedProblems - problem
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onConfirm(selectedProblems) },
            enabled = selectedProblems.size == 7,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirm")
        }
    }
}

@Composable
fun ProblemItem(
    problem: LeetCodeProblem,
    isSelected: Boolean,
    onProblemSelected: (Boolean) -> Unit
) {
    val backgroundColor: Color = when(problem.difficulty) {
        "Easy" -> Color(0xFFE0F7FA)
        "Medium" -> Color(0xFFFFF9C4)
        "Hard" -> Color(0xFFFFCDD2)
        else -> Color(0xFFE0F7FA)
    }
    Card(
        shape = RoundedCornerShape(16.dp), // Circular border
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(backgroundColor)
                .clickable { onProblemSelected(!isSelected) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Text(text = problem.title, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Tag: ${problem.tag}", style = MaterialTheme.typography.bodySmall)
                Text(text = "Difficulty: ${problem.difficulty}", style = MaterialTheme.typography.bodySmall)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onProblemSelected(it) }
            )
        }
    }
}

// Dummy function to simulate API call
fun getProblemsFromApi(): List<LeetCodeProblem> {
    return listOf(
        LeetCodeProblem("Two Sum", "Easy", "Array"),
        LeetCodeProblem("Binary Tree Level Order Traversal", "Medium", "Tree"),
        LeetCodeProblem("Longest Substring Without Repeating Characters", "Medium", "String"),
        LeetCodeProblem("Median of Two Sorted Arrays", "Hard", "Array"),
        LeetCodeProblem("Search in Rotated Sorted Array", "Medium", "Binary Search"),
        LeetCodeProblem("Longest Palindromic Substring", "Medium", "String"),
        LeetCodeProblem("Valid Parentheses", "Easy", "Stack"),
        LeetCodeProblem("Merge Intervals", "Medium", "Sorting"),
        LeetCodeProblem("Word Ladder", "Hard", "Graph")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProblemSelectionApp() {
    val problems = remember { getProblemsFromApi() }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Set Weekly Goals") })
        },
    ) { innerPadding ->
        ProblemSelection(Modifier.padding(innerPadding), problems = problems) { selectedProblems ->
            println("Confirmed Problems: $selectedProblems")
        }
    }
}
