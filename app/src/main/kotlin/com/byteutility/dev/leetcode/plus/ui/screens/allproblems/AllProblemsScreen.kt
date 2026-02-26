package com.byteutility.dev.leetcode.plus.ui.screens.allproblems

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.domain.model.SetMetadata
import com.byteutility.dev.leetcode.plus.ui.theme.easyCategory
import com.byteutility.dev.leetcode.plus.ui.theme.hardCategory
import com.byteutility.dev.leetcode.plus.ui.theme.mediumCategory
import kotlinx.coroutines.delay

/**
 * Created by Shuvo on 11/24/2025.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllProblemsScreen(
    onPopCurrent: () -> Unit = {},
    onNavigateToProblemDetails: (String) -> Unit = {}
) {
    val viewModel: AllProblemsViewModel = hiltViewModel()
    val problems by viewModel.problemsList.collectAsStateWithLifecycle()
    val tags by viewModel.tags.collectAsStateWithLifecycle()
    val difficulties by viewModel.difficulties.collectAsStateWithLifecycle()
    val selectedDifficulties by viewModel.selectedDifficulties.collectAsStateWithLifecycle()
    val selectedTags by viewModel.selectedTags.collectAsStateWithLifecycle()
    var showFilterBottomSheet by remember { mutableStateOf(false) }
    val activeFilterCount by viewModel.activeFilterCount.collectAsStateWithLifecycle()
    val selectedStaticProblemSet by viewModel.selectedStaticProblemSet.collectAsStateWithLifecycle()
    val predefinedProblemSet = viewModel.predefinedProblemSets

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "All Problems", fontWeight = FontWeight.Bold) },
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
                ),
                actions = {
                    IconButton(onClick = {
                        showFilterBottomSheet = true
                    }) {
                        BadgedBox(
                            badge = {
                                if (activeFilterCount > 0) {
                                    Badge {
                                        Text(activeFilterCount.toString())
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter Problems"
                            )
                        }
                    }
                }
            )
        },
    ) { innerPadding ->

        if (showFilterBottomSheet) {
            FilterBottomSheet(
                problemSets = predefinedProblemSet,
                selectedStaticProblemSet = selectedStaticProblemSet,
                selectedTags = selectedTags,
                selectedDifficulties = selectedDifficulties,
                tags = tags,
                difficulties = difficulties,
                onTagSelected = { viewModel.onTagSelected(it) },
                onDifficultySelected = { viewModel.onDifficultySelected(it) },
                onApply = { showFilterBottomSheet = false },
                onClear = {
                    viewModel.clearFilters()
                    showFilterBottomSheet = false
                },
                onDismiss = { showFilterBottomSheet = false },
                onProblemSetSelected = { viewModel.onProblemSetSelected(it) }
            )
        }

        ProblemSelection(
            modifier = Modifier.padding(innerPadding),
            problems = problems,
            onNavigateToProblemDetails = onNavigateToProblemDetails
        )
    }
}

@Composable
fun ProblemSelection(
    modifier: Modifier = Modifier,
    problems: List<LeetCodeProblem>,
    onNavigateToProblemDetails: (String) -> Unit = {}
) {
    var currentPage by remember { mutableIntStateOf(0) }
    var searchText by remember { mutableStateOf("") }
    val placeholders = listOf("Search Problems", "Search by tag", "Search by difficulty")
    var placeholderIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(1000)
            placeholderIndex = (placeholderIndex + 1) % placeholders.size
        }
    }

    val animatedPlaceholder: @Composable () -> Unit = {
        Crossfade(targetState = placeholderIndex, label = "placeholder") {
            Text(placeholders[it], fontSize = 18.sp)
        }
    }

    val itemsPerPage = 50
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
            .fillMaxSize()
            .then(modifier)
    ) {
        TextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = animatedPlaceholder,
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
                .padding(start = 16.dp, end = 16.dp)
                .weight(1.0f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
        ) {
            items(displayedItems) { problem ->
                ProblemItem(
                    problem = problem,
                    onNavigateToProblemDetails
                )
            }
        }

        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
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
    }
}

@Composable
fun ProblemItem(
    problem: LeetCodeProblem,
    onNavigateToProblemDetails: (String) -> Unit = {}
) {
    val backgroundColor: Color = getDifficultyColor(problem.difficulty)
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
    ) {
        Row(
            modifier = Modifier
                .clickable {
                    onNavigateToProblemDetails.invoke(problem.titleSlug)
                }
                .fillMaxWidth()
                .background(backgroundColor)
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Text(
                    text = problem.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("Tag: ")
                        }
                        append(problem.tag)
                    }, style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                            append("Difficulty: ")
                        }
                        append(problem.difficulty)
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

@Composable
fun getDifficultyColor(difficulty: String): Color {
    return when (difficulty.lowercase()) {
        "easy" -> MaterialTheme.colorScheme.easyCategory
        "medium" -> MaterialTheme.colorScheme.mediumCategory
        "hard" -> MaterialTheme.colorScheme.hardCategory
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FilterBottomSheet(
    problemSets: List<SetMetadata>,
    tags: List<String>,
    difficulties: List<String>,
    onTagSelected: (String) -> Unit,
    onDifficultySelected: (String) -> Unit,
    onApply: () -> Unit,
    onClear: () -> Unit,
    onDismiss: () -> Unit,
    onProblemSetSelected: (SetMetadata) -> Unit,
    selectedTags: List<String>,
    selectedDifficulties: List<String>,
    selectedStaticProblemSet: SetMetadata?
) {
    val scrollState = rememberScrollState()
    val modalBottomSheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = modalBottomSheetState,
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Filters", style = MaterialTheme.typography.headlineSmall)

            Text(text = "Difficulty", style = MaterialTheme.typography.titleMedium)
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                difficulties.forEach { difficulty ->
                    FilterChip(
                        selected = selectedDifficulties.contains(difficulty),
                        onClick = {
                            onDifficultySelected(difficulty)
                        },
                        label = { Text(difficulty) },
                        leadingIcon = if (selectedDifficulties.contains(difficulty)) {
                            {
                                Icon(Icons.Default.Check, contentDescription = "")
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = "Problem Sets", style = MaterialTheme.typography.titleMedium)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                problemSets.forEach { set ->
                    FilterChip(
                        selected = if (selectedStaticProblemSet == null) false else set == selectedStaticProblemSet,
                        onClick = { onProblemSetSelected(set) },
                        label = { Text(set.name) },
                        leadingIcon = if (selectedStaticProblemSet == set) {
                            { Icon(Icons.Default.Check, contentDescription = null) }
                        } else {
                            null
                        }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Text(text = "Tags", style = MaterialTheme.typography.titleMedium)

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                tags.forEach { tag ->
                    FilterChip(
                        selected = selectedTags.contains(tag),
                        onClick = {
                            onTagSelected(tag)
                        },
                        label = { Text(tag) },
                        leadingIcon = if (selectedTags.contains(tag)) {
                            {
                                Icon(Icons.Default.Check, contentDescription = "")
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onClear,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
                Button(
                    onClick = onApply,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Filters")
                }
            }
        }
    }
}

@Composable
@Preview
fun PreviewFilterBottomSheet() {
    FilterBottomSheet(
        tags = listOf("Array", "String", "Hash Table"),
        difficulties = listOf("Easy", "Medium", "Hard"),
        onTagSelected = {},
        onDifficultySelected = {},
        onApply = {},
        onClear = {},
        onDismiss = {},
        selectedTags = listOf("Array", "String"),
        selectedDifficulties = listOf("Easy", "Medium"),
        problemSets = emptyList(),
        onProblemSetSelected = {},
        selectedStaticProblemSet = null
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun ProblemSelectionPreview() {
    val problems = remember { getDummyProblems() }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "All Problems", fontWeight = FontWeight.Bold) })
        },
    ) { innerPadding ->
        ProblemSelection(
            Modifier.padding(innerPadding),
            problems = problems,
            onNavigateToProblemDetails = {},
        )
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
