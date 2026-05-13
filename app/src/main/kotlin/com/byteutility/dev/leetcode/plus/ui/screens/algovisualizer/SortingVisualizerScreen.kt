package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteutility.dev.leetcode.plus.ui.theme.LeetcodePlusTheme
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemsGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingVisualizerScreen(viewModel: SortingViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onAlgorithmSelected("bubble_sort")
    }
    
    Scaffold(
        topBar = {
            Column {
                TopAppBar(title = {
                    Text(
                        text = "Algorithm Visualizer",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        ),
                        color = ProblemsGreen
                    )
                })
                AlgorithmSelector(
                    onAlgoClick = {
                        viewModel.onAlgorithmSelected(it.id)
                    },
                    enabled = !state.isPlaying,
                    algorithms = state.availableAlgorithms,
                    selectedAlgorithm = state.selectedAlgorithm
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            state.selectedAlgorithm?.let {
                AlgorithmDescription(
                    algorithm = it,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SortingVisualizer(state = state)
                }
                SortingLegend(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
            
            if (state.algorithmCode.isNotEmpty()) {
                CodeVisualizer(
                    code = state.algorithmCode,
                    activeLineIndex = state.activeLineIndex,
                    modifier = Modifier
                        .height(220.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            if (state.totalSteps > 0) {
                PlaybackControls(
                    state = state,
                    onTogglePlay = viewModel::togglePlayback,
                    onStepForward = viewModel::stepForward,
                    onStepBackward = viewModel::stepBackward,
                    onSeek = viewModel::seekTo
                )
            }
        }
    }
}

@Composable
fun AlgorithmDescription(
    algorithm: SortingAlgorithmInfo,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = algorithm.displayName,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = ProblemsGreen
            )
            Text(
                text = algorithm.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun CodeVisualizer(
    code: List<String>,
    activeLineIndex: Int?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            itemsIndexed(code) { index, line ->
                val isHighlighted = index == activeLineIndex
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            if (isHighlighted) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                            else Color.Transparent
                        )
                        .padding(vertical = 2.dp, horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.width(24.dp)
                    )
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                            color = if (isHighlighted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun PlaybackControls(
    state: VisualizerState,
    onTogglePlay: () -> Unit,
    onStepForward: () -> Unit,
    onStepBackward: () -> Unit,
    onSeek: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Step: ${state.currentStepIndex + 1} / ${state.totalSteps}",
            style = MaterialTheme.typography.bodyMedium
        )
        
        Slider(
            value = state.currentStepIndex.toFloat(),
            onValueChange = { onSeek(it.toInt()) },
            valueRange = 0f..(state.totalSteps - 1).coerceAtLeast(0).toFloat(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            IconButton(onClick = onStepBackward, enabled = state.currentStepIndex > 0) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Step Backward")
            }
            
            FloatingActionButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = if (state.isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (state.isPlaying) "Pause" else "Play"
                )
            }
            
            IconButton(onClick = onStepForward, enabled = state.currentStepIndex < state.totalSteps - 1) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Step Forward")
            }
        }
    }
}

@Composable
fun AlgorithmSelector(
    algorithms: List<SortingAlgorithmInfo>,
    selectedAlgorithm: SortingAlgorithmInfo?,
    onAlgoClick: (SortingAlgorithmInfo) -> Unit,
    enabled: Boolean
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(algorithms) { algo ->
            FilterChip(
                selected = algo.id == selectedAlgorithm?.id,
                onClick = { onAlgoClick(algo) },
                label = { Text(algo.displayName) },
                enabled = enabled
            )
        }
    }
}

@Composable
fun SortingLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LegendItem(color = Color.Red, label = "Comparing")
        LegendItem(color = ProblemsGreen, label = "Sorted / processed")
        LegendItem(color = ProblemsGreen.copy(alpha = 0.4f), label = "Unsorted")
    }
}

@Composable
fun LegendItem(
    color: Color,
    label: String
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SortingVisualizer(state: VisualizerState) {
    val labelColor = MaterialTheme.colorScheme.onSurface.toArgb()

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / state.numbers.size
        val maxVal = state.numbers.maxOrNull() ?: 1
        val showLabels = state.numbers.size <= 20
        val labelAreaHeight = if (showLabels) 20.sp.toPx() else 0f
        val availableBarHeight = (canvasHeight - labelAreaHeight).coerceAtLeast(1f)
        val labelPaint = Paint().apply {
            color = labelColor
            textAlign = Paint.Align.CENTER
            textSize = 12.sp.toPx()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            isAntiAlias = true
        }

        state.numbers.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / maxVal) * availableBarHeight

            // Pick color based on state
            val color = when {
                index in state.currentlyComparing -> Color.Red
                index in state.sortedIndexes -> ProblemsGreen
                else -> ProblemsGreen.copy(alpha = 0.4f)
            }
            val barLeft = index * barWidth
            val barTop = labelAreaHeight + availableBarHeight - barHeight
            val visibleBarWidth = (barWidth - 4f).coerceAtLeast(2f)

            drawRoundRect(
                color = color,
                topLeft = Offset(x = barLeft + 2f, y = barTop),
                size = Size(width = visibleBarWidth, height = barHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )

            if (showLabels) {
                drawContext.canvas.nativeCanvas.drawText(
                    value.toString(),
                    barLeft + barWidth / 2,
                    labelAreaHeight - 6f,
                    labelPaint
                )
            }
        }
    }
}

