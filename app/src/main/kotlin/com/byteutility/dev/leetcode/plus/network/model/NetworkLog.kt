package com.byteutility.dev.leetcode.plus.network.model

import java.util.UUID

data class NetworkLog(
    val id: String = UUID.randomUUID().toString(),
    val method: String,
    val url: String,
    val requestHeaders: Map<String, String>,
    val requestBody: String?,
    val responseCode: Int,
    val responseHeaders: Map<String, String>,
    val responseBody: String?,
    val durationMs: Long,
    val timestamp: Long,
    val error: String? = null,
)
