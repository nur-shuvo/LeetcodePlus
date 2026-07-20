export type InterviewRole = 'android' | 'backend' | 'frontend' | 'ml' | 'system_design';

export interface InterviewProfile {
  uid: string;
  displayName: string;
  email: string;
  leetcodeHandle?: string;
  roles: InterviewRole[];
  timeZoneId: string;
  fcmToken?: string;
  /**
   * OAuth refresh token for the Google Calendar `calendar.events` scope, obtained during
   * Google Sign-In with offline access requested. Only present for users who have granted
   * calendar access; required to act as the organizer for createMeetEvent.
   */
  calendarRefreshToken?: string;
}

export type BookingStatus = 'waiting' | 'matched' | 'expired';

export interface SlotBooking {
  uid: string;
  slotId: string;
  role: InterviewRole;
  status: BookingStatus;
  startEpochMillis: number;
  endEpochMillis: number;
  createdAt: number;
}

export type SessionStatus =
  | 'pending_calendar'
  | 'confirmed'
  | 'calendar_failed'
  | 'completed'
  | 'cancelled';

export interface InterviewSession {
  sessionId: string;
  uidA: string;
  uidB: string;
  participantUids: [string, string];
  role: InterviewRole;
  startEpochMillis: number;
  endEpochMillis: number;
  status: SessionStatus;
  meetLink?: string;
  calendarEventId?: string;
  reminderSentAt?: number;
  feedbackPromptSentAt?: number;
}

export interface InterviewFeedback {
  raterUid: string;
  rateeUid: string;
  communicationRating: number;
  problemSolvingRating: number;
  wouldMatchAgain: boolean;
  notes?: string;
  didNotShowUp: boolean;
  submittedAt: number;
}
