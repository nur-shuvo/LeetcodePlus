package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessiondetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.JOIN_WINDOW_MILLIS
import com.byteutility.dev.leetcode.plus.ui.screens.interview.formatCountdown
import com.byteutility.dev.leetcode.plus.ui.screens.interview.phase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.rememberNowTicker
import com.byteutility.dev.leetcode.plus.ui.screens.interview.statusChipColors
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSessionDetailScreen(
    onBack: () -> Unit = {},
    onSubmitFeedback: (sessionId: String, rateeUid: String) -> Unit = { _, _ -> },
    viewModel: InterviewSessionDetailViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val session by viewModel.session.collectAsStateWithLifecycle()
    val peerProfile by viewModel.peerProfile.collectAsStateWithLifecycle()
    val now = rememberNowTicker()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mock Interview") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val currentSession = session ?: return@Column
            val phase = currentSession.phase(now)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = currentSession.role.displayName, style = MaterialTheme.typography.titleLarge)
                AssistChip(onClick = {}, label = { Text(phase.label) }, colors = statusChipColors(phase))
            }
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(currentSession.startEpochMillis))
            )

            peerProfile?.let { peer ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Interviewing with", style = MaterialTheme.typography.labelMedium)
                        Text(text = peer.displayName, style = MaterialTheme.typography.titleMedium)
                        if (peer.leetcodeHandle.isNotEmpty()) {
                            Text(text = "LeetCode: ${peer.leetcodeHandle}")
                        }
                    }
                }
            }

            SessionAction(
                session = currentSession,
                phase = phase,
                now = now,
                onJoin = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(currentSession.meetingUrl)))
                },
                onSubmitFeedback = {
                    onSubmitFeedback(currentSession.sessionId, peerProfile?.uid.orEmpty())
                }
            )
        }
    }
}

@Composable
private fun SessionAction(
    session: InterviewSession,
    phase: InterviewSessionPhase,
    now: Long,
    onJoin: () -> Unit,
    onSubmitFeedback: () -> Unit,
) {
    when (phase) {
        InterviewSessionPhase.CANCELLED -> Text("This session was cancelled.")
        InterviewSessionPhase.ENDED, InterviewSessionPhase.EXPIRED ->
            Button(onClick = onSubmitFeedback) { Text("Submit Feedback") }
        InterviewSessionPhase.JOINABLE ->
            Button(onClick = onJoin) { Text("Join Video Call") }
        InterviewSessionPhase.MATCHED_UPCOMING, InterviewSessionPhase.WAITING -> {
            val untilJoinable = session.startEpochMillis - JOIN_WINDOW_MILLIS - now
            Text(
                text = "Join opens in ${formatCountdown(untilJoinable)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
