package com.byteutility.dev.leetcode.plus.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

@Composable
fun WeeklyGoalResetDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(text = "Reset Goal") },
            text = {
                val message = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontSize = 16.sp)) {
                        append("Are you sure you want to reset your goal?\n\n")
                    }
                    withStyle(style = SpanStyle(fontSize = 12.sp, color = Color.Gray)) {
                        append("Confirming will remove the problems that were unsolved")
                    }
                }
                Text(
                    text = message
                )
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm
                ) {
                    Text("OK")
                }
            }
        )
    }
}