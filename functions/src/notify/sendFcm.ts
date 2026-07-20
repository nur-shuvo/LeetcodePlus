import { getMessaging } from 'firebase-admin/messaging';
import { getFirestore } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions/v2';

interface InterviewPushPayload {
  type: 'matched' | 'reminder' | 'feedback_prompt';
  sessionId: string;
  title: string;
  body: string;
}

export async function sendFcm(uid: string, payload: InterviewPushPayload): Promise<void> {
  const db = getFirestore();
  const profileSnap = await db.collection('interviewProfiles').doc(uid).get();
  const fcmToken = profileSnap.data()?.fcmToken as string | undefined;

  if (!fcmToken) {
    logger.warn(`No FCM token for user ${uid}, skipping push`, { sessionId: payload.sessionId });
    return;
  }

  try {
    await getMessaging().send({
      token: fcmToken,
      notification: {
        title: payload.title,
        body: payload.body,
      },
      data: {
        type: payload.type,
        sessionId: payload.sessionId,
      },
    });
  } catch (error) {
    logger.error(`Failed to send FCM to ${uid}`, error);
  }
}
