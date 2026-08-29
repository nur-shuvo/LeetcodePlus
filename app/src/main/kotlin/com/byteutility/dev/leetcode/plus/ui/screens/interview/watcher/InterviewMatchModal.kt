package com.byteutility.dev.leetcode.plus.ui.screens.interview.watcher

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import java.text.DateFormat
import java.util.Date

/**
 * Hosted once at the app root (alongside the nav graph, not inside it) so it can pop up
 * regardless of which screen the user is currently on. Only actively watches for matches while
 * the app is at least STARTED (visible/foreground) - see [InterviewMatchWatcherViewModel].
 */
@Composable
fun InterviewMatchModal(
    onViewSession: (String) -> Unit,
    viewModel: InterviewMatchWatcherViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.observeMatches()
        }
    }

    val session by viewModel.newlyMatchedSession.collectAsStateWithLifecycle()
    val peer by viewModel.newlyMatchedPeer.collectAsStateWithLifecycle()
    val currentSession = session ?: return

    AlertDialog(
        onDismissRequest = { viewModel.dismissMatchModal() },
        title = { Text("You're matched!") },
        text = {
            Column {
                Text("${currentSession.role.displayName} mock interview")
                Text(
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
                        .format(Date(currentSession.startEpochMillis))
                )
                peer?.let { peerProfile ->
                    Text("With ${peerProfile.displayName}")
                    if (peerProfile.leetcodeHandle.isNotEmpty()) {
                        Text("LeetCode: ${peerProfile.leetcodeHandle}")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onViewSession(currentSession.sessionId)
                viewModel.dismissMatchModal()
            }) {
                Text("View")
            }
        },
        dismissButton = {
            TextButton(onClick = { viewModel.dismissMatchModal() }) {
                Text("Later")
            }
        }
    )
}
