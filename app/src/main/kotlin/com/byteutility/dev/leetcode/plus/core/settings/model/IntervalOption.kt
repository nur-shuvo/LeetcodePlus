package com.byteutility.dev.leetcode.plus.core.settings.model

@Suppress("MagicNumber")
data class IntervalOption(
    val time: Long,
    val isDefault: Boolean = false
) {

    val displayLabel: String
        get() = when {
            time < 60 -> "$time mins"
            time % 60 == 0L -> "${time / 60} ${if (time == 60L) "hour" else "hours"}"
            else -> "$time mins"
        }
}
