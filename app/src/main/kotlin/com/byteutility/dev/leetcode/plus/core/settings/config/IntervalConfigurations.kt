package com.byteutility.dev.leetcode.plus.core.settings.config

import com.byteutility.dev.leetcode.plus.core.settings.model.IntervalOption

@Suppress("MagicNumber")
enum class IntervalEnum(val minutes: Long) {
    FIFTEEN(15L),
    THIRTY(30L),
    HOUR(60L),
    TWO_HOURS(120L),
    THREE_HOURS(180L)
}

object IntervalConfigurations {
    val DATA_SYNC_DEFAULT_INTERVAL = IntervalEnum.THIRTY
    val NOTIFICATION_DEFAULT_INTERVAL = IntervalEnum.THREE_HOURS

    val syncIntervalOptions: List<IntervalOption> = IntervalEnum.entries.map {
        IntervalOption(time = it.minutes, isDefault = (it == DATA_SYNC_DEFAULT_INTERVAL))
    }

    val notificationIntervalOptions: List<IntervalOption> = IntervalEnum.entries.map {
        IntervalOption(time = it.minutes, isDefault = (it == NOTIFICATION_DEFAULT_INTERVAL))
    }
}
