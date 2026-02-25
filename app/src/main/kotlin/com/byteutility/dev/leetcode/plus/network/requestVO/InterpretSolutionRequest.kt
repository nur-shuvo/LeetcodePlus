package com.byteutility.dev.leetcode.plus.network.requestVO

import com.google.gson.annotations.SerializedName

data class InterpretSolutionRequest(
    @SerializedName("lang") val lang: String,
    @SerializedName("question_id") val questionId: String,
    @SerializedName("typed_code") val typedCode: String,
    @SerializedName("data_input") val dataInput: String,
)
