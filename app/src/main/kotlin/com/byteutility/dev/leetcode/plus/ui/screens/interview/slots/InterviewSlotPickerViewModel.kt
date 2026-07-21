package com.byteutility.dev.leetcode.plus.ui.screens.interview.slots

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotCalendar
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.NotSignedInException
import com.byteutility.dev.leetcode.plus.data.repository.interview.TooManyActiveBookingsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

private const val SHARE_STOP_TIMEOUT_MS = 5000L

sealed interface BookingUiState {
    data object Idle : BookingUiState
    data object Booking : BookingUiState
    data object Booked : BookingUiState
    data class Error(val message: String) : BookingUiState
}

@HiltViewModel
class InterviewSlotPickerViewModel @Inject constructor(
    private val interviewSlotRepository: InterviewSlotRepository,
) : ViewModel() {

    private val _selectedRole = MutableStateFlow(InterviewRole.ANDROID)
    val selectedRole: StateFlow<InterviewRole> = _selectedRole.asStateFlow()

    /** Slots for the selected role, minus any the user already has a booking for (waiting or
     * matched) - re-booking the same slot was already a harmless no-op, but showing it as
     * "available" was confusing since tapping Book on it visibly did nothing. */
    val slots: StateFlow<List<InterviewSlot>> = combine(
        _selectedRole.map { role -> interviewSlotRepository.getAvailableSlots(role) },
        interviewSlotRepository.getMyBookings(),
    ) { availableSlots, myBookings ->
        val bookedKeys = myBookings.map { it.slotId to it.role }.toSet()
        availableSlots.filterNot { (it.slotId to it.role) in bookedKeys }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyList())

    /** [slots] grouped by local calendar date - drives which grid days are tappable and
     * supplies the time rows for [selectedDate]. */
    val slotsByDate: StateFlow<Map<LocalDate, List<InterviewSlot>>> = slots
        .map { InterviewSlotCalendar.groupByLocalDate(it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyMap())

    /** The local calendar date of the last slot in the current role's 14-day window,
     * regardless of booking state - used only to clamp [visibleMonth] navigation forward. */
    val windowEndDate: StateFlow<LocalDate> = _selectedRole
        .map { role ->
            InterviewSlotCalendar.groupByLocalDate(interviewSlotRepository.getAvailableSlots(role))
                .keys
                .maxOrNull() ?: LocalDate.now()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), LocalDate.now())

    /** Null unless the user has explicitly tapped a day; falls back to the earliest date in
     * [slotsByDate] otherwise. Cleared back to null by [selectRole]. */
    private val _selectedDateOverride = MutableStateFlow<LocalDate?>(null)

    val selectedDate: StateFlow<LocalDate?> = combine(
        slotsByDate,
        _selectedDateOverride,
    ) { byDate, override ->
        override ?: byDate.keys.minOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    /** Null unless the user has explicitly navigated the month view; follows [selectedDate]'s
     * month otherwise. Cleared back to null by [selectRole] and [selectDate]. */
    private val _visibleMonthOverride = MutableStateFlow<YearMonth?>(null)

    val visibleMonth: StateFlow<YearMonth> = combine(
        selectedDate,
        _visibleMonthOverride,
    ) { selected, override ->
        override ?: selected?.let { YearMonth.from(it) } ?: YearMonth.now()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), YearMonth.now())

    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState: StateFlow<BookingUiState> = _bookingState.asStateFlow()

    fun selectRole(role: InterviewRole) {
        _selectedRole.value = role
        _selectedDateOverride.value = null
        _visibleMonthOverride.value = null
    }

    fun selectDate(date: LocalDate) {
        _selectedDateOverride.value = date
        _visibleMonthOverride.value = null
    }

    fun navigateMonth(monthDelta: Int) {
        val requested = visibleMonth.value.plusMonths(monthDelta.toLong())
        _visibleMonthOverride.value =
            InterviewSlotCalendar.clampMonth(requested, LocalDate.now(), windowEndDate.value)
    }

    fun bookSlot(slot: InterviewSlot) {
        viewModelScope.launch {
            _bookingState.value = BookingUiState.Booking
            runCatching { interviewSlotRepository.bookSlot(slot) }
                .onSuccess { _bookingState.value = BookingUiState.Booked }
                .onFailure { e ->
                    val message = when (e) {
                        is NotSignedInException -> "Sign in with Google first to book a mock interview."
                        is TooManyActiveBookingsException -> e.message.orEmpty()
                        else -> "Couldn't book that slot, please try again."
                    }
                    _bookingState.value = BookingUiState.Error(message)
                }
        }
    }
}
