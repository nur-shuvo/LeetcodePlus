package com.byteutility.dev.leetcode.plus.ui.model

import com.byteutility.dev.leetcode.plus.data.model.ProblemStatus

data class ProgressUiState(
    val problemsWithStatus: List<ProblemStatus> = emptyList()
)