package com.byteutility.dev.leetcode.plus.data.database.converter

import androidx.room.TypeConverter
import com.byteutility.dev.leetcode.plus.data.database.entity.TopicTag
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Created by Johny on 1/4/26.
 * Copyright (c) 2026 Pathao Ltd. All rights reserved.
 */

class Converters {
    private val gson = Gson()
    @TypeConverter
    fun fromTopicTagList(tags: List<TopicTag>): String {
        return gson.toJson(tags)
    }
    @TypeConverter
    fun toTopicTagList(json: String): List<TopicTag> {
        val type = object : TypeToken<List<TopicTag>>() {}.type
        return gson.fromJson(json, type)
    }
}