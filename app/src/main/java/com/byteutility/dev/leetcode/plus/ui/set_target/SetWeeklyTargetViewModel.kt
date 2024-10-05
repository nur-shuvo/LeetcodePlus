package com.byteutility.dev.leetcode.plus.ui.set_target

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.network.RestApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    private val restApiService: RestApiService,
) : ViewModel() {
    fun getProblemsByLimit(limit: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                val response = restApiService.getProblemsWithLimit(limit)
                Log.i("SetWeeklyTargetViewModel", "response: $response")
            }.onFailure { it: Throwable ->
                it.printStackTrace()
            }
        }
    }
}