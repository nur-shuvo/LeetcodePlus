package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class SampleResponseVo(
    @SerializedName("response")
    var message: String? = null
)