import { onDocumentCreated } from 'firebase-functions/v2/firestore';
import { getFirestore } from 'firebase-admin/firestore';
import { logger } from 'firebase-functions/v2';
import { InterviewSession, SlotBooking } from '../types';
import { decideMatch } from './matchDecision';

/**
 * Fires whenever a user books a slot. Re-reads sibling candidates inside a transaction
 * (not before it) so two near-simultaneous bookings can't both observe "no peer yet" and
 * both stay waiting - that race is what created double-booking bugs in the design. The
 * actual matching decision is delegated to the pure, unit-tested `decideMatch` function.
 */
export const onSlotBookingCreated = onDocumentCreated(
  'slotBookings/{slotDocId}/candidates/{uid}',
  async (event) => {
    const snapshot = event.data;
    if (!snapshot) {
      return;
    }

    const { slotDocId } = event.params;
    const db = getFirestore();
    const candidatesRef = db.collection('slotBookings').doc(slotDocId).collection('candidates');
    const newCandidateRef = snapshot.ref;

    await db.runTransaction(async (tx) => {
      const candidatesSnap = await tx.get(candidatesRef);
      const candidates = candidatesSnap.docs.map((doc) => ({
        id: doc.id,
        data: doc.data() as SlotBooking,
      }));

      const decision = decideMatch(candidates, newCandidateRef.id);
      if (!decision) {
        return;
      }

      const newCandidate = candidates.find((c) => c.id === decision.newCandidateId)?.data;
      const peer = candidates.find((c) => c.id === decision.peerId)?.data;
      if (!newCandidate || !peer) {
        return;
      }

      tx.update(newCandidateRef, { status: 'matched' });
      tx.update(candidatesRef.doc(decision.peerId), { status: 'matched' });

      const sessionRef = db.collection('sessions').doc();
      const session: InterviewSession = {
        sessionId: sessionRef.id,
        uidA: peer.uid,
        uidB: newCandidate.uid,
        participantUids: [peer.uid, newCandidate.uid],
        role: newCandidate.role,
        startEpochMillis: newCandidate.startEpochMillis,
        endEpochMillis: newCandidate.endEpochMillis,
        status: 'pending_calendar',
      };
      tx.set(sessionRef, session);

      logger.info(`Matched ${peer.uid} with ${newCandidate.uid} for slot ${slotDocId}`, {
        sessionId: sessionRef.id,
      });
    });
  }
);
