package com.byteutility.dev.leetcode.plus.ui.targetset

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.repository.ProblemsRepository
import com.byteutility.dev.leetcode.plus.network.RestApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetWeeklyTargetViewModel @Inject constructor(
    private val repository: ProblemsRepository
) : ViewModel() {

    val problemsList = MutableStateFlow<List<LeetCodeProblem>>(emptyList())

    init {
        viewModelScope.launch(Dispatchers.IO) {
            problemsList.value = repository.getProblems(limit = 3000)
        }
    }
}