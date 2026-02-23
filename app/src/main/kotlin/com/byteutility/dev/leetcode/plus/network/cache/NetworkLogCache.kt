package com.byteutility.dev.leetcode.plus.network.cache

import com.byteutility.dev.leetcode.plus.network.model.NetworkLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object NetworkLogCache {

    private const val MAX_LOGS = 200

    private val _logs = MutableStateFlow<List<NetworkLog>>(emptyList())
    val logs: StateFlow<List<NetworkLog>> = _logs.asStateFlow()

    fun add(log: NetworkLog) {
        _logs.update { current ->
            (listOf(log) + current).take(MAX_LOGS)
        }
    }

    fun clear() {
        _logs.value = emptyList()
    }

    fun getById(id: String): NetworkLog? = _logs.value.find { it.id == id }
}
