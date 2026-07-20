package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

sealed interface BookingUiState {
    data object Idle : BookingUiState
    data object Booking : BookingUiState
    data object Booked : BookingUiState
    data object Error : BookingUiState
}

@HiltViewModel
class InterviewSlotPickerViewModel @Inject constructor(
    private val interviewSlotRepository: InterviewSlotRepository,
) : ViewModel() {

    private val _selectedRole = MutableStateFlow(InterviewRole.ANDROID)
    val selectedRole: StateFlow<InterviewRole> = _selectedRole.asStateFlow()

    val slots: StateFlow<List<InterviewSlot>> = _selectedRole
        .map { role -> interviewSlotRepository.getAvailableSlots(role) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyList())

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    fun selectRole(role: InterviewRole) {
        _selectedRole.value = role
    }

    fun bookSlot(slot: InterviewSlot) {
        viewModelScope.launch {
            _bookingState.value = BookingUiState.Booking
            runCatching { interviewSlotRepository.bookSlot(slot) }
                .onSuccess { _bookingState.value = BookingUiState.Booked }
                .onFailure { _bookingState.value = BookingUiState.Error }
        }
    }
}
