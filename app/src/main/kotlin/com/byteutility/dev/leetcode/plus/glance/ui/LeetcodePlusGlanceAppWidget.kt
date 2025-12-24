package com.byteutility.dev.leetcode.plus.glance.ui

import android.content.Context
import android.content.Intent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.LocalContext
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.actionStartActivity
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
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.byteutility.dev.leetcode.plus.data.datastore.UserDatastore
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.ui.MainActivity
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
                UserDataStoreProviderEntryPoint::class.java
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
                    DailyProblemWidget(it)
                }
            }
        }
    }

    @Composable
    private fun DailyProblemWidget(
        problem: LeetCodeProblem
    ) {
        val context = LocalContext.current
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("dailyProblemTitleSlug", problem.titleSlug)
        }

        Box(
            modifier = GlanceModifier
                .padding(8.dp)
                .background(ColorProvider(Color.White))
                .fillMaxWidth()
                .cornerRadius(16.dp)
        ) {
            Box(
                modifier = GlanceModifier
                    .padding(12.dp)
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .fillMaxWidth()
                    .cornerRadius(12.dp)
                    .clickable(actionStartActivity(intent))
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text = "Problem of the day",
                        style = TextStyle(
                            fontSize = TextUnit(16.0F, TextUnitType.Sp)
                        )
                    )
                    Spacer(modifier = GlanceModifier.height(4.dp))
                    Text(
                        text = problem.title,
                        style = TextStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = TextUnit(20.0F, TextUnitType.Sp)
                        )
                    )
                }
            }
        }
    }
}


@EntryPoint
@InstallIn(SingletonComponent::class)
interface UserDataStoreProviderEntryPoint {
    fun userDatastore(): UserDatastore
}
