package com.byteutility.dev.leetcode.plus.glance.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.text.Text
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class LeetcodePlusGlanceAppWidget : GlanceAppWidget() {

    override suspend fun provideGlance(
        context: Context,
        id: GlanceId
    ) {
        provideContent {
            val context = LocalContext.current
            val datastore = EntryPointAccessors.fromApplication(
                context.applicationContext,
                WidgetEntryPoint::class.java
            ).userDatastore()
            val dailyProblem = remember { mutableStateOf<LeetCodeProblem?>(null) }
            var isLoading = remember { mutableStateOf(true) }
            LaunchedEffect(Unit) {
                datastore.getDailyProblem().collect {
                    dailyProblem.value = it
                    isLoading.value = false
                }
            }
            if (isLoading.value) {

            } else {
                dailyProblem.value?.let {
                    Widget(it)
                }
            }
        }
    }

    @Composable
    private fun Widget(
        problem: LeetCodeProblem
    ) {
        Box(
            modifier = GlanceModifier
                .background(Color.White)
        ) {
            Box(
                modifier = GlanceModifier
                    .padding(8.dp)
                    .background(Color(0xFF4CAF50)) // Green color
                    .fillMaxWidth()
                    .cornerRadius(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "LeetCode Daily Problem",
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = problem.title,
                    )
                }
            }
        }
    }
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface WidgetEntryPoint {
    fun userDatastore(): UserDatastore
}
