package com.byteutility.dev.leetcode.plus.network.responseVo

import com.google.gson.annotations.SerializedName

data class UserProfileVo(
    @SerializedName("about") val about: String,
    @SerializedName("avatar") val avatar: String,
    @SerializedName("birthday") val birthday: String?,
    @SerializedName("company") val company: String?,
    @SerializedName("country") val country: String?,
    @SerializedName("gitHub") val gitHub: String?,
    @SerializedName("linkedIN") val linkedIN: String?,
    @SerializedName("name") val name: String,
    @SerializedName("ranking") val ranking: Int,
    @SerializedName("reputation") val reputation: Int,
    @SerializedName("school") val school: String?,
    @SerializedName("skillTags") val skillTags: List<String> = emptyList(),
    @SerializedName("twitter") val twitter: String?,
    @SerializedName("username") val username: String,
    @SerializedName("website") val website: List<String> = emptyList()
)
