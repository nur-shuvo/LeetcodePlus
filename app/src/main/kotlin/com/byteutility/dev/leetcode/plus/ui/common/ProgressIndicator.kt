package com.byteutility.dev.leetcode.plus.ui.common

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ehsanmsz.mszprogressindicator.progressindicator.LineSpinFadeLoaderProgressIndicator

@Composable
@Preview
fun ProgressIndicator(modifier: Modifier = Modifier) {
    LineSpinFadeLoaderProgressIndicator(
        modifier = modifier.then(Modifier.size(60.dp)),
        rectWidth = 5.dp,
        rectHeight = 20.dp
    )
}
