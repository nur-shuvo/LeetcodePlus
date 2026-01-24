package com.byteutility.dev.leetcode.plus.ui.screens.targetstatus

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import com.byteutility.dev.leetcode.plus.ui.common.AdBannerAdaptive
import com.byteutility.dev.leetcode.plus.ui.common.done
import com.byteutility.dev.leetcode.plus.ui.dialogs.WeeklyGoalResetDialog
import com.byteutility.dev.leetcode.plus.ui.model.ProgressUiState

@Composable
fun GoalProgressScreen(
    onPopCurrent: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit,
) {
    val viewmodel: GoalProgressViewModel = hiltViewModel()
    viewmodel.init()
    val uiState by viewmodel.uiState.collectAsStateWithLifecycle()
    var showGoalResetDialog by rememberSaveable { mutableStateOf(false) }
    ProgressScreenContent(
        uiState = uiState,
        onPopCurrent = onPopCurrent,
        onNavigateToProblemDetails = onNavigateToProblemDetails,
        onResetGoal = {
            if (uiState.problemsWithStatus.isNotEmpty()) {
                showGoalResetDialog = true
            }
        }
    )

    if (showGoalResetDialog) {
        WeeklyGoalResetDialog(
            onConfirm = {
                viewmodel.resetGoal()
                showGoalResetDialog = false
            },
            onDismiss = {
                showGoalResetDialog = false
            }
        )
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun ProgressScreenContent(
    uiState: ProgressUiState,
    onPopCurrent: () -> Unit,
    onResetGoal: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "My Progress") },
                actions = {
                    TextButton(
                        onClick = onResetGoal, modifier = Modifier.padding(16.dp),
                        content = { Text("Reset") }
                    )
                },
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.problemsWithStatus.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 16.dp)
                    ) {
                        item {
                            ProgressSectionCard(
                                title = "Goal Period",
                                icon = Icons.Default.DateRange
                            ) {
                                DateRow(
                                    startDate = uiState.period.startDate,
                                    endDate = uiState.period.endDate
                                )
                            }
                        }
                        item {
                            ProgressSectionCard(
                                title = "Problems (${uiState.problemsWithStatus.size})",
                                icon = Icons.Default.Assignment
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    uiState.problemsWithStatus.forEach { problemStatus ->
                                        ProblemCard(problemStatus, onNavigateToProblemDetails)
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Set a goal to see your progress",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            AdBannerAdaptive(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun ProblemCard(
    problemStatus: ProblemStatus,
    onNavigateToProblemDetails: (String) -> Unit
) {
    val backgroundColor = when (problemStatus.status) {
        "Completed" -> Color(0xFFE8F5E9)
        "In Progress" -> Color(0xFFFFF8E1)
        else -> Color(0xFFF5F5F5)
    }

    val statusColor = when (problemStatus.status) {
        "Completed" -> Color(0xFF4CAF50)
        "In Progress" -> Color(0xFFFFA000)
        else -> Color(0xFF757575)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .clickable { onNavigateToProblemDetails(problemStatus.titleSlug) }
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = problemStatus.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = problemStatus.status,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = statusColor
                    )
                    Text(
                        text = "${problemStatus.attemptsCount} attempts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (problemStatus.status == "Completed") {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(Color(0xFF4CAF50), shape = CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        modifier = Modifier
                            .size(28.dp)
                            .align(Alignment.Center),
                        imageVector = done,
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(Color.White)
                    )
                }
            } else {
                Image(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(R.drawable.baseline_pending_24),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(statusColor)
                )
            }
        }
    }
}

@Composable
fun ProgressSectionCard(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4CAF50), Color.LightGray)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(gradientBrush)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
fun DateRow(startDate: String, endDate: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateItem(label = "Start Date", date = startDate)
        DateItem(label = "End Date", date = endDate)
    }
}

@Composable
fun DateItem(label: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
@Preview
fun LeetCodeProgressScreenPreview() {
    val problemStatuses = remember {
        listOf(
            ProblemStatus("Two Sum", "two-sum", "Not Started", "Easy", 0),
            ProblemStatus(
                "Longest Substring Without Repeating Characters",
                "two-sum",
                "In Progress",
                "Medium",
                1,
            ),
            ProblemStatus(
                "Median of Two Sorted Arrays",
                "two-sum",
                "Completed",
                "Hard",
                1,
            ),
            ProblemStatus("Add Two Numbers", "two-sum", "Not Started", "Medium", 0),
            ProblemStatus("Valid Parentheses", "two-sum", "In Progress", "Easy", 5),
            ProblemStatus("Merge Two Sorted Lists", "two-sum", "Not Started", "Easy", 0),
            ProblemStatus("Climbing Stairs", "two-sum", "Completed", "Easy", 2)
        )
    }
    ProgressScreenContent(
        ProgressUiState(
            problemStatuses,
            WeeklyGoalPeriod("14 June 2024", "21 June 2024")
        ),
        onPopCurrent = {},
        onNavigateToProblemDetails = {},
        onResetGoal = {}
    )
}
