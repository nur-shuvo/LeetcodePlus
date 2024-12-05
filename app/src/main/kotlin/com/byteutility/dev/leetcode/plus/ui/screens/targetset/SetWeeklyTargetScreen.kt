package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import android.util.Log
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.ui.dialogs.WeeklyGoalSetDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetWeeklyTargetScreen(onPopCurrent: () -> Unit = {}) {
    val viewModel: SetWeeklyTargetViewModel = hiltViewModel()
    val problems = viewModel.problemsList.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.popCurrentDestination.collect {
            onPopCurrent()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Set Weekly Goals") },
                navigationIcon = {
                    IconButton(
                        onClick = { onPopCurrent() }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFABDEF5).copy(
                        alpha = 0.1f
                    )
                )
            )
        },
    ) { innerPadding ->
        var selectedProblems by remember { mutableStateOf<List<LeetCodeProblem>>(emptyList()) }
        ProblemSelection(Modifier.padding(innerPadding), problems = problems.value) {
            Log.i("SetWeeklyTargetScreen", "Problems selected for week")
            selectedProblems = it
        }
        if (selectedProblems.isNotEmpty()) {
            WeeklyGoalSetDialog {
                viewModel.handleWeeklyGoalSet(selectedProblems, it)
            }
        }
    }
}

@Composable
fun ProblemSelection(
    modifier: Modifier = Modifier,
    problems: List<LeetCodeProblem>,
    onConfirm: (List<LeetCodeProblem>) -> Unit
) {
    var selectedProblems by remember { mutableStateOf<List<LeetCodeProblem>>(emptyList()) }
    var currentPage by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }

    val itemsPerPage = 20
    val filteredProblems = problems.filter {
        it.title.contains(searchText, ignoreCase = true) ||
                it.tag.contains(searchText, ignoreCase = true) ||
                it.difficulty.contains(searchText, ignoreCase = true)
    }
    val totalPages = (filteredProblems.size + itemsPerPage - 1) / itemsPerPage
    val displayedItems = filteredProblems.drop(currentPage * itemsPerPage).take(itemsPerPage)

    Column(
        modifier = Modifier
            .background(Color.White)
            .then(modifier)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search problems...", fontSize = 18.sp) },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        LazyColumn(
            modifier = Modifier
                .weight(1.0f)
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(displayedItems) { problem ->
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { if (currentPage > 0) currentPage-- },
                enabled = currentPage > 0
            ) {
                Text("Previous")
            }

            Text("Page ${currentPage + 1} of $totalPages")

            Button(
                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                enabled = currentPage < totalPages - 1
            ) {
                Text("Next")
            }
        }

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
    val backgroundColor: Color = when (problem.difficulty) {
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
                Text(
                    text = "Difficulty: ${problem.difficulty}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onProblemSelected(it) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProblemSelectionPreview() {
    val problems = remember { getDummyProblems() }
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

fun getDummyProblems(): List<LeetCodeProblem> {
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
