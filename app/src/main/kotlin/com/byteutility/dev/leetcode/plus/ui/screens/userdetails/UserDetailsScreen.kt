package com.byteutility.dev.leetcode.plus.ui.screens.userdetails

import android.annotation.SuppressLint
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.bytebeats.views.charts.pie.PieChart
import me.bytebeats.views.charts.pie.PieChartData
import me.bytebeats.views.charts.pie.render.SimpleSliceDrawer
import me.bytebeats.views.charts.simpleChartAnimation
import kotlin.time.Duration.Companion.seconds

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun UserProfileScreen(
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
    onTroubleShoot: () -> Unit = {},
) {
    val viewModel: UserDetailsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dailyProblem by viewModel.dailyProblem.collectAsStateWithLifecycle()
    val dailyProblemSolved by viewModel.dailyProblemSolved.collectAsStateWithLifecycle()
    viewModel.startsSync(LocalContext.current)
    UserProfileLayout(
        uiState,
        dailyProblem,
        dailyProblemSolved,
        onSetGoal,
        onGoalStatus,
        onTroubleShoot
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileLayout(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
    onTroubleShoot: () -> Unit = {}
) {
    var clickCount by remember { mutableIntStateOf(0) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    /**
                     * 5 times click in a shorter period will open troubleshoot page
                     */
                    Text(text = "My Profile", modifier = Modifier.clickable {
                        val currentTime = System.currentTimeMillis()
                        if (currentTime - lastClickTime <= 1000) {
                            clickCount++
                            if (clickCount == 5) {
                                onTroubleShoot.invoke()
                                clickCount = 0
                            }
                        } else {
                            clickCount = 1
                        }
                        lastClickTime = currentTime
                        scope.launch {
                            delay(2000)
                            clickCount = 0
                        }
                    })
                },
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
        UserProfileContent(
            uiState,
            dailyProblem,
            dailyProblemSolved,
            Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun UserProfileContent(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UserProfileCard(uiState.userBasicInfo)
                DailyProblemCard(
                    title = dailyProblem.title,
                    if (dailyProblemSolved) "Completed"
                    else "Pending"
                )
                UserStatisticsCard(uiState.userContestInfo)
                UserProblemCategoryStats(userProblemSolvedInfo = uiState.userProblemSolvedInfo)

                if (uiState.userSubmissions.isEmpty()) {
                    Text(
                        text = "You have no recent submissions",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    Text(
                        "Recent AC",
                        modifier = Modifier.padding(top = 8.dp, start = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                    )
                }
            }
        }

        items(uiState.userSubmissions) { submission ->
            SubmissionItem(submission = submission)
        }
    }
}

@Composable
private fun DailyProblemCard(
    title: String = "Two Sum",
    verdict: String = "Completed"
) {
    var animatedContent by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1.seconds)
            animatedContent = !animatedContent
        }
    }
    Crossfade(
        targetState = animatedContent,
        label = ""
    ) { state ->
        when (state) {
            true -> LeetcodeDailyProblemText()
            false -> LeetcodeDailyProblemCard(
                title,
                verdict
            )
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
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceContainer,
            shape = MaterialTheme.shapes.large,
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
            .padding(4.dp)
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
                AsyncImage(
                    model = user.avatar,
                    placeholder = painterResource(R.drawable.profile_placeholder),
                    contentDescription = "User avatar",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )

                Column(modifier = Modifier.fillMaxHeight()) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))
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
fun LeetcodeDailyProblemCard(
    title: String = "Two Sum",
    verdict: String = "Completed"
) {
    val backgroundColor = when (verdict) {
        "Completed" -> Color(0xFFD1FAD7)
        "Pending" -> Color(0xFFFDE2E4)
        else -> Color.LightGray
    }
    val textColor = when (verdict) {
        "Completed" -> Color(0xFF217346)
        "Pending" -> Color(0xFFB00020)
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(4.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = {}
        ) {
            Row(
                modifier = Modifier
                    .background(backgroundColor)
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = verdict,
                        style = MaterialTheme.typography.bodySmall,
                        color = textColor
                    )
                }
            }
        }
    }
}

@Composable
fun LeetcodeDailyProblemText(
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(4.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = {}
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "LeetCode Daily Problem",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge
                    )
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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = {}
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
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
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
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
    ProblemCategoryBox(
        category = "Easy",
        solved = userProblemSolvedInfo.easy,
        total = 830,
        backgroundColor = Color(0xFFE0F7FA)
    )

    ProblemCategoryBox(
        category = "Medium",
        solved = userProblemSolvedInfo.medium,
        total = 1742,
        backgroundColor = Color(0xFFFFF9C4)
    )

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
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = {}
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    text = "Title: ${submission.title}",
                    style = MaterialTheme.typography.titleMedium,
                )
                Text(
                    text = "Date: ${submission.timestamp}",
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Language: ${submission.lang}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
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
        LeetCodeProblem("Two Sum", "", ""),
        false
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewSubmissionItem() {
    SubmissionItem(
        submission = UserSubmission(
            lang = "volumus",
            statusDisplay = "veri",
            timestamp = "eu",
            title = "reformidans",
        )
    )
}