import { onSchedule } from 'firebase-functions/v2/scheduler';
import { getFirestore } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions/v2';

/** Runs every 15 minutes; marks bookings that never got matched before their slot ended. */
export const expireStaleBookings = onSchedule('every 15 minutes', async () => {
  const db = getFirestore();
  const now = Date.now();

  const slotBookingsSnapshot = await db.collection('slotBookings').get();
  let expiredCount = 0;

  await Promise.all(
    slotBookingsSnapshot.docs.map(async (slotDoc) => {
      const staleCandidates = await slotDoc.ref
        .collection('candidates')
        .where('status', '==', 'waiting')
        .where('endEpochMillis', '<', now)
        .get();

      expiredCount += staleCandidates.size;
      await Promise.all(
        staleCandidates.docs.map((candidateDoc) => candidateDoc.ref.update({ status: 'expired' }))
      );
    })
  );

  logger.info(`Expired ${expiredCount} stale booking(s)`);
});
