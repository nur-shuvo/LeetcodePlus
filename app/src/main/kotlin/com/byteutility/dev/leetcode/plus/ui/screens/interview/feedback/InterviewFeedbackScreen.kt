package com.byteutility.dev.leetcode.plus.ui.screens.interview.feedback

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private const val MAX_RATING = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewFeedbackScreen(
    onSubmitted: () -> Unit = {},
    viewModel: InterviewFeedbackViewModel = hiltViewModel()
) {
    val submitState by viewModel.submitState.collectAsStateWithLifecycle()

    var communicationRating by remember { mutableIntStateOf(0) }
    var problemSolvingRating by remember { mutableIntStateOf(0) }
    var wouldMatchAgain by remember { mutableStateOf(true) }
    var didNotShowUp by remember { mutableStateOf(false) }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(submitState) {
        if (submitState is SubmitUiState.Submitted) {
            onSubmitted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Rate your peer") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = didNotShowUp,
                    onCheckedChange = { didNotShowUp = it },
                    colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.error)
                )
                Text("My peer didn't show up", modifier = Modifier.padding(top = 12.dp))
            }

            if (!didNotShowUp) {
                RatingRow(
                    label = "Communication",
                    rating = communicationRating,
                    onRatingChange = { communicationRating = it }
                )
                RatingRow(
                    label = "Problem solving",
                    rating = problemSolvingRating,
                    onRatingChange = { problemSolvingRating = it }
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = wouldMatchAgain, onCheckedChange = { wouldMatchAgain = it })
                    Text("Would match again", modifier = Modifier.padding(top = 12.dp))
                }
            }

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (submitState is SubmitUiState.ValidationError) {
                Text(
                    text = "Please rate both categories before submitting.",
                    color = MaterialTheme.colorScheme.error
                )
            }
            if (submitState is SubmitUiState.Error) {
                Text(
                    text = "Couldn't submit feedback, please try again.",
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    viewModel.submit(
                        communicationRating,
                        problemSolvingRating,
                        wouldMatchAgain,
                        notes,
                        didNotShowUp
                    )
                },
                enabled = submitState !is SubmitUiState.Submitting
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit")
            }
        }
    }
}

@Composable
private fun RatingRow(label: String, rating: Int, onRatingChange: (Int) -> Unit) {
    Column {
        Text(label, style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (value in 1..MAX_RATING) {
                val starColor = if (value <= rating) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }
                Icon(
                    imageVector = if (value <= rating) Icons.Filled.Star else Icons.Filled.StarBorder,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier
                        .padding(4.dp)
                        .clickable { onRatingChange(value) }
                )
            }
        }
    }
}
