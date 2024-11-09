package com.byteutility.dev.leetcode.plus.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.byteutility.dev.leetcode.plus.data.model.WeeklyGoalPeriod
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun WeeklyGoalSetDialog(
    confirmed: (period: WeeklyGoalPeriod) -> Unit
) {
    var showDialog by remember { mutableStateOf(true) }
    val today = LocalDate.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
    val startDate = today.format(formatter)
    val endDate = today.plusDays(7).format(formatter)

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirming Weekly Goal") },
            text = {
                Text(
                    "Weekly goal from today ($startDate) until the next 7 days ($endDate)"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirmed.invoke(WeeklyGoalPeriod(startDate, endDate))
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            }
        )
    }
}