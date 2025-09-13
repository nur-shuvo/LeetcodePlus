package com.byteutility.dev.leetcode.plus.utils

import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

fun formatContestDate(utcDate: String): String {
    return try {
        val offsetDateTime = OffsetDateTime.parse(utcDate + "Z")
        val zonedDateTime = ZonedDateTime.ofInstant(offsetDateTime.toInstant(), ZoneId.systemDefault())
        val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
        zonedDateTime.format(formatter)
    } catch (e: Exception) {
        utcDate // Return original string if parsing fails
    }
}
