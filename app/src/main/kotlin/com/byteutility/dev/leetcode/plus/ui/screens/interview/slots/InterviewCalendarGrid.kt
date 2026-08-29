package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotCalendar
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val WEEKDAY_LABELS = listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa")
private const val DAY_CELL_MUTED_ALPHA = 0.4f

@Composable
fun InterviewCalendarGrid(
    visibleMonth: YearMonth,
    selectedDate: LocalDate?,
    datesWithSlots: Set<LocalDate>,
    canGoToPreviousMonth: Boolean,
    canGoToNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val weeks = remember(visibleMonth) { InterviewSlotCalendar.monthGridDates(visibleMonth) }

    Column(modifier = modifier.fillMaxWidth()) {
        CalendarHeader(
            visibleMonth = visibleMonth,
            canGoToPreviousMonth = canGoToPreviousMonth,
            canGoToNextMonth = canGoToNextMonth,
            onPreviousMonth = onPreviousMonth,
            onNextMonth = onNextMonth,
        )
        WeekdayLabelsRow()
        weeks.forEach { week ->
            WeekRow(
                week = week,
                selectedDate = selectedDate,
                datesWithSlots = datesWithSlots,
                onSelectDate = onSelectDate,
            )
        }
    }
}

@Composable
private fun CalendarHeader(
    visibleMonth: YearMonth,
    canGoToPreviousMonth: Boolean,
    canGoToNextMonth: Boolean,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonth, enabled = canGoToPreviousMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
        }
        val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())
        Text(text = "$monthName ${visibleMonth.year}", style = MaterialTheme.typography.titleMedium)
        IconButton(onClick = onNextMonth, enabled = canGoToNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next month")
        }
    }
}

@Composable
private fun WeekdayLabelsRow() {
    Row(modifier = Modifier.fillMaxWidth()) {
        WEEKDAY_LABELS.forEach { label ->
            Text(
                text = label,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun WeekRow(
    week: List<LocalDate?>,
    selectedDate: LocalDate?,
    datesWithSlots: Set<LocalDate>,
    onSelectDate: (LocalDate) -> Unit,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        week.forEach { date ->
            if (date != null) {
                DayCell(
                    date = date,
                    isSelected = date == selectedDate,
                    hasSlots = date in datesWithSlots,
                    onClick = { onSelectDate(date) },
                    modifier = Modifier.weight(1f),
                )
            } else {
                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    hasSlots: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val cellModifier = modifier
        .aspectRatio(1f)
        .padding(2.dp)
        .clip(CircleShape)
        .background(backgroundColor)
        .then(if (hasSlots) Modifier.clickable(onClick = onClick) else Modifier)

    Box(modifier = cellModifier, contentAlignment = Alignment.Center) {
        Text(
            text = date.dayOfMonth.toString(),
            color = when {
                isSelected -> MaterialTheme.colorScheme.onPrimary
                hasSlots -> MaterialTheme.colorScheme.onSurface
                else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = DAY_CELL_MUTED_ALPHA)
            },
        )
    }
}
