package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import java.time.YearMonth
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterviewSlotPickerScreen(
    onBooked: () -> Unit = {},
    viewModel: InterviewSlotPickerViewModel = hiltViewModel()
) {
    val selectedRole by viewModel.selectedRole.collectAsStateWithLifecycle()
    val slotsByDate by viewModel.slotsByDate.collectAsStateWithLifecycle()
    val selectedDate by viewModel.selectedDate.collectAsStateWithLifecycle()
    val visibleMonth by viewModel.visibleMonth.collectAsStateWithLifecycle()
    val windowEndDate by viewModel.windowEndDate.collectAsStateWithLifecycle()
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

            InterviewCalendarGrid(
                visibleMonth = visibleMonth,
                selectedDate = selectedDate,
                datesWithSlots = slotsByDate.keys,
                canGoToPreviousMonth = visibleMonth.isAfter(YearMonth.now()),
                canGoToNextMonth = visibleMonth.isBefore(YearMonth.from(windowEndDate)),
                onPreviousMonth = { viewModel.navigateMonth(-1) },
                onNextMonth = { viewModel.navigateMonth(1) },
                onSelectDate = { viewModel.selectDate(it) },
            )

            val slotsForSelectedDate = selectedDate?.let { slotsByDate[it] }.orEmpty()
            if (slotsForSelectedDate.isEmpty()) {
                Text(
                    text = "You've booked all available ${selectedRole.displayName} slots.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    slotsForSelectedDate.forEach { slot ->
                        TimeSlotRow(
                            slot = slot,
                            isBooking = bookingState is BookingUiState.Booking,
                            onBook = { viewModel.bookSlot(slot) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeSlotRow(slot: InterviewSlot, isBooking: Boolean, onBook: () -> Unit) {
    val formatter = remember { DateFormat.getTimeInstance(DateFormat.SHORT) }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isBooking, onClick = onBook)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = formatter.format(Date(slot.startEpochMillis)))
        }
    }
}
