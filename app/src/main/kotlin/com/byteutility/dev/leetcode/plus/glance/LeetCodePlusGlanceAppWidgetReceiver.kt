package com.byteutility.dev.leetcode.plus.glance

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import com.byteutility.dev.leetcode.plus.glance.ui.LeetcodePlusGlanceAppWidget

class LeetCodePlusGlanceAppWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = LeetcodePlusGlanceAppWidget()
}
