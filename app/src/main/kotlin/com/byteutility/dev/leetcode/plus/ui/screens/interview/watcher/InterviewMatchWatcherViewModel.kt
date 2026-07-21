package com.byteutility.dev.leetcode.plus.ui.screens.interview.watcher

import android.content.Context
import androidx.lifecycle.ViewModel
import com.byteutility.dev.leetcode.plus.data.datastore.NotificationDataStore
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewProfile
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSession
import com.byteutility.dev.leetcode.plus.data.repository.interview.GoogleAuthRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewProfileRepository
import com.byteutility.dev.leetcode.plus.data.repository.interview.InterviewSessionRepository
import com.byteutility.dev.leetcode.plus.utils.NotificationHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Watches the signed-in user's mock-interview sessions in real time (the same live Firestore
 * listener [InterviewSessionRepository.getMySessions] already uses) so a match is noticed the
 * instant Firestore pushes it - no need to wait for the periodic background worker's 15-minute
 * poll. Meant to be observed for as long as the app is in the foreground (via `repeatOnLifecycle`
 * at the call site), regardless of which screen is currently shown.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class InterviewMatchWatcherViewModel @Inject constructor(
    @ApplicationContext private val appContext: Context,
    private val googleAuthRepository: GoogleAuthRepository,
    private val interviewSessionRepository: InterviewSessionRepository,
    private val interviewProfileRepository: InterviewProfileRepository,
    private val notificationDataStore: NotificationDataStore,
) : ViewModel() {

    private val _newlyMatchedSession = MutableStateFlow<InterviewSession?>(null)
    val newlyMatchedSession: StateFlow<InterviewSession?> = _newlyMatchedSession.asStateFlow()

    private val _newlyMatchedPeer = MutableStateFlow<InterviewProfile?>(null)
    val newlyMatchedPeer: StateFlow<InterviewProfile?> = _newlyMatchedPeer.asStateFlow()

    /** Call from a `LaunchedEffect` wrapped in `repeatOnLifecycle(STARTED)` - suspends for as
     * long as the caller keeps collecting, i.e. only while actually observed. */
    suspend fun observeMatches() {
        googleAuthRepository.currentUser
            .flatMapLatest { user ->
                val uid = user?.uid
                if (uid == null) flowOf(null) else interviewSessionRepository.getMySessions().map { uid to it }
            }
            .collect { uidAndSessions ->
                if (uidAndSessions != null) {
                    handleSessions(uidAndSessions.first, uidAndSessions.second)
                }
            }
    }

    private suspend fun handleSessions(uid: String, sessions: List<InterviewSession>) {
        val now = System.currentTimeMillis()
        val unnotified = sessions.filter { !notificationDataStore.hasNotifiedMatched(it.sessionId) }
        unnotified.forEach { session -> notifyMatched(session) }

        val toCelebrate = unnotified.filter { it.endEpochMillis > now }.maxByOrNull { it.startEpochMillis }
        if (toCelebrate != null) {
            _newlyMatchedSession.value = toCelebrate
            _newlyMatchedPeer.value = interviewProfileRepository.getProfile(toCelebrate.peerUid(uid)).first()
        }
    }

    private suspend fun notifyMatched(session: InterviewSession) {
        NotificationHandler.createInterviewMatchedNotification(
            appContext,
            session.sessionId,
            "You're matched for a ${session.role.displayName} mock interview"
        )
        notificationDataStore.markMatchedNotified(session.sessionId)
    }

    fun dismissMatchModal() {
        _newlyMatchedSession.value = null
        _newlyMatchedPeer.value = null
    }
}
