package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.data.database.entity.ProblemEntity
import com.byteutility.dev.leetcode.plus.ui.theme.BadgeRed
import com.byteutility.dev.leetcode.plus.ui.theme.CardBorderColor
import com.byteutility.dev.leetcode.plus.ui.theme.EasyBg
import com.byteutility.dev.leetcode.plus.ui.theme.EasyText
import com.byteutility.dev.leetcode.plus.ui.theme.HardBg
import com.byteutility.dev.leetcode.plus.ui.theme.HardText
import com.byteutility.dev.leetcode.plus.ui.theme.LabelColor
import com.byteutility.dev.leetcode.plus.ui.theme.MediumBg
import com.byteutility.dev.leetcode.plus.ui.theme.MediumText
import com.byteutility.dev.leetcode.plus.ui.theme.OverflowTagBg
import com.byteutility.dev.leetcode.plus.ui.theme.OverflowTagBorder
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemNumberColor
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemsGreen
import com.byteutility.dev.leetcode.plus.ui.theme.SearchBarBackground
import com.byteutility.dev.leetcode.plus.ui.theme.SearchBarPlaceholder
import com.byteutility.dev.leetcode.plus.ui.theme.TagBackground
import com.byteutility.dev.leetcode.plus.ui.theme.TagText
import com.byteutility.dev.leetcode.plus.ui.theme.TitleColor
import com.byteutility.dev.leetcode.plus.ui.theme.TopBarBackground
import com.byteutility.dev.leetcode.plus.ui.theme.premiumLockColor
import kotlinx.coroutines.delay

private const val MAX_VISIBLE_TAGS = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProblemsScreen(
    onNavigateToProblemDetails: (String) -> Unit = {},
    viewModel: AllProblemsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
    val problems = viewModel.problems.collectAsLazyPagingItems()
    val showFilterSheet = remember { mutableStateOf(false) }

    if (showFilterSheet.value) {
        FilterBottomSheet(
            tags = state.tags,
            difficulties = state.difficulties,
            selectedTags = state.selectedTag,
            selectedDifficulties = state.selectedDifficulties,
            onTagSelected = viewModel::onTagSelected,
            onDifficultySelected = viewModel::onDifficultySelected,
            onApply = { showFilterSheet.value = false },
            onClear = viewModel::clearFilters,
            onDismiss = { showFilterSheet.value = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeContentPadding()
    ) {
        ProblemsTopAppBar(
            activeFilterCount = activeFilterCount,
            onFilterClick = { showFilterSheet.value = true }
        )
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
                    onProblemClick = { problem ->
                        problem.titleSlug?.let { onNavigateToProblemDetails(it) }
                    }
                )
            }
        }
    }
}

@Composable
private fun ProblemsTopAppBar(
    activeFilterCount: Int,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(TopBarBackground.copy(alpha = 0.9f))
            .statusBarsPadding()
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "Problems",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = ProblemsGreen,
                modifier = Modifier.padding(start = 16.dp)
            )
        }

        IconButton(onClick = onFilterClick) {
            BadgedBox(
                badge = {
                    if (activeFilterCount > 0) {
                        Badge(
                            containerColor = BadgeRed,
                            contentColor = Color.White,
                            modifier = Modifier.offset(x = (-4).dp, y = 4.dp) // Fine-tune badge position
                        ) {
                            Text(
                                text = activeFilterCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.FilterList,
                    contentDescription = "Filter",
                    tint = ProblemsGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val placeholders = listOf("problems...", "by tag...", "by difficulty...")
    var currentIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1500)
            currentIndex = (currentIndex + 1) % placeholders.size
        }
    }
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp)),
        placeholder = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Search ",
                    fontSize = 14.sp,
                    color = SearchBarPlaceholder
                )
                AnimatedContent(
                    targetState = placeholders[currentIndex],
                    transitionSpec = {
                        (slideInVertically { height -> height } + fadeIn())
                            .togetherWith(slideOutVertically { height -> -height } + fadeOut())
                    },
                    label = "PlaceholderAnimation"
                ) { targetText ->
                    Text(
                        text = targetText,
                        fontSize = 14.sp,
                        color = SearchBarPlaceholder
                    )
                }
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
private fun ProblemList(
    problems: LazyPagingItems<ProblemEntity>,
    onProblemClick: (ProblemEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            count = problems.itemCount,
            key = { index -> problems[index]?.problemId ?: index }
        ) { index ->
            val problem = problems[index]
            if (problem != null) {
                ProblemCard(
                    problem = problem,
                    onClick = { onProblemClick(problem) }
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
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, CardBorderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(17.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${problem.problemId}.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = ProblemNumberColor
                        )
                    )
                    Text(
                        text = problem.title,
                        fontSize = 16.sp,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TitleColor
                        ),
                        maxLines = 1,
                        modifier = Modifier
                            .weight(1f, fill = false)
                            .basicMarquee(
                                iterations = Int.MAX_VALUE,
                                initialDelayMillis = 2000
                            )
                    )
                    if (!problem.isFree) {
                        Icon(
                            imageVector = Icons.Filled.Lock,
                            contentDescription = "Premium",
                            modifier = Modifier.size(16.dp),
                            tint = premiumLockColor
                        )
                    }
                }
            }

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                DifficultyChip(difficulty = problem.difficulty)

                val tags = problem.topicTags.orEmpty()
                val visibleTags = tags.take(MAX_VISIBLE_TAGS)
                val overflowCount = tags.size - MAX_VISIBLE_TAGS

                visibleTags.forEach { tag ->
                    TopicTagChip(tag = tag)
                }

                if (overflowCount > 0) {
                    OverflowTagChip(count = overflowCount)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ACCEPTANCE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LabelColor,
                        letterSpacing = (-0.45).sp
                    )
                    Text(
                        text = problem.acceptance,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 12.sp,
                            color = TitleColor,
                            fontWeight = FontWeight.Normal
                        )
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "SOLUTION",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LabelColor,
                        letterSpacing = (-0.45).sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (problem.hasVideoSolution) {
                            Icon(
                                painter = painterResource(R.drawable.ic_video),
                                contentDescription = "Video solution",
                                modifier = Modifier.size(16.dp),
                                tint = LabelColor
                            )
                        }
                        if (problem.hasSolution) {
                            Icon(
                                painter = painterResource(R.drawable.ic_document),
                                contentDescription = "Article solution",
                                modifier = Modifier.size(16.dp),
                                tint = LabelColor
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyChip(difficulty: String) {
    val (bg, text) = when (difficulty.lowercase()) {
        "easy" -> EasyBg to EasyText
        "medium" -> MediumBg to MediumText
        "hard" -> HardBg to HardText
        else -> TagBackground to TagText
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bg
    ) {
        Text(
            text = difficulty,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = text
        )
    }
}

@Composable
fun TopicTagChip(tag: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = TagBackground
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
            fontSize = 12.sp,
            color = TagText
        )
    }
}

