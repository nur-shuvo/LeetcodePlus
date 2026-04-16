package com.byteutility.dev.leetcode.plus.ui.screens.home

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.Contest
import com.byteutility.dev.leetcode.plus.ui.common.ProgressIndicator
import com.byteutility.dev.leetcode.plus.ui.model.YouTubeVideo
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.DifficultyStatistics
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.UserDetailsUiState
import com.byteutility.dev.leetcode.plus.ui.screens.home.model.VideosByPlayListState
import com.byteutility.dev.leetcode.plus.ui.theme.EasyText
import com.byteutility.dev.leetcode.plus.ui.theme.HardText
import com.byteutility.dev.leetcode.plus.ui.theme.MediumText
import com.byteutility.dev.leetcode.plus.utils.formatContestDate
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.OffsetDateTime
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeScreen(
    onSetGoal: () -> Unit = {},
    onGoalStatus: () -> Unit = {},
    onTroubleShoot: () -> Unit = {},
    onNavigateToProblemDetails: (String) -> Unit = {},
    onNavigateToVideoSolutions: () -> Unit = {},
    onNavigateToAllProblems: () -> Unit = {},
    onNavigateToContestDetail: (Contest) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val viewModel: HomeScreenViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val dailyProblem by viewModel.dailyProblem.collectAsStateWithLifecycle()
    val dailyProblemSolved by viewModel.dailyProblemSolved.collectAsStateWithLifecycle()
    LifecycleResumeEffect(Unit) {
        viewModel.refreshUiState()
        onPauseOrDispose { }
    }
    HomeLayout(
        uiState = uiState,
        dailyProblem = dailyProblem,
        dailyProblemSolved = dailyProblemSolved,
        onSetGoal = onSetGoal,
        onGoalStatus = onGoalStatus,
        onTroubleShoot = onTroubleShoot,
        onNavigateToProblemDetails = onNavigateToProblemDetails,
        onLoadMoreSubmission = {
            viewModel.loadNextAcSubmissions()
        },
        onLoadMoreVideos = {
            viewModel.loadNextVideos()
        },
        onSearchClick = onNavigateToVideoSolutions,
        onLogout = {
            viewModel.logout()
            onLogout.invoke()
        },
        onSetInAppReminder = { contest ->
            viewModel.setInAppReminder(contest)
        },
        checkInAppContestReminderStatus = {
            viewModel.checkInAppContestReminderStatus(it)
        },
        onNavigateToAllProblems = onNavigateToAllProblems,
        onNavigateToContestDetail = onNavigateToContestDetail
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeLayout(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onSetGoal: () -> Unit,
    onGoalStatus: () -> Unit,
    onTroubleShoot: () -> Unit,
    onNavigateToProblemDetails: (String) -> Unit,
    onLoadMoreSubmission: () -> Unit,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit,
    onLogout: () -> Unit,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToAllProblems: () -> Unit = {},
    onNavigateToContestDetail: (Contest) -> Unit = {},
) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        LogoutConfirmationDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

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
                    Text(
                        text = "Home",
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
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
                    MainTopActions(
                        isWeeklyGoalSet = uiState.isWeeklyGoalSet,
                        avatarUrl = uiState.userBasicInfo.avatar,
                        onSetGoal = onSetGoal,
                        onGoalStatus = onGoalStatus,
                        onLogoutClick = {
                            showLogoutDialog = true
                        },
                        modifier = Modifier.testTag("main_top_actions")
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFABDEF5).copy(
                        alpha = 0.1f
                    )
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                UserProfileContent(
                    uiState = uiState,
                    dailyProblem = dailyProblem,
                    dailyProblemSolved = dailyProblemSolved,
                    onNavigateToProblemDetails = onNavigateToProblemDetails,
                    onLoadMoreSubmission = onLoadMoreSubmission,
                    onLoadMoreVideos = onLoadMoreVideos,
                    onSearchClick = onSearchClick,
                    onSetInAppReminder = onSetInAppReminder,
                    checkInAppContestReminderStatus = checkInAppContestReminderStatus,
                    onNavigateToContestDetail = onNavigateToContestDetail
                )
                val infiniteTransition = rememberInfiniteTransition(label = "fab_animation")
                val scale by infiniteTransition.animateFloat(
                    initialValue = 1f,
                    targetValue = 1.3f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000),
                        repeatMode = RepeatMode.Reverse
                    ), label = "fab_scale"
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .scale(scale),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .background(Color(0xFF2193b0).copy(alpha = 0.3f), CircleShape)
                                .blur(20.dp)
                        )

                        FloatingActionButton(
                            onClick = onNavigateToAllProblems,
                            containerColor = Color(0xFF2193b0),
                            contentColor = Color.White,
                            shape = CircleShape,
                            modifier = Modifier
                                .size(64.dp)
                                .shadow(12.dp, CircleShape, ambientColor = Color(0xFF2193b0))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_problems),
                                contentDescription = null,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        shadowElevation = 10.dp,
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onNavigateToAllProblems() }
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF6dd5ed), Color(0xFF2193b0))
                                    )
                                )
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = "ALL PROBLEMS",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = .5.sp
                                ),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainTopActions(
    isWeeklyGoalSet: Boolean,
    avatarUrl: String,
    onSetGoal: () -> Unit,
    onGoalStatus: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Button(
            onClick = { if (isWeeklyGoalSet) onGoalStatus() else onSetGoal() },
            modifier = Modifier
                .height(42.dp)
                .padding(end = 12.dp)
                .testTag("goal_action_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isWeeklyGoalSet)
                    Color(0xFFE3F2FD) else Color(0xFF4CAF50),
                contentColor = if (isWeeklyGoalSet)
                    Color(0xFF1976D2) else Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            Icon(
                imageVector = if (isWeeklyGoalSet)
                    Icons.Default.CheckCircle else Icons.Default.AddCircleOutline,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = if (isWeeklyGoalSet) "Goal Status" else "Set Weekly Goal",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.ExtraBold
                )
            )
        }

        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White)
                .clickable { onLogoutClick() }
                .border(1.dp, Color.LightGray.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "Profile",
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.profile_placeholder)
            )
        }
    }
}

