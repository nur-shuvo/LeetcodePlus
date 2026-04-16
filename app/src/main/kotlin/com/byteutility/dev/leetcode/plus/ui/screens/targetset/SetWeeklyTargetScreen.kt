package com.byteutility.dev.leetcode.plus.ui.screens.targetset

import android.app.Activity
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Description
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.byteutility.dev.leetcode.plus.BuildConfig
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.ui.dialogs.WeeklyGoalSetDialog
import com.byteutility.dev.leetcode.plus.ui.screens.allproblems.DifficultyChip
import com.byteutility.dev.leetcode.plus.ui.screens.allproblems.TopicTagChip
import com.byteutility.dev.leetcode.plus.ui.theme.CardBorderColor
import com.byteutility.dev.leetcode.plus.ui.theme.EasyText
import com.byteutility.dev.leetcode.plus.ui.theme.HardText
import com.byteutility.dev.leetcode.plus.ui.theme.LabelColor
import com.byteutility.dev.leetcode.plus.ui.theme.MediumText
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemsGreen
import com.byteutility.dev.leetcode.plus.ui.theme.SearchBarBackground
import com.byteutility.dev.leetcode.plus.ui.theme.SearchBarPlaceholder
import com.byteutility.dev.leetcode.plus.ui.theme.TitleColor
import com.byteutility.dev.leetcode.plus.ui.theme.TopBarBackground
import com.byteutility.dev.leetcode.plus.utils.toLeetCodeProblem
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetWeeklyTargetScreen(
    onPopCurrent: () -> Unit = {},
    onNavigateToProblemDetails: (String) -> Unit = {},
    viewModel: SetWeeklyTargetViewModel = hiltViewModel()
) {
    val problems = viewModel.problems.collectAsLazyPagingItems()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.popCurrentDestination.collect {
            onPopCurrent()
        }
    }

    LaunchedEffect(Unit) {
        InterstitialAd.load(
            context,
            BuildConfig.ADMOB_INTERSTITIAL_AD_UNIT_ID,
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    if (showDialog.value) {
        WeeklyGoalSetDialog(
            confirmed = { period ->
                viewModel.handleWeeklyGoalSet(state.selectedProblem, period)
                val activity = context as? Activity
                val add = interstitialAd
                if (add != null && activity != null) {
                    add.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            interstitialAd = null
                        }

                        override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                            interstitialAd = null
                        }
                    }
                    add.show(activity)
                }
            },
            onDismiss = {
                showDialog.value = it
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
    ) {
        ProblemsTopAppBar {
            onPopCurrent()
        }
        SearchBar(
            query = state.searchQuery,
            onQueryChange = viewModel::updateSearchQuery,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ProblemsGreen)
                }
            }
            state.isError -> {
                ErrorCard(
                    onRetry = viewModel::retry,
                    modifier = Modifier.fillMaxSize()
                )
            }
            else -> {
                ProblemList(
                    problems = problems,
                    selectedProblems = state.selectedProblem,
                    onProblemClick = { problem ->
                        problem.titleSlug?.let { onNavigateToProblemDetails(it) }
                    },
                    onProblemSelected = { problem ->
                        viewModel.onProblemSelected(problem.toLeetCodeProblem())
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Button(
            onClick = {
                showDialog.value = true
            },
            enabled = state.selectedProblem.size == 7,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = ProblemsGreen,
                contentColor = Color.White,
                disabledContainerColor = Color.Gray,
                disabledContentColor = Color.Black
            )
        ) {
            Text(
               text =  "Confirm",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontSize = 16.sp
                )
            )
        }
    }
}


