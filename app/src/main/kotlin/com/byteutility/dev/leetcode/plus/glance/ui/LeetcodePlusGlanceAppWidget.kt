package com.byteutility.dev.leetcode.plus.glance.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text

class LeetcodePlusGlanceAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            Widget()
        }
    }

    @Composable
    private fun Widget() {
        Box(
            modifier = GlanceModifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth()
                .cornerRadius(12.dp)
                .background(Color(0xFF4CAF50))
        ) {
            Row(
                modifier = GlanceModifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(
                    modifier = GlanceModifier.defaultWeight(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "LeetCode Daily Problem",
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = "Solve today for a streak!",
                    )
                }
            }
        }
    }
}
