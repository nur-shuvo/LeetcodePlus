package com.byteutility.dev.leetcode.plus.ui.screens.solutions.model

import com.google.api.services.youtube.model.Video

data class VideoSolutionsUiState(
    val videoState: MutableList<Video> = mutableListOf()
)
