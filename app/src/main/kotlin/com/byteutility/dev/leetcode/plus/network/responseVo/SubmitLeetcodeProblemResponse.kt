package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName


/**
 * Created by Shuvo on 11/09/2025.
 */
class SubmitLeetcodeProblemResponse(
    @SerializedName("submission_id")
    val submissionId: Long
)