private val previewBubbleSortInfo = SortingAlgorithmInfo(
    id = "bubble_sort",
    displayName = "Bubble Sort",
    description = "Repeatedly compares adjacent values and moves the largest unsorted value to the end after each pass.",
    code = listOf(
        "for i in 0..n-2:",
        "  for j in 0..n-i-2:",
        "    if list[j] > list[j+1]:",
        "      swap(list[j], list[j+1])"
    )
)

private val previewInsertionSortInfo = SortingAlgorithmInfo(
    id = "insertion_sort",
    displayName = "Insertion Sort",
    description = "Builds a sorted prefix by taking one value at a time and inserting it into the correct position.",
    code = listOf(
        "for i in 1..n-1:",
        "  key = list[i]",
        "  j = i - 1",
        "  while j >= 0 and list[j] > key:",
        "    list[j+1] = list[j]",
        "    j--",
        "  list[j+1] = key"
    )
)

private val previewSelectionSortInfo = SortingAlgorithmInfo(
    id = "selection_sort",
    displayName = "Selection Sort",
    description = "Finds the minimum value from the unsorted part and places it at the next sorted position.",
    code = listOf(
        "for i in 0..n-1:",
        "  minIdx = i",
        "  for j in i+1..n-1:",
        "    if list[j] < list[minIdx]:",
        "      minIdx = j",
        "  swap(list[i], list[minIdx])"
    )
)

private val previewAlgorithms = listOf(
    previewBubbleSortInfo,
    previewInsertionSortInfo,
    previewSelectionSortInfo
)

private val previewVisualizerState = VisualizerState(
    numbers = listOf(72, 18, 95, 41, 63, 29, 84, 55, 10, 37),
    currentlyComparing = listOf(3, 4),
    sortedIndexes = listOf(7, 8, 9),
    currentStepIndex = 18,
    totalSteps = 64,
    isPlaying = false,
    isSorting = true,
    availableAlgorithms = previewAlgorithms,
    selectedAlgorithm = previewBubbleSortInfo,
    algorithmCode = previewBubbleSortInfo.code,
    activeLineIndex = 2
)

@Preview(showBackground = true)
@Composable
private fun AlgorithmSelectorPreview() {
    LeetcodePlusTheme {
        AlgorithmSelector(
            algorithms = previewAlgorithms,
            selectedAlgorithm = previewBubbleSortInfo,
            onAlgoClick = {},
            enabled = true
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlgorithmDescriptionPreview() {
    LeetcodePlusTheme {
        AlgorithmDescription(
            algorithm = previewBubbleSortInfo,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 300)
@Composable
private fun SortingVisualizerPreview() {
    LeetcodePlusTheme {
        SortingVisualizer(state = previewVisualizerState)
    }
}

@Preview(showBackground = true)
@Composable
private fun SortingLegendPreview() {
    LeetcodePlusTheme {
        SortingLegend(modifier = Modifier.padding(16.dp))
    }
}

@Preview(showBackground = true, heightDp = 220)
@Composable
private fun CodeVisualizerPreview() {
    LeetcodePlusTheme {
        CodeVisualizer(
            code = previewBubbleSortInfo.code,
            activeLineIndex = 2,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlaybackControlsPreview() {
    LeetcodePlusTheme {
        PlaybackControls(
            state = previewVisualizerState,
            onTogglePlay = {},
            onStepForward = {},
            onStepBackward = {},
            onSeek = {}
        )
    }
}

@Preview(showBackground = true, heightDp = 760)
@Composable
private fun SortingVisualizerContentPreview() {
    LeetcodePlusTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AlgorithmDescription(
                algorithm = previewBubbleSortInfo,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                Box(modifier = Modifier.weight(1f)) {
                    SortingVisualizer(state = previewVisualizerState)
                }
                SortingLegend(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            CodeVisualizer(
                code = previewBubbleSortInfo.code,
                activeLineIndex = 2,
                modifier = Modifier
                    .height(220.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            PlaybackControls(
                state = previewVisualizerState,
                onTogglePlay = {},
                onStepForward = {},
                onStepBackward = {},
                onSeek = {}
            )
        }
    }
}
