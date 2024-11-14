package com.byteutility.dev.leetcode.plus.ui.screens.userdetails

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import me.bytebeats.views.charts.simpleChartAnimation


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
) {
    val viewModel: UserDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    UserProfileLayout(uiState, onSetGoal, onGoalStatus)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileLayout(
    uiState: UserDetailsUiState,
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "My Profile") },
                actions = {
                    TextButton(onClick = {
                        if (uiState.isWeeklyGoalSet) {
                            onGoalStatus()
                        } else {
                            onSetGoal()
                        }
                    }, modifier = Modifier.padding(end = 8.dp)) {
                        Text(
                            text = if (uiState.isWeeklyGoalSet) "See Goal Status" else "Set Goal",
                            fontSize = 12.sp
                        )
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
        UserProfileContent(uiState, Modifier.padding(paddingValues))
    }
}

@Composable
fun UserProfileContent(
    uiState: UserDetailsUiState,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                UserProfileCard(uiState.userBasicInfo)
                UserStatisticsCard(uiState.userContestInfo)
                UserProblemCategoryStats(userProblemSolvedInfo = uiState.userProblemSolvedInfo)
                Text("Recent AC", fontSize = 16.sp)
            }
        }

        items(uiState.userSubmissions) { submission ->
            SubmissionItem(submission = submission)
        }
    }
}

@Composable
fun UserPieChart(userProblemSolvedInfo: UserProblemSolvedInfo) {
    PieChart(
        pieChartData = PieChartData(
            slices = listOf(
                PieChartData.Slice(userProblemSolvedInfo.getEasyPercentage(), Color(0xFFE0F7FA)),
                PieChartData.Slice(userProblemSolvedInfo.getMediumPercentage(), Color(0xFFFFF9C4)),
                PieChartData.Slice(userProblemSolvedInfo.getHardPercentage(), Color(0xFFFFCDD2))
            )
        ),
        modifier = Modifier.size(200.dp),
        animation = simpleChartAnimation(),
        sliceDrawer = SimpleSliceDrawer(50F),
    )
}

@Composable
fun UserProblemCategoryStats(
    modifier: Modifier = Modifier,
    userProblemSolvedInfo: UserProblemSolvedInfo,
) {
    Box(
        modifier = modifier.fillMaxWidth()
    ) {
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                UserPieChart(userProblemSolvedInfo)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProblemCategoriesSolved(userProblemSolvedInfo)
                }
            }
        }
    }
}

@Composable
fun UserProfileCard(user: UserBasicInfo) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Avatar image
                AsyncImage(
                    model = user.avatar,
                    placeholder = painterResource(R.drawable.profile_placeholder),
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )

                // User information
                Column(modifier = Modifier.fillMaxHeight()) {
                    // User name
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Country with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Country",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Country: ${user.country}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Ranking with icon
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ranking",
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ranking: ${user.ranking}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun UserStatisticsCard(user: UserContestInfo) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .background(gradientBrush)
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Contest Rating: ${String.format("%.3f", user.rating)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Global Ranking",
                        tint = Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Global Ranking: ${user.globalRanking}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Attend Days",
                        tint = Color.Magenta,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Attend: ${user.attend} days",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            }
        }
    }
}


@Composable
fun ProblemCategoriesSolved(
    userProblemSolvedInfo: UserProblemSolvedInfo,
) {
    // Easy Category
    ProblemCategoryBox(
        category = "Easy",
        solved = userProblemSolvedInfo.easy,
        total = 830,
        backgroundColor = Color(0xFFE0F7FA)
    )

    // Medium Category
    ProblemCategoryBox(
        category = "Medium",
        solved = userProblemSolvedInfo.medium,
        total = 1742,
        backgroundColor = Color(0xFFFFF9C4)
    )

    // Hard Category
    ProblemCategoryBox(
        category = "Hard",
        solved = userProblemSolvedInfo.hard,
        total = 756,
        backgroundColor = Color(0xFFFFCDD2)
    )
}

@Composable
fun ProblemCategoryBox(category: String, solved: Int, total: Int, backgroundColor: Color) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
        ),
        modifier = Modifier
            .width(100.dp)
            .height(70.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = category)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "$solved/$total")
        }
    }
}

@Composable
fun SubmissionItem(submission: UserSubmission) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "Title: ${submission.title}")
            Text(text = "Date: ${submission.timestamp}")
            Text(text = "Language: ${submission.lang}")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewUserDetails() {
    val submissions = listOf(
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
        UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans"
        ),
    )
    UserProfileLayout(
        uiState = UserDetailsUiState(
            userBasicInfo = UserBasicInfo(
                name = "Mindy Shannon",
                userName = "Annette Jones",
                avatar = "venenatis",
                ranking = 8869,
                country = "Gambia, The"
            ), userContestInfo = UserContestInfo(
                rating = 14.15,
                globalRanking = 3679,
                attend = 7232
            ), userProblemSolvedInfo = UserProblemSolvedInfo(
                easy = 4592,
                medium = 5761,
                hard = 6990
            ), userSubmissions = submissions
        ),
    )
}

@Composable
fun DateRow(startDate: String, endDate: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DateItem(label = "Start Date", date = startDate)
        Spacer(modifier = Modifier.width(16.dp))
        DateItem(label = "End Date", date = endDate)
    }
}

@Composable
fun DateItem(label: String, date: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        )
        Text(
            text = date,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDateRow() {
    DateRow(
        startDate = "2024-11-15",
        endDate = "2024-11-20"
    )
}
