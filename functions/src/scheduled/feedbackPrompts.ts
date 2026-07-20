import { onSchedule } from 'firebase-functions/v2/scheduler';
import { getFirestore } from 'firebase-admin/firestore';
import { InterviewSession } from '../types';
import { sendFcm } from '../notify/sendFcm';

/** Runs every 15 minutes; nudges both peers to submit feedback once a session has ended. */
export const feedbackPrompts = onSchedule('every 15 minutes', async () => {
  const db = getFirestore();
  const now = Date.now();

  const snapshot = await db
    .collection('sessions')
    .where('status', '==', 'confirmed')
    .where('endEpochMillis', '<', now)
    .get();

  await Promise.all(
    snapshot.docs.map(async (doc) => {
      const session = doc.data() as InterviewSession;
      if (session.feedbackPromptSentAt) {
        return;
      }

      const feedbackMessage = {
        type: 'feedback_prompt' as const,
        sessionId: session.sessionId,
        title: 'How did it go?',
        body: 'Rate your mock interview peer.',
      };
      await Promise.all([
        sendFcm(session.uidA, feedbackMessage),
        sendFcm(session.uidB, feedbackMessage),
      ]);

      await doc.ref.update({
        status: 'completed',
        feedbackPromptSentAt: now,
      });
    })
  );
});
