import { onSchedule } from 'firebase-functions/v2/scheduler';
import { getFirestore } from 'firebase-admin/firestore';
import { InterviewSession } from '../types';
import { sendFcm } from '../notify/sendFcm';

const REMINDER_WINDOW_MINUTES = 10;

/** Runs every 5 minutes; sends a one-time reminder to sessions starting within 10 minutes. */
export const sessionReminders = onSchedule('every 5 minutes', async () => {
  const db = getFirestore();
  const now = Date.now();
  const windowEnd = now + REMINDER_WINDOW_MINUTES * 60 * 1000;

  const snapshot = await db
    .collection('sessions')
    .where('status', '==', 'confirmed')
    .where('startEpochMillis', '>=', now)
    .where('startEpochMillis', '<=', windowEnd)
    .get();

  await Promise.all(
    snapshot.docs.map(async (doc) => {
      const session = doc.data() as InterviewSession;
      if (session.reminderSentAt) {
        return;
      }

      const reminderMessage = {
        type: 'reminder' as const,
        sessionId: session.sessionId,
        title: 'Mock interview starting soon',
        body: `Your ${session.role} interview starts in ${REMINDER_WINDOW_MINUTES} minutes.`,
      };
      await Promise.all([
        sendFcm(session.uidA, reminderMessage),
        sendFcm(session.uidB, reminderMessage),
      ]);

      await doc.ref.update({ reminderSentAt: now });
    })
  );
});
