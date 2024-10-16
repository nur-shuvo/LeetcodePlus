package com.byteutility.dev.leetcode.plus.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getDateFromTimestamp(timestamp: Long): String {
    val date = Date(timestamp * 1000)
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a", Locale.getDefault())
    return dateFormat.format(date)
}