@Composable
fun OverflowTagChip(count: Int) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = OverflowTagBg,
        border = BorderStroke(1.dp, OverflowTagBorder)
    ) {
        Text(
            text = "+$count",
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = ProblemsGreen
        )
    }
}

@Composable
private fun ErrorCard(
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
                    fontWeight = FontWeight.Bold,
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

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
private fun AllProblemPreview() {
    val dummyProblems = listOf(
        ProblemEntity(
            problemId = 92,
            title = "Reverse Linked List i just checking how it behave II",
            titleSlug = "reverse-linked-list-ii",
            difficulty = "Medium",
            acceptance = "51.2%",
            isFree = true,
            hasSolution = true,
            hasVideoSolution = true,
            topicTags = listOf("Linked List", "Recursion")
        ),
        ProblemEntity(
            problemId = 5,
            title = "Longest Palindromic Substring",
            titleSlug = "longest-palindromic-substring",
            difficulty = "Medium",
            acceptance = "52.4%",
            isFree = true,
            hasSolution = true,
            hasVideoSolution = true,
            topicTags = listOf("String", "Dynamic Programming", "Two Pointers", "Manacher's Algorithm")
        ),
        ProblemEntity(
            problemId = 1,
            title = "Two Sum",
            titleSlug = "two-sum",
            difficulty = "Easy",
            acceptance = "49.8%",
            isFree = false,
            hasSolution = true,
            hasVideoSolution = false,
            topicTags = listOf("Array", "Hash Table")
        ),
        ProblemEntity(
            problemId = 25,
            title = "Reverse Nodes in k-Group",
            titleSlug = "reverse-nodes-in-k-group",
            difficulty = "Hard",
            acceptance = "55.1%",
            isFree = true,
            hasSolution = true,
            hasVideoSolution = true,
            topicTags = listOf("Linked List", "Recursion")
        ),
        ProblemEntity(
            problemId = 146,
            title = "LRU Cache",
            titleSlug = "lru-cache",
            difficulty = "Medium",
            acceptance = "41.2%",
            isFree = true,
            hasSolution = true,
            hasVideoSolution = false,
            topicTags = listOf("Hash Table", "Linked List", "Design", "Doubly-Linked List")
        ),
    )

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeContentPadding()
        ) {
            ProblemsTopAppBar(
                activeFilterCount = 3,
                onFilterClick = {}
            )
            SearchBar(
                query = "",
                onQueryChange = {},
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    count = dummyProblems.size,
                    key = { dummyProblems[it].problemId }
                ) { index ->
                    ProblemCard(
                        problem = dummyProblems[index],
                        onClick = {}
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ProblemCardPreview() {
    val dummy = ProblemEntity(
        problemId = 146,
        title = "LRU Cache",
        titleSlug = "lru-cache",
        difficulty = "Medium",
        acceptance = "41.2%",
        isFree = true,
        hasSolution = true,
        hasVideoSolution = true,
        topicTags = listOf("Hash Table", "Linked List", "Design", "Doubly-Linked List")
    )
    MaterialTheme {
        ProblemCard(
            problem = dummy
        ) { }
    }
}

@Preview
@Composable
private fun ErrorCardPreview() {
    MaterialTheme {
        ErrorCard(
            onRetry = {
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
