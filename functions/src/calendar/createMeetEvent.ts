import { onDocumentCreated } from 'firebase-functions/v2/firestore';
import { getFirestore } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions/v2';
import { defineSecret } from 'firebase-functions/params';
import { google } from 'googleapis';
import { InterviewProfile, InterviewSession } from '../types';
import { sendFcm } from '../notify/sendFcm';

// Requires the sensitive `calendar.events` OAuth scope, which needs Google's app-verification
// review before general availability (see Phase 0 of the implementation plan). Set these via
// `firebase functions:secrets:set` once the OAuth client is created in Google Cloud Console.
const googleOAuthClientId = defineSecret('GOOGLE_OAUTH_CLIENT_ID');
const googleOAuthClientSecret = defineSecret('GOOGLE_OAUTH_CLIENT_SECRET');

async function getCalendarClientForUser(uid: string, clientId: string, clientSecret: string) {
  const db = getFirestore();
  const profileSnap = await db.collection('interviewProfiles').doc(uid).get();
  const profile = profileSnap.data() as InterviewProfile | undefined;
  if (!profile?.calendarRefreshToken) {
    throw new Error(`No calendar refresh token stored for organizer ${uid}`);
  }

  const oAuth2Client = new google.auth.OAuth2(clientId, clientSecret);
  oAuth2Client.setCredentials({ refresh_token: profile.calendarRefreshToken });
  return google.calendar({ version: 'v3', auth: oAuth2Client });
}

/**
 * Fires when the matching function creates a `pending_calendar` session. Whichever peer
 * booked first (uidA) acts as the Calendar event organizer; the other peer is invited by
 * email, which is what causes Google to auto-generate the Meet link (conferenceDataVersion: 1).
 */
export const createMeetEvent = onDocumentCreated(
  {
    document: 'sessions/{sessionId}',
    secrets: [googleOAuthClientId, googleOAuthClientSecret],
  },
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      return;
    }

    const session = snapshot.data() as InterviewSession;
    if (session.status !== 'pending_calendar') {
      return;
    }

    const db = getFirestore();
    const organizerUid = session.uidA;
    const attendeeUid = session.uidB;

    try {
      const attendeeProfileSnap = await db.collection('interviewProfiles').doc(attendeeUid).get();
      const attendeeProfile = attendeeProfileSnap.data() as InterviewProfile | undefined;
      if (!attendeeProfile?.email) {
        throw new Error(`Attendee ${attendeeUid} has no email on file`);
      }

      const calendar = await getCalendarClientForUser(
        organizerUid,
        googleOAuthClientId.value(),
        googleOAuthClientSecret.value()
      );

      const response = await calendar.events.insert({
        calendarId: 'primary',
        conferenceDataVersion: 1,
        requestBody: {
          summary: `LeetcodePlus Mock Interview (${session.role})`,
          description: 'Auto-scheduled peer mock interview via LeetcodePlus.',
          start: { dateTime: new Date(session.startEpochMillis).toISOString() },
          end: { dateTime: new Date(session.endEpochMillis).toISOString() },
          attendees: [{ email: attendeeProfile.email }],
          conferenceData: {
            createRequest: { requestId: session.sessionId },
          },
        },
      });

      await snapshot.ref.update({
        status: 'confirmed',
        meetLink: response.data.hangoutLink ?? null,
        calendarEventId: response.data.id ?? null,
      });

      const matchedMessage = {
        type: 'matched' as const,
        sessionId: session.sessionId,
        title: "You're matched!",
        body: `Your ${session.role} mock interview is confirmed.`,
      };
      await Promise.all([
        sendFcm(organizerUid, matchedMessage),
        sendFcm(attendeeUid, matchedMessage),
      ]);
    } catch (error) {
      logger.error(`Failed to create calendar event for session ${session.sessionId}`, error);
      await snapshot.ref.update({ status: 'calendar_failed' });
    }
  }
);
