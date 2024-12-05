package com.byteutility.dev.leetcode.plus.network.requestVO

import com.google.gson.annotations.SerializedName

data class SampleRequestVo(
    @SerializedName("message")
    var message: Message? = Message()
)

data class Message(
    @SerializedName("token")
    var token: String? = null,
    @SerializedName("notification")
    var notification: Notification? = Notification()

)

data class Notification(
    @SerializedName("body")
    var body: String? = null,
    @SerializedName("title")
    var title: String? = null
)
