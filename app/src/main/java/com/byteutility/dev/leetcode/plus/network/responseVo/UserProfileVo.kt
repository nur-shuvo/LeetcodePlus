package com.byteutility.dev.leetcode.plus.network.responseVo

data class UserProfileVo(
    val about: String,
    val avatar: String,
    val birthday: String,
    val company: String,
    val country: String,
    val gitHub: String,
    val linkedIN: String,
    val name: String,
    val ranking: Int,
    val reputation: Int,
    val school: String,
    val skillTags: List<String>,
    val twitter: String,
    val username: String,
    val website: List<String>
)