@Composable
fun UserProfileContent(
    uiState: UserDetailsUiState,
    dailyProblem: LeetCodeProblem,
    dailyProblemSolved: Boolean,
    onNavigateToProblemDetails: (String) -> Unit,
    onLoadMoreSubmission: () -> Unit,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToContestDetail: (Contest) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier
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

                // Data sync info message
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 2.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.Event,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Data updated every ${uiState.syncInterval} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                DailyProblemCard(
                    title = dailyProblem.title,
                    verdict = if (dailyProblemSolved) "Completed" else "Pending",
                    titleSlug = dailyProblem.titleSlug,
                    difficulty = dailyProblem.difficulty,
                    onNavigateToProblemDetails = onNavigateToProblemDetails
                )
                UserStatisticsCard(uiState.userContestInfo)
                YouTubeVideoRowContent(
                    uiState.videosByPlayListState,
                    onLoadMoreVideos,
                    onSearchClick
                )
                Text(
                    text = "Upcoming contests",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 16.dp)
                )
                AutoScrollingContestList(
                    contests = uiState.leetcodeUpcomingContestsState.contests,
                    onSetInAppReminder = onSetInAppReminder,
                    checkInAppContestReminderStatus = checkInAppContestReminderStatus,
                    onNavigateToContestDetail = onNavigateToContestDetail
                )
                UserProblemCategoryStats(userProblemSolvedInfo = uiState.userProblemSolvedInfo, diffStat = uiState.difficultyStat)

                if (uiState.userSubmissionState.submissions.isEmpty()) {
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

        items(uiState.userSubmissionState.submissions.size) { index ->
            val item = uiState.userSubmissionState.submissions[index]
            if (index >= uiState.userSubmissionState.submissions.size - 1 && !uiState.userSubmissionState.endReached && !uiState.userSubmissionState.isLoading) {
                onLoadMoreSubmission()
            }
            SubmissionItem(
                submission = item,
                onClick = { onNavigateToProblemDetails(item.titleSlug) }
            )
        }

        item {
            if (uiState.userSubmissionState.isLoading) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    ProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun UserProblemCategoryStats(
    modifier: Modifier = Modifier,
    userProblemSolvedInfo: UserProblemSolvedInfo,
    diffStat: DifficultyStatistics
) {
    Box(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .fillMaxWidth()
    ) {
        CategoryStatsCard(userProblemSolvedInfo, diffStat = diffStat )
    }
}

@Composable
fun UserProfileCard(user: UserBasicInfo) {
    val premiumGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF1e3c72), Color(0xFF2a5298))
    )

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
    ) {
        Box(
            modifier = Modifier
                .background(premiumGradient)
                .padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 40.dp, y = (-40).dp)
                    .background(Color.White.copy(alpha = 0.05f), CircleShape)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(82.dp)
                    ) {}
                    AsyncImage(
                        model = user.avatar,
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        contentDescription = "User avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(20.dp))

                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = user.name,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Country Tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color(0xFF4FC3F7),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = user.country.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Global Rank ",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            text = "#${user.ranking}",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LogoutButton(
    onLogout: () -> Unit,
    avatar: String
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(Color(0xFFE91E63), Color(0xFFFFC107))
    )
    Box(
        modifier = Modifier
            .padding(end = 16.dp)
            .size(40.dp)
            .clip(CircleShape)
            .background(gradientBrush)
            .clickable { onLogout() },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = avatar,
            placeholder = painterResource(R.drawable.profile_placeholder),
            contentDescription = "User avatar",
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
        )
    }
}

