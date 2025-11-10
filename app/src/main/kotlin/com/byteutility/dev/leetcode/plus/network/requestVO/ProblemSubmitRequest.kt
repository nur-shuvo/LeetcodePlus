package com.byteutility.dev.leetcode.plus.network.requestVO

import com.google.gson.annotations.SerializedName


/**
 * Created by Shuvo on 11/09/2025.
 */
data class ProblemSubmitRequest(
    @SerializedName("lang")
    val lang: String,
    @SerializedName("typed_code")
    val typedCode: String,
    @SerializedName("question_id")
    val questionId: String,
)