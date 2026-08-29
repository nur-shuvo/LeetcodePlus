package com.byteutility.dev.leetcode.plus.ui.screens.interview

import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SessionStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

/** A matched session becomes joinable this long before its start time. */
const val JOIN_WINDOW_MILLIS = 5 * 60 * 1000L

private const val TICK_MILLIS = 1000L

enum class InterviewSessionPhase(val label: String) {
    WAITING("Waiting"),
    MATCHED_UPCOMING("Matched"),
    JOINABLE("Starting soon"),
    ENDED("Ended"),
    EXPIRED("Ended"),
    CANCELLED("Cancelled"),
}

fun InterviewSession.phase(now: Long): InterviewSessionPhase = when {
    status == SessionStatus.CANCELLED -> InterviewSessionPhase.CANCELLED
    now >= endEpochMillis -> InterviewSessionPhase.ENDED
    now >= startEpochMillis - JOIN_WINDOW_MILLIS -> InterviewSessionPhase.JOINABLE
    else -> InterviewSessionPhase.MATCHED_UPCOMING
}

fun SlotBooking.phase(now: Long): InterviewSessionPhase = when {
    now >= endEpochMillis -> InterviewSessionPhase.EXPIRED
    else -> InterviewSessionPhase.WAITING
}

@Composable
fun statusChipColors(phase: InterviewSessionPhase) = when (phase) {
    InterviewSessionPhase.WAITING -> AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceVariant,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    InterviewSessionPhase.MATCHED_UPCOMING -> AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
    )
    InterviewSessionPhase.JOINABLE -> AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        labelColor = MaterialTheme.colorScheme.onTertiaryContainer,
    )
    InterviewSessionPhase.ENDED, InterviewSessionPhase.EXPIRED -> AssistChipDefaults.assistChipColors(
        containerColor = Color.Transparent,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant,
    )
    InterviewSessionPhase.CANCELLED -> AssistChipDefaults.assistChipColors(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        labelColor = MaterialTheme.colorScheme.onErrorContainer,
    )
}

/** Ticks every second so callers can recompute time-derived UI (phase, countdowns). */
@Composable
fun rememberNowTicker(): Long {
    val now by produceState(initialValue = System.currentTimeMillis()) {
        while (true) {
            value = System.currentTimeMillis()
            delay(TICK_MILLIS)
        }
    }
    return now
}

/** Formats a remaining duration as `H:MM:SS` / `MM:SS`, floored at zero. */
fun formatCountdown(remainingMillis: Long): String {
    val clamped = remainingMillis.coerceAtLeast(0)
    val hours = TimeUnit.MILLISECONDS.toHours(clamped)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(clamped) % TimeUnit.HOURS.toMinutes(1)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(clamped) % TimeUnit.MINUTES.toSeconds(1)
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}
