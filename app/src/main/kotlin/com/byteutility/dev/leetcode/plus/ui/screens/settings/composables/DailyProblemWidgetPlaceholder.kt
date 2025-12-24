package com.byteutility.dev.leetcode.plus.ui.screens.settings.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp


@Composable
fun DailyProblemWidgetPlaceholder() {

    Box(
        modifier = Modifier
            .padding(top = 16.dp)
            .fillMaxWidth()
            .background(Color.White)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.errorContainer)
            .clip(RoundedCornerShape(16.dp))
            .padding(16.dp)
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
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "A very hard problem of DP",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = TextUnit(20.0F, TextUnitType.Sp)
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewDailyProblemWidgetPlaceholder() {
    DailyProblemWidgetPlaceholder()
}
