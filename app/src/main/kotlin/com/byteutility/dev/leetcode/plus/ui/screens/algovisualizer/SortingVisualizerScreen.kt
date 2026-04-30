package com.byteutility.dev.leetcode.plus.ui.screens.algovisualizer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
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
    var selectedAlgorithm: SortingAlgorithm? = remember { null }
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
                    enabled = !state.isSorting,
                    selectedAlgorithm = selectedAlgorithm
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                selectedAlgorithm = null
                viewModel.onReset()
            }) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            SortingVisualizer(state = state)
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
                selected = algo == selectedAlgorithm, // You can track the current active algo state
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
                index in state.sortedIndexes -> Color.Green
                else -> Color.Blue
            }

            drawRect(
                color = color,
                topLeft = Offset(x = index * barWidth, y = canvasHeight - barHeight),
                size = Size(width = barWidth - 2f, height = barHeight) // -2f for spacing
            )
        }
    }
}