@Composable
fun UserStatisticsCard(user: UserContestInfo) {
    val mainGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F2027), Color(0xFF203A43), Color(0xFF2C5364))
    )

    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp)
    ) {
        Box(
            modifier = Modifier
                .background(mainGradient)
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Contest Stats",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Surface(
                        color = Color(0xFF4CAF50).copy(alpha = 0.2f),
                        shape = CircleShape,
                        border = BorderStroke(1.dp, Color(0xFF4CAF50))
                    ) {
                        Text(
                            text = "ACTIVE",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format(Locale.US,"%.0f", user.rating),
                        style = MaterialTheme.typography.displayMedium.copy(
                            fontWeight = FontWeight.Black
                        ),
                        color = Color.White
                    )
                    Text(
                        text = ".${String.format(Locale.US,"%03d", ((user.rating % 1) * 1000).toInt())}",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(bottom = 8.dp, start = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        label = "Global Rank",
                        value = "#${user.globalRanking}",
                        icon = Icons.Default.Leaderboard,
                        iconColor = Color(0xFF00D2FF),
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Days Attended",
                        value = "${user.attend}",
                        icon = Icons.Default.CalendarToday,
                        iconColor = Color(0xFF9D50BB),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun StatTile(
    label: String,
    value: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color.White.copy(alpha = 0.08f), // Glass effect
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}

@Composable
fun SubmissionItem(
    submission: UserSubmission,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
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
    difficulty: String,
    onNavigateToProblemDetails: (String) -> Unit
) {
    var isShowingLogo by remember { mutableStateOf(true) }
    var remainingTime by remember { mutableStateOf(calculateRemainingTime()) }

    // Logic for toggling views
    LaunchedEffect(Unit) {
        while (isActive) {
            delay(5000L) // Show logo/intro for 5s
            isShowingLogo = false
            delay(15000L) // Show details for 15s
            isShowingLogo = true
        }
    }

    // Logic for the timer
    LaunchedEffect(Unit) {
        while (isActive) {
            remainingTime = calculateRemainingTime()
            delay(1000L)
        }
    }

    // Main Container with a fixed glassmorphic style
    Card(
        modifier = Modifier
            .padding(12.dp)
            .fillMaxWidth()
            .height(110.dp), // Fixed height prevents "jumping" during Crossfade
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Glow (Subtle accent based on difficulty)
            val accentColor = getDifficultyColor(difficulty)
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 20.dp, y = (-20).dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape)
                    .blur(30.dp)
            )

            Crossfade(
                targetState = isShowingLogo,
                animationSpec = tween(800),
                modifier = Modifier.padding(16.dp)
            ) { showLogo ->
                if (showLogo) {
                    ProblemHeroIntro(remainingTime, accentColor)
                } else {
                    ProblemActiveDetails(
                        title = title,
                        verdict = verdict,
                        difficulty = difficulty,
                        remainingTime = remainingTime,
                        accentColor = accentColor,
                        onClick = { onNavigateToProblemDetails(titleSlug) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProblemHeroIntro(remainingTime: String, accentColor: Color) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon / Logo Branding
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = accentColor.copy(alpha = 0.1f),
            modifier = Modifier.size(56.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.padding(12.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "DAILY CHALLENGE",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                ),
                color = accentColor
            )
            Text(
                text = "Solve to keep your streak!",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        TimerBadge(remainingTime)
    }
}

@Composable
fun ProblemActiveDetails(
    title: String,
    verdict: String,
    difficulty: String,
    remainingTime: String,
    accentColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null // Clean click
            ) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            // Difficulty Tag
            Surface(
                shape = CircleShape,
                color = accentColor.copy(alpha = 0.1f),
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Text(
                    text = difficulty,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = accentColor
                )
            }

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            // Verdict status
            val verdictColor = when(verdict) {
                "Completed" -> Color(0xFF4CAF50)
                "Pending" -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            }
            Text(
                text = verdict,
                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color = verdictColor
            )
        }

        TimerBadge(remainingTime)
    }
}

@Composable
fun TimerBadge(time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "LEFT",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }
}

val DifficultyColorKey = SemanticsPropertyKey<Color>("DifficultyColor")
var SemanticsPropertyReceiver.difficultyColor by DifficultyColorKey

fun getDifficultyColor(difficulty: String): Color = when (difficulty) {
    "Easy" -> Color(0xFF4CAF50)
    "Medium" -> Color(0xFFFFC107)
    else -> Color(0xFFF44336)
}

@Composable
fun YouTubeVideoRowContent(
    state: VideosByPlayListState,
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit
) {
    val videos = state.videos.map {
        YouTubeVideo(
            videoId = it.id,
            thumbnailUrl = it.snippet.thumbnails.high.url,
            title = it.snippet.title
        )
    }
    YouTubeVideoRow(state, videos, onLoadMoreVideos, onSearchClick)
}

@Composable
fun YouTubeVideoRow(
    state: VideosByPlayListState,
    videos: List<YouTubeVideo> = mutableListOf(),
    onLoadMoreVideos: () -> Unit,
    onSearchClick: () -> Unit
) {
    val context = LocalContext.current
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            SearchVideosButton(onClick = onSearchClick)
        }
        itemsIndexed(videos) { index, video ->
            if (index >= state.videos.size - 1 && !state.endReached && !state.isLoading) {
                onLoadMoreVideos()
            }
            Box(
                modifier = Modifier
                    .size(135.dp, 100.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray)
                    .clickable {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            "https://www.youtube.com/watch?v=${video.videoId}".toUri()
                        )
                        context.startActivity(intent)
                    }
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = "YouTube Thumbnail",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

@Composable
fun SearchVideosButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(135.dp, 100.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(
                brush = Brush.horizontalGradient(listOf(Color.Blue, Color.Cyan))
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
            Text("Search Videos", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoScrollingContestList(
    contests: List<Contest>,
    modifier: Modifier = Modifier,
    scrollIntervalMillis: Long = 3000L,
    onSetInAppReminder: (Contest) -> Unit,
    checkInAppContestReminderStatus: suspend (Contest) -> Boolean,
    onNavigateToContestDetail: (Contest) -> Unit = {}
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    var currentIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedContest by remember { mutableStateOf<Contest?>(null) }
    var isInAppContestReminderSet by remember { mutableStateOf(false) }

    LaunchedEffect(contests) {
        if (contests.isEmpty()) return@LaunchedEffect
        while (true) {
            delay(scrollIntervalMillis)
            if (contests.isNotEmpty()) {
                currentIndex = (currentIndex + 1) % contests.size
                scope.launch {
                    listState.animateScrollToItem(currentIndex)
                }
            }
        }
    }

    LazyRow(
        state = listState,
        modifier = modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(contests) { contest ->
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .width(IntrinsicSize.Max)
                    .clickable { onNavigateToContestDetail(contest) }
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = contest.event,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = formatContestDate(contest.start),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = "Link to contest",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(contest.href))
                                    context.startActivity(intent)
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = "Add a reminder",
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    selectedContest = contest
                                    scope.launch {
                                        isInAppContestReminderSet =
                                            checkInAppContestReminderStatus(contest)
                                        showBottomSheet = true
                                    }
                                },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        selectedContest?.let { contest ->
            AddReminderBottomSheet(
                onDismiss = { showBottomSheet = false },
                onAddToCalendar = {
                    val beginTime =
                        OffsetDateTime.parse(contest.start + "Z").toInstant()
                            .toEpochMilli()
                    val endTime =
                        beginTime + Duration.ofSeconds(contest.duration.toLong())
                            .toMillis()

                    val intent = Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(
                            CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                            beginTime
                        )
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime)
                        .putExtra(CalendarContract.Events.TITLE, contest.event)
                        .putExtra(
                            CalendarContract.Events.DESCRIPTION,
                            "LeetCode Contest: ${contest.event}"
                        )
                        .putExtra(
                            CalendarContract.Events.EVENT_LOCATION,
                            contest.href
                        )
                        .putExtra(
                            CalendarContract.Events.AVAILABILITY,
                            CalendarContract.Events.AVAILABILITY_BUSY
                        )

                    context.startActivity(intent)
                    showBottomSheet = false
                },
                onSetInAppReminder = {
                    onSetInAppReminder(contest)
                    showBottomSheet = false
                },
                isInAppContestReminderSet = isInAppContestReminderSet
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderBottomSheet(
    onDismiss: () -> Unit,
    onAddToCalendar: () -> Unit,
    onSetInAppReminder: () -> Unit,
    isInAppContestReminderSet: Boolean
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add a reminder for the contest", style = MaterialTheme.typography.titleLarge)
            Button(onClick = onAddToCalendar) {
                Text("Add to Google Calendar")
            }
            Button(onClick = onSetInAppReminder, enabled = !isInAppContestReminderSet) {
                Text(if (isInAppContestReminderSet) "In-App reminder already set" else "Set in-app reminder")
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Confirm Logout") },
        text = { Text(text = "Are you sure you want to logout?") },
        confirmButton = {
            Button(
                onClick = onConfirm
            ) {
                Text("Logout")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun MultiRadialProgressChart(
    userProblemSolvedInfo: UserProblemSolvedInfo?,
    diffStat: DifficultyStatistics,
    modifier: Modifier = Modifier
) {
    if (userProblemSolvedInfo == null) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("No Data Available", style = MaterialTheme.typography.bodyMedium)
        }
        return
    }

    val rings = listOf(
        RadialData("Easy", userProblemSolvedInfo.easy, 937, EasyText),
        RadialData("Medium", userProblemSolvedInfo.medium, 2037, MediumText),
        RadialData("Hard", userProblemSolvedInfo.hard, 921, HardText)
    )

    Canvas(modifier = modifier.padding(16.dp)) {
        val strokeWidth = 12.dp.toPx()
        val spacing = 10.dp.toPx()
        rings.forEachIndexed { index, data ->
            val sweepAngle = if (data.total > 0) {
                (data.solved.toFloat() / data.total.toFloat()) * 360f
            } else 0f

            val inset = index * (strokeWidth + spacing)
            val ringSize = Size(
                width = size.width - inset * 2,
                height = size.height - inset * 2
            )

            drawArc(
                color = data.color.copy(alpha = 0.2f),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = ringSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            drawArc(
                color = data.color,
                startAngle = -90f,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = ringSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
    }
}

@Composable
fun RadialLegend(userProblemSolvedInfo: UserProblemSolvedInfo,diffStat: DifficultyStatistics) {
    val items = listOf(
        Triple("Easy", userProblemSolvedInfo.easy to 937, EasyText),
        Triple("Medium", userProblemSolvedInfo.medium to 2037, MediumText),
        Triple("Hard", userProblemSolvedInfo.hard to 921, HardText)
    )

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items.forEach { (label, stats, color) ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(width = 40.dp, height = 20.dp)
                        .background(color, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = label, fontWeight = FontWeight.Bold)
                    val percentage = if (stats.second > 0) {
                        (stats.first.toFloat() / stats.second.toFloat()) * 100
                    } else 0f
                    val formattedPercentage = "%.1f".format(percentage)
                    Text(
                        text = "$formattedPercentage%",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "${stats.first} from ${stats.second}",
                        color = Color.Gray,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryStatsCard(userProblemSolvedInfo: UserProblemSolvedInfo?,diffStat: DifficultyStatistics) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Box(modifier = Modifier.weight(1f)) {
                if (userProblemSolvedInfo != null) {
                    RadialLegend(userProblemSolvedInfo, diffStat = diffStat )
                } else {
                    Text("No Statistics")
                }
            }

            Spacer(modifier = Modifier.width(16.dp))
            MultiRadialProgressChart(
                userProblemSolvedInfo = userProblemSolvedInfo,
                diffStat = diffStat ,
                modifier = Modifier
                    .size(140.dp)
                    .aspectRatio(1f)
            )
        }
    }
}

data class RadialData(val label: String, val solved: Int, val total: Int, val color: Color)

private fun calculateRemainingTime(): String {
    val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
    val midnight = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_MONTH, 1)
    }

    val diffMillis = midnight.timeInMillis - now.timeInMillis
    val hours = (diffMillis / (1000 * 60 * 60)) % 24
    val minutes = (diffMillis / (1000 * 60)) % 60
    val seconds = (diffMillis / 1000) % 60

    return String.format(Locale.US,"%02d:%02d:%02d", hours, minutes, seconds)
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
    HomeLayout(
        uiState = UserDetailsUiState(
            userBasicInfo = UserBasicInfo(
                name = "Mindy Shannon",
                userName = "Annette Jones",
                avatar = "venenatis",
                ranking = 8869,
                country = "Gambia, The"
            ),
            userContestInfo = UserContestInfo(
                rating = 14.15,
                globalRanking = 3679,
                attend = 7232
            ),
            userProblemSolvedInfo = UserProblemSolvedInfo(
                easy = 4592,
                medium = 5761,
                hard = 6990
            ),
        ),
        LeetCodeProblem("Two Sum", "", ""),
        false,
        onSetGoal = {},
        onGoalStatus = {},
        onTroubleShoot = {},
        onNavigateToProblemDetails = {},
        onLoadMoreSubmission = {},
        onLoadMoreVideos = {},
        onSearchClick = {},
        onLogout = {},
        onSetInAppReminder = {},
        checkInAppContestReminderStatus = { false },
        onNavigateToContestDetail = {}
    )
}


@Preview
@Composable
fun PreviewContestScreen() {
    val sampleContests = listOf(
        Contest(
            duration = 5400,
            end = "2025-09-27T16:00:00",
            event = "Biweekly Contest 166",
            host = "leetcode.com",
            href = "https://leetcode.com/contest/biweekly-contest-166",
            id = 61794058,
            nProblems = null,
            nStatistics = null,
            parsedAt = null,
            problems = null,
            resource = "leetcode.com",
            resourceId = 102,
            start = "2025-09-27T14:30:00"
        ),
        Contest(
            duration = 5400,
            end = "2025-09-21T04:00:00",
            event = "Weekly Contest 468",
            host = "leetcode.com",
            href = "https://leetcode.com/contest/weekly-contest-468",
            id = 61794059,
            nProblems = null,
            nStatistics = null,
            parsedAt = null,
            problems = null,
            resource = "leetcode.com",
            resourceId = 102,
            start = "2025-09-21T02:30:00"
        )
    )

    AutoScrollingContestList(
        contests = sampleContests,
        onSetInAppReminder = {},
        checkInAppContestReminderStatus = { false },
        onNavigateToContestDetail = {}
    )
}