@Composable
private fun ProblemList(
    problems: LazyPagingItems<ProblemEntity>,
    selectedProblems: List<LeetCodeProblem>,
    onProblemClick: (ProblemEntity) -> Unit,
    onProblemSelected: (ProblemEntity) -> Unit,
    modifier: Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = problems.itemCount,
            key = { index -> problems[index]?.problemId ?: index }
        ) { index ->
            val problem = problems[index]
            if (problem != null) {
                val isSelected = selectedProblems.any { it.titleSlug == problem.titleSlug }
                ProblemCard(
                    problem = problem,
                    isSelected = isSelected,
                    onClick = { onProblemClick(problem) },
                    onProblemSelected = { onProblemSelected(problem) }
                )
            }
        }

        if (problems.loadState.append is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = ProblemsGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProblemCard(
    problem: ProblemEntity,
    isSelected: Boolean,
    onClick: () -> Unit,
    onProblemSelected: () -> Unit
) {
    // Dynamic theme based on difficulty for selection state
    val accentColor = when (problem.difficulty.lowercase()) {
        "easy" -> EasyText
        "medium" -> MediumText
        "hard" -> HardText
        else -> LabelColor
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.White else Color.White
        ),
        // Glow effect when selected
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) accentColor.copy(alpha = 0.5f) else CardBorderColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 6.dp else 2.dp
        )
    ) {
        Box(modifier = Modifier.height(IntrinsicSize.Min)) {
            // Left Side Selection Indicator
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .width(6.dp)
                        .fillMaxHeight()
                        .padding(vertical = 12.dp)
                        .clip(RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp))
                        .background(accentColor)
                )
            }

            Column(
                modifier = Modifier.padding(start = if (isSelected) 22.dp else 16.dp,
                    top = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header: ID, Title, and Checkbox
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "${problem.problemId}.",
                        style = TextStyle(
                            fontWeight = Bold,
                            fontSize = 14.sp,
                            color = LabelColor.copy(alpha = 0.5f)
                        )
                    )
                    Text(
                        text = problem.title,
                        modifier = Modifier.weight(1f).basicMarquee(),
                        style = TextStyle(
                            fontWeight = Bold,
                            fontSize = 16.sp,
                            color = TitleColor
                        ),
                        maxLines = 1
                    )

                    if (!problem.isFree) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFA116)
                        )
                    }

                    CircularCheckbox(
                        checked = isSelected,
                        onCheckedChange = { onProblemSelected() },
                        color = accentColor
                    )
                }

                // Tags
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DifficultyChip(difficulty = problem.difficulty)
                    val tags = problem.topicTags.orEmpty()
                    val visibleTags = tags.take(2)
                    val overflowCount = tags.size - 2
                    visibleTags.forEach { tag ->
                        TopicTagChip(tag = tag)
                    }
                    if (overflowCount > 0) OverflowTagChip(count = overflowCount)
                }

                HorizontalDivider(
                    Modifier,
                    thickness = 0.5.dp,
                    color = CardBorderColor.copy(alpha = 0.4f)
                )

                // Footer: Acceptance & Solution types
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "ACCEPTANCE ",
                            letterSpacing = 0.5.sp,
                            style = TextStyle(
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = LabelColor
                            )
                        )
                        Text(
                            text = problem.acceptance,
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium,
                                color = TitleColor
                            )
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (problem.hasVideoSolution) {
                            Icon(Icons.Rounded.PlayCircle, null, Modifier.size(16.dp), tint = accentColor)
                        }
                        if (problem.hasSolution) {
                            Icon(Icons.Rounded.Description, null, Modifier.size(16.dp), tint = LabelColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CircularCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = ProblemsGreen
) {
    val transition = updateTransition(targetState = checked, label = "checkboxTransition")
    val scale by transition.animateFloat(label = "scale") { if (it) 1.1f else 1.0f }

    Box(
        modifier = modifier
            .size(22.dp)
            .scale(scale)
            .clip(CircleShape)
            .background(if (checked) color else Color.Transparent)
            .border(
                width = 2.dp,
                color = if (checked) color else LabelColor.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onCheckedChange(!checked) },
        contentAlignment = Alignment.Center
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Rounded.Check,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}

@Composable
fun OverflowTagChip(count: Int) {
    Box(
        modifier = Modifier.padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "+$count",
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = Bold,
                color = LabelColor.copy(alpha = 0.8f)
            )
        )
    }
}

@Composable
private fun ProblemsTopAppBar(
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBackground.copy(alpha = 0.9f))
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = ""
                )
            }

            Text(
                text = "Set Weekly Goals",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val fullText = "Search problems..."
    var displayedText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        while (true) {
            displayedText = ""
            fullText.forEachIndexed { index, _ ->
                displayedText = fullText.substring(0, index + 1)
                delay(150)
            }
            delay(2000)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cursor")
    val cursorAlpha by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = keyframes { durationMillis = 500 },
            repeatMode = RepeatMode.Reverse
        ),
        label = "cursorAlpha"
    )

    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        placeholder = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = displayedText,
                    fontSize = 14.sp,
                    color = SearchBarPlaceholder
                )
                Box(
                    modifier = Modifier
                        .padding(start = 2.dp)
                        .width(2.dp)
                        .height(14.dp)
                        .alpha(if (query.isEmpty()) cursorAlpha else 0f)
                        .background(SearchBarPlaceholder)
                )
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Filled.Search,
                contentDescription = "Search",
                modifier = Modifier.size(15.dp),
                tint = SearchBarPlaceholder
            )
        },
        singleLine = true,
        shape = RoundedCornerShape(4.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = SearchBarBackground,
            unfocusedContainerColor = SearchBarBackground,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        )
    )
}



@Composable
fun ErrorCard(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Error,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = Color.Red
            )
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = Bold,
                    color = TitleColor
                )
            )
            Text(
                text = "Unable to fetch problems. Please try again!",
                fontSize = 13.sp,
                color = LabelColor,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = ProblemsGreen,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "Try Again",
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
private fun ProblemsTopAppBarPreview() {
    MaterialTheme {
        ProblemsTopAppBar {

        }
    }
}

@Preview
@Composable
private fun SearchBarPreview() {
    MaterialTheme {
        SearchBar(
            query = "",
            onQueryChange = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun ProblemCardPreview() {
    val a = ProblemEntity(
        problemId = 92,
        title = "Reverse Linked List i just checking how it behave II",
        titleSlug = "reverse-linked-list-ii",
        difficulty = "Easy",
        acceptance = "51.2%",
        isFree = true,
        hasSolution = true,
        hasVideoSolution = true,
        topicTags = listOf("Linked List", "Recursion")
    )
    MaterialTheme {
        ProblemCard(
            problem = a,
            isSelected = true,
            onClick = {},
            onProblemSelected = {},
        )
    }
}
