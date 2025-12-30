package com.byteutility.dev.leetcode.plus.ui.screens.solutions

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.byteutility.dev.leetcode.plus.ui.common.AdBannerAdaptive
import com.byteutility.dev.leetcode.plus.ui.model.YouTubeVideo
import com.google.api.services.youtube.model.Video

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoSolutionsScreen(
    onPopCurrent: () -> Unit = {},
) {
    val viewModel: VideoSolutionsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "Leetcode Videos") },
                    navigationIcon = {
                        IconButton(onClick = { onPopCurrent() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFABDEF5).copy(alpha = 0.1f)
                    )
                )
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by title") },
                    leadingIcon = {
                        Icon(Icons.Filled.Search, contentDescription = "Search")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFE3F2FD),
                        unfocusedContainerColor = Color(0xFFE3F2FD),
                        focusedIndicatorColor = Color.Blue,
                        unfocusedIndicatorColor = Color.Gray
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }
        },
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            YouTubeVideoGrid(
                videos = uiState.videoState,
                searchQuery = searchQuery.text,
                modifier = Modifier.weight(1f)
            )
            AdBannerAdaptive(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun YouTubeVideoGrid(
    videos: MutableList<Video> = mutableListOf(),
    searchQuery: String,
    modifier: Modifier
) {
    val context = LocalContext.current
    val filteredVideos = videos.filter {
        it.snippet.title.contains(searchQuery, ignoreCase = true)
    }.map {
        YouTubeVideo(
            videoId = it.id,
            thumbnailUrl = it.snippet.thumbnails.high.url,
            title = it.snippet.title
        )
    }

    Column(modifier = modifier) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredVideos) { video ->
                Box(
                    modifier = Modifier
                        .wrapContentSize()
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
                    val painter = rememberAsyncImagePainter(
                        model = ImageRequest.Builder(context)
                            .data(video.thumbnailUrl)
                            .crossfade(true)
                            .build()
                    )

                    val alphaAnim = remember { Animatable(0f) }
                    LaunchedEffect(painter.state) {
                        alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 500))
                    }

                    AsyncImage(
                        model = video.thumbnailUrl,
                        contentDescription = "YouTube Thumbnail",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .alpha(alphaAnim.value),
                    )
                }
            }
        }
    }
}
