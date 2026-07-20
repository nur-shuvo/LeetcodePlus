package com.byteutility.dev.leetcode.plus.data.model.interview

enum class InterviewRole(val firestoreValue: String, val displayName: String) {
    ANDROID("android", "Android"),
    BACKEND("backend", "Backend"),
    FRONTEND("frontend", "Frontend"),
    ML("ml", "Machine Learning"),
    SYSTEM_DESIGN("system_design", "System Design");

    companion object {
        fun fromFirestoreValue(value: String?): InterviewRole? =
            entries.find { it.firestoreValue == value }
    }
}

enum class BookingStatus(val firestoreValue: String) {
    WAITING("waiting"),
    MATCHED("matched"),
    EXPIRED("expired");

    companion object {
        fun fromFirestoreValue(value: String?): BookingStatus =
            entries.find { it.firestoreValue == value } ?: WAITING
    }
}

enum class SessionStatus(val firestoreValue: String) {
    MATCHED("matched"),
    COMPLETED("completed"),
    CANCELLED("cancelled");

    companion object {
        fun fromFirestoreValue(value: String?): SessionStatus =
            entries.find { it.firestoreValue == value } ?: MATCHED
    }
}

data class InterviewProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val leetcodeHandle: String = "",
    val roles: List<InterviewRole> = emptyList(),
    val timeZoneId: String = "",
)

data class InterviewSlot(
    val slotId: String = "",
    val role: InterviewRole = InterviewRole.ANDROID,
    val startEpochMillis: Long = 0L,
    val endEpochMillis: Long = 0L,
)

data class SlotBooking(
    val uid: String = "",
    val slotId: String = "",
    val role: InterviewRole = InterviewRole.ANDROID,
    val status: BookingStatus = BookingStatus.WAITING,
    val startEpochMillis: Long = 0L,
    val endEpochMillis: Long = 0L,
    val createdAt: Long = 0L,
)

data class InterviewSession(
    val sessionId: String = "",
    val uidA: String = "",
    val uidB: String = "",
    val participantUids: List<String> = emptyList(),
    val role: InterviewRole = InterviewRole.ANDROID,
    val startEpochMillis: Long = 0L,
    val endEpochMillis: Long = 0L,
    val status: SessionStatus = SessionStatus.MATCHED,
) {
    fun peerUid(myUid: String): String = if (uidA == myUid) uidB else uidA

    /**
     * Ad-hoc Jitsi Meet room derived from the session id - both peers compute the same URL
     * client-side, so no Calendar/Meet API integration or Firestore field is needed.
     */
    val meetingUrl: String get() = "https://meet.jit.si/lcplus-$sessionId"
}

data class InterviewFeedback(
    val raterUid: String = "",
    val rateeUid: String = "",
    val communicationRating: Int = 0,
    val problemSolvingRating: Int = 0,
    val wouldMatchAgain: Boolean = false,
    val notes: String = "",
    val didNotShowUp: Boolean = false,
    val submittedAt: Long = 0L,
)
