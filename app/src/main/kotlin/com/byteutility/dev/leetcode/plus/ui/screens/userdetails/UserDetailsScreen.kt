package com.byteutility.dev.leetcode.plus.ui.screens.userdetails

import android.annotation.SuppressLint
import android.net.Uri
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
import androidx.compose.ui.draw.shadow
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
import kotlinx.coroutines.isActive
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
    onNavigateToWebView: (String) -> Unit = {}
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
        onTroubleShoot,
        onNavigateToWebView
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
    onTroubleShoot: () -> Unit = {},
    onNavigateToWebView: (String) -> Unit = {}
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
            onNavigateToWebView,
            Modifier.padding(paddingValues),
        )
    }
}

@Composable
fun UserProfileContent(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onNavigateToWebView: (String) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
            .padding(start = 16.dp, end = 16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                UserProfileCard(uiState.userBasicInfo)
                DailyProblemCard(
                    title = dailyProblem.title,
                    verdict = if (dailyProblemSolved) "Completed" else "Pending",
                    titleSlug = dailyProblem.titleSlug,
                    onNavigateToWebView = onNavigateToWebView
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
                        fontSize = 16.sp,
                        modifier = Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
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
            .padding(4.dp)
            .fillMaxWidth()
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

                Column(
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Country",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Country: ${user.country}",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Ranking",
                            tint = Color.Yellow,
                            modifier = Modifier.size(20.dp)
                        )
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
            .padding(4.dp)
            .fillMaxWidth()
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
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color.Yellow,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Contest Rating: ${String.format("%.3f", user.rating)}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Leaderboard,
                        contentDescription = "Global Ranking",
                        tint = Color.Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Global Ranking: ${user.globalRanking}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
                Divider(color = Color.White.copy(alpha = 0.5f), thickness = 1.dp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Attend Days",
                        tint = Color.Magenta,
                        modifier = Modifier.size(20.dp)
                    )
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
            .size(width = 100.dp, height = 70.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize(),
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
            .padding(horizontal = 8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Title: ${submission.title}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color(0xFF323232)
            )
            Text(
                text = "Date: ${submission.timestamp}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF757575)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Language: ${submission.lang}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF323232)
                )
            }
        }
    }
}

@Composable
fun DailyProblemCard(
    title: String,
    verdict: String,
    titleSlug: String,
    onNavigateToWebView: (String) -> Unit
) {
    var animatedContent by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (isActive) {
            animatedContent = true
            delay(1.seconds.inWholeMilliseconds)
            animatedContent = false
            delay(5.seconds.inWholeMilliseconds)
        }
    }

    Crossfade(
        targetState = animatedContent,
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
    ) { state ->
        when (state) {
            true -> ProblemTextPlaceholder()
            false -> ProblemDetailsCard(title = title, titleSlug = titleSlug, verdict = verdict) {
                onNavigateToWebView(it)
            }
        }
    }
}

@Composable
fun ProblemTextPlaceholder() {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "LeetCode Daily Problem",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Solve today for a streak!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun ProblemDetailsCard(
    title: String,
    titleSlug: String,
    verdict: String,
    onNavigateToWebView: (String) -> Unit
) {
    val backgroundColor = when (verdict) {
        "Completed" -> MaterialTheme.colorScheme.secondaryContainer
        "Pending" -> MaterialTheme.colorScheme.errorContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val textColor = when (verdict) {
        "Completed" -> MaterialTheme.colorScheme.onSecondaryContainer
        "Pending" -> MaterialTheme.colorScheme.onErrorContainer
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .clickable {
                val encodedUrl =
                    Uri.encode("https://leetcode.com/problems/${titleSlug}/description")
                onNavigateToWebView.invoke(encodedUrl)
            }
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = verdict,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textColor
                )
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