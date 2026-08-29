package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewHowItWorksDialog
import com.byteutility.dev.leetcode.plus.ui.screens.interview.InterviewSessionPhase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.formatCountdown
import com.byteutility.dev.leetcode.plus.ui.screens.interview.phase
import com.byteutility.dev.leetcode.plus.ui.screens.interview.rememberNowTicker
import com.byteutility.dev.leetcode.plus.ui.screens.interview.statusChipColors
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSessionListScreen(
    onBack: () -> Unit = {},
    onBookNew: () -> Unit = {},
    onOpenSession: (String) -> Unit = {},
    onSignedOut: () -> Unit = {},
    viewModel: InterviewSessionListViewModel = hiltViewModel()
) {
    val listItems by viewModel.items.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val now = rememberNowTicker()
    var accountMenuExpanded by remember { mutableStateOf(false) }
    var showHowItWorks by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mock Interviews") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showHowItWorks = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "How mock interviews work")
                    }
                    Box {
                        IconButton(onClick = { accountMenuExpanded = true }) {
                            val photoUrl = currentUser?.photoUrl?.toString()
                            if (photoUrl != null) {
                                AsyncImage(
                                    model = photoUrl,
                                    contentDescription = "Account",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(Icons.Filled.AccountCircle, contentDescription = "Account")
                            }
                        }
                        DropdownMenu(
                            expanded = accountMenuExpanded,
                            onDismissRequest = { accountMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = "Signed in as ${currentUser?.email.orEmpty()}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                },
                                onClick = {},
                                enabled = false
                            )
                            HorizontalDivider()
                            DropdownMenuItem(
                                text = { Text("Sign out") },
                                onClick = {
                                    accountMenuExpanded = false
                                    viewModel.signOut()
                                    onSignedOut()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onBookNew,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Book a new mock interview")
            }
        }
    ) { paddingValues ->
        if (listItems.isEmpty()) {
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
                items(listItems, key = { itemKey(it) }) { item ->
                    when (item) {
                        is InterviewListItem.Matched -> SessionRow(
                            session = item.session,
                            now = now,
                            onClick = { onOpenSession(item.session.sessionId) }
                        )
                        is InterviewListItem.Pending -> PendingBookingRow(booking = item.booking, now = now)
                    }
                }
            }
        }
    }

    if (showHowItWorks) {
        InterviewHowItWorksDialog(onDismiss = { showHowItWorks = false })
    }
}

private fun itemKey(item: InterviewListItem): String = when (item) {
    is InterviewListItem.Matched -> "session_${item.session.sessionId}"
    is InterviewListItem.Pending -> "booking_${item.booking.slotId}_${item.booking.role.firestoreValue}"
}

@Composable
private fun SessionRow(session: InterviewSession, now: Long, onClick: () -> Unit) {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    val phase = session.phase(now)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = session.role.displayName, style = MaterialTheme.typography.titleMedium)
                AssistChip(
                    onClick = {},
                    label = { Text(phase.label) },
                    colors = statusChipColors(phase)
                )
            }
            Text(text = formatter.format(Date(session.startEpochMillis)))
            if (phase == InterviewSessionPhase.JOINABLE) {
                Text(
                    text = "Join available now - starts in ${formatCountdown(session.startEpochMillis - now)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary
                )
            }
        }
    }
}

@Composable
private fun PendingBookingRow(booking: SlotBooking, now: Long) {
    val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    val phase = booking.phase(now)
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = booking.role.displayName, style = MaterialTheme.typography.titleMedium)
                AssistChip(
                    onClick = {},
                    label = { Text(phase.label) },
                    colors = statusChipColors(phase)
                )
            }
            Text(text = formatter.format(Date(booking.startEpochMillis)))
            Text(
                text = if (phase == InterviewSessionPhase.EXPIRED) {
                    "No peer found"
                } else {
                    "Waiting for a peer..."
                }
            )
        }
    }
}
