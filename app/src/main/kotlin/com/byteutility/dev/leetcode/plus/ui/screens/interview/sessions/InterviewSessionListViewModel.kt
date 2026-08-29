package com.byteutility.dev.leetcode.plus.ui.screens.interview.sessions

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteutility.dev.leetcode.plus.data.model.interview.BookingStatus
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.model.interview.SlotBooking
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSlotRepository
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

private const val TAG = "InterviewSessionListVM"

private const val SHARE_STOP_TIMEOUT_MS = 5000L

sealed interface InterviewListItem {
    val startEpochMillis: Long

    data class Matched(val session: InterviewSession) : InterviewListItem {
        override val startEpochMillis: Long = session.startEpochMillis
    }

    /** A booking still waiting for a peer, or one that timed out with none found. */
    data class Pending(val booking: SlotBooking) : InterviewListItem {
        override val startEpochMillis: Long = booking.startEpochMillis
    }
}

@HiltViewModel
class InterviewSessionListViewModel @Inject constructor(
    interviewSessionRepository: InterviewSessionRepository,
    interviewSlotRepository: InterviewSlotRepository,
    private val googleAuthRepository: GoogleAuthRepository,
) : ViewModel() {

    val items: StateFlow<List<InterviewListItem>> = combine(
        interviewSessionRepository.getMySessions(),
        interviewSlotRepository.getMyBookings(),
    ) { sessions, bookings ->
        val matched = sessions.map { InterviewListItem.Matched(it) }
        val pending = bookings
            .filter { it.status == BookingStatus.WAITING }
            .map { InterviewListItem.Pending(it) }
        (matched + pending).sortedByDescending { it.startEpochMillis }
    }.catch { e ->
        Log.e(TAG, "Failed to load mock interview sessions/bookings", e)
        emit(emptyList())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), emptyList())

    val currentUser: StateFlow<FirebaseUser?> = googleAuthRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(SHARE_STOP_TIMEOUT_MS), null)

    fun signOut() {
        googleAuthRepository.signOut()
    }
}
