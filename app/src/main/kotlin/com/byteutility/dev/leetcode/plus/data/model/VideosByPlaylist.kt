package com.byteutility.dev.leetcode.plus.data.model

import com.google.api.services.youtube.model.Video

data class VideosByPlaylist(
    val videos: List<Video>,
    val nextPageToken: String?
)
