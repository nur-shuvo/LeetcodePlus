package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.byteutility.dev.leetcode.plus.ui.theme.ProblemsGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortingVisualizerScreen(viewModel: SortingViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var selectedAlgorithm by remember { mutableStateOf<SortingAlgorithm?>(null) }
    
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
                        selectedAlgorithm = it
                        viewModel.onAlgorithmSelected(it)
                    },
                    enabled = !state.isPlaying,
                    selectedAlgorithm = selectedAlgorithm
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Box(modifier = Modifier.weight(0.6f)) {
                SortingVisualizer(state = state)
            }
            
            if (state.algorithmCode.isNotEmpty()) {
                CodeVisualizer(
                    code = state.algorithmCode,
                    activeLineIndex = state.activeLineIndex,
                    modifier = Modifier
                        .weight(0.4f)
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
    selectedAlgorithm: SortingAlgorithm?,
    onAlgoClick: (SortingAlgorithm) -> Unit,
    enabled: Boolean
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(SortingAlgorithm.entries) { algo ->
            FilterChip(
                selected = algo == selectedAlgorithm,
                onClick = { onAlgoClick(algo) },
                label = { Text(algo.displayName) },
                enabled = enabled
            )
        }
    }
}

@Composable
fun SortingVisualizer(state: VisualizerState) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val barWidth = canvasWidth / state.numbers.size
        val maxVal = state.numbers.maxOrNull() ?: 1

        state.numbers.forEachIndexed { index, value ->
            val barHeight = (value.toFloat() / maxVal) * canvasHeight

            // Pick color based on state
            val color = when {
                index in state.currentlyComparing -> Color.Red
                index in state.sortedIndexes -> ProblemsGreen
                else -> ProblemsGreen.copy(alpha = 0.4f)
            }

            drawRect(
                color = color,
                topLeft = Offset(x = index * barWidth, y = canvasHeight - barHeight),
                size = Size(width = barWidth - 2f, height = barHeight)
            )
        }
    }
}
