package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SessionStatus
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSessionListScreen(
    onBookNew: () -> Unit = {},
    onOpenSession: (String) -> Unit = {},
    viewModel: InterviewSessionListViewModel = hiltViewModel()
) {
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mock Interviews") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onBookNew) {
                Icon(Icons.Filled.Add, contentDescription = "Book a new mock interview")
            }
        }
    ) { paddingValues ->
        if (sessions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No mock interviews yet. Tap + to book one.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sessions, key = { it.sessionId }) { session ->
                    SessionRow(session = session, onClick = { onOpenSession(session.sessionId) })
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: InterviewSession, onClick: () -> Unit) {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = session.role.displayName, style = MaterialTheme.typography.titleMedium)
            Text(text = formatter.format(Date(session.startEpochMillis)))
            Text(text = statusLabel(session.status))
        }
    }
}

private fun statusLabel(status: SessionStatus): String = when (status) {
    SessionStatus.PENDING_CALENDAR -> "Setting up your meeting link..."
    SessionStatus.CONFIRMED -> "Confirmed"
    SessionStatus.CALENDAR_FAILED -> "Couldn't create a meeting link"
    SessionStatus.COMPLETED -> "Completed - leave feedback"
    SessionStatus.CANCELLED -> "Cancelled"
}
