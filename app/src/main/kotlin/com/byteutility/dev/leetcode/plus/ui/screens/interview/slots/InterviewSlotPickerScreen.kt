package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSlotPickerScreen(
    onBooked: () -> Unit = {},
    viewModel: InterviewSlotPickerViewModel = hiltViewModel()
) {
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
    val slots by viewModel.slots.collectAsStateWithLifecycle()
    val bookingState by viewModel.bookingState.collectAsStateWithLifecycle()

    LaunchedEffect(bookingState) {
        if (bookingState is BookingUiState.Booked) {
            onBooked()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Book a Mock Interview") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(InterviewRole.entries) { role ->
                    FilterChip(
                        selected = selectedRole == role,
                        onClick = { viewModel.selectRole(role) },
                        label = { Text(role.displayName) }
                    )
                }
            }

            val bookingError = bookingState as? BookingUiState.Error
            if (bookingError != null) {
                Text(text = bookingError.message, color = MaterialTheme.colorScheme.error)
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(slots, key = { it.slotId }) { slot ->
                    SlotRow(
                        slot = slot,
                        isBooking = bookingState is BookingUiState.Booking,
                        onBook = { viewModel.bookSlot(slot) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SlotRow(slot: InterviewSlot, isBooking: Boolean, onBook: () -> Unit) {
    val formatter = remember(slot) {
        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
    }
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatter.format(Date(slot.startEpochMillis)))
            TextButton(onClick = onBook, enabled = !isBooking) {
                Text("Book")
            }
        }
    }
}
