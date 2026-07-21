package com.byteutility.dev.leetcode.plus.ui.screens.interview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private data class HowItWorksStep(val icon: ImageVector, val label: String)

private val HOW_IT_WORKS_STEPS = listOf(
    HowItWorksStep(Icons.Filled.Person, "Set up profile"),
    HowItWorksStep(Icons.Filled.CalendarToday, "Book a slot"),
    HowItWorksStep(Icons.Filled.People, "Get matched"),
    HowItWorksStep(Icons.Filled.Videocam, "Join & practice"),
)

private val HOW_IT_WORKS_BULLETS = listOf(
    "Pick the role(s) you want to practice and sign in with Google",
    "Book any open time slot - 3 times a day, up to 14 days ahead",
    "Matched instantly if someone's already waiting on that slot",
    "Otherwise you wait, and get notified the moment a peer books it too",
    "Join your video call once it's within 5 minutes of starting",
    "Leave quick feedback for your peer afterward",
)

/**
 * Static "how it works" explanation for the mock-interview feature: a 4-step icon summary plus
 * a bulleted walkthrough. Reused both inline (expandable on the profile setup screen) and inside
 * [InterviewHowItWorksDialog] (on-demand from the session list) - this is the single source of
 * truth for the copy so neither consumer duplicates it.
 */
@Composable
fun InterviewHowItWorksContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            HOW_IT_WORKS_STEPS.forEach { step -> HowItWorksStepIcon(step, modifier = Modifier.weight(1f)) }
        }
        HOW_IT_WORKS_BULLETS.forEach { bullet ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                Text(text = bullet, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun HowItWorksStepIcon(step: HowItWorksStep, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(imageVector = step.icon, contentDescription = null)
        Text(text = step.label, style = MaterialTheme.typography.labelSmall, textAlign = TextAlign.Center)
    }
}

/** On-demand "how it works" dialog, opened from an info icon on the session list. */
@Composable
fun InterviewHowItWorksDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How Mock Interviews Work") },
        text = { InterviewHowItWorksContent() },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Got it") }
        }
    )
}
