package com.byteutility.dev.leetcode.plus.ui.codeEditSubmit.utils

import android.content.Intent
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


inline fun <reified T> Intent.putExtraJson(key: String, value: T) {
    val jsonString = Json.encodeToString(value)
    this.putExtra(key, jsonString)
}

inline fun <reified T> Intent.getJsonExtra(key: String): T? {
    val jsonString = this.getStringExtra(key)
    return if (jsonString != null) {
        try {
            Json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        null
    }
}

fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}