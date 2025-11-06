package com.byteutility.dev.leetcode.plus.ui.screens.solutions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.repository.userDetails.UserDetailsRepository
import com.byteutility.dev.leetcode.plus.ui.screens.solutions.model.VideoSolutionsUiState
import com.google.api.services.youtube.model.Video
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideoSolutionsViewModel @Inject constructor(
    private val userDetailsRepository: UserDetailsRepository
) : ViewModel() {

    companion object {
        private val playListIDList = listOf(
            "PLiX7zQQX6FZMwPNeACDFvPDsUiu7AbZ8R", // Ethos typos leetcode problems
            "PLot-Xpze53ldVwtstag2TL4HQhAnC8ATf" // Neetcode blind 75
        )
    }

    private val videosState = MutableStateFlow(mutableListOf<Video>())

    val uiState: StateFlow<VideoSolutionsUiState> = combine(
        listOf(videosState)
    ) { values ->
        VideoSolutionsUiState(
            values[0]
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = VideoSolutionsUiState()
    )

    init {
        viewModelScope.launch(Dispatchers.IO) {
            playListIDList.forEach { id ->
                var pageTokenForPlayList: String? = null
                do {
                    runCatching {
                        val result = userDetailsRepository.getVideosByPlayList(
                            pageTokenForPlayList,
                            id
                        )
                        pageTokenForPlayList = result.nextPageToken
                        videosState.update { currentVideos ->
                            currentVideos.plus(result.videos) as MutableList<Video>
                        }
                    }.onFailure {}
                } while (!pageTokenForPlayList.isNullOrBlank())
            }
        }
    }
}