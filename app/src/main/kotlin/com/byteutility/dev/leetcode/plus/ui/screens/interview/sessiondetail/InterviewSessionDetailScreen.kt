package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessiondetail

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.SessionStatus
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

            Text(text = currentSession.role.displayName, style = MaterialTheme.typography.titleLarge)
            Text(
                text = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                    .format(Date(currentSession.startEpochMillis))
            )

            peerProfile?.let { peer ->
                Text(text = "Interviewing with ${peer.displayName}")
                if (peer.leetcodeHandle.isNotEmpty()) {
                    Text(text = "LeetCode: ${peer.leetcodeHandle}")
                }
            }

            when (currentSession.status) {
                SessionStatus.PENDING_CALENDAR -> Text("Setting up your Google Meet link...")
                SessionStatus.CALENDAR_FAILED -> Text(
                    text = "Couldn't create a meeting link automatically. " +
                        "Please coordinate with your peer directly.",
                    color = MaterialTheme.colorScheme.error
                )
                SessionStatus.CONFIRMED -> {
                    Button(onClick = {
                        context.startActivity(
                            Intent(Intent.ACTION_VIEW, Uri.parse(currentSession.meetLink))
                        )
                    }) {
                        Text("Join Google Meet")
                    }
                }
                SessionStatus.COMPLETED -> {
                    Button(onClick = {
                        onSubmitFeedback(currentSession.sessionId, peerProfile?.uid.orEmpty())
                    }) {
                        Text("Submit Feedback")
                    }
                }
                SessionStatus.CANCELLED -> Text("This session was cancelled.")
            }
        }
    }
}
