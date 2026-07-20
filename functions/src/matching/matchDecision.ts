import { SlotBooking } from '../types';

export const MAX_CANDIDATES_PER_SLOT = 2;

export interface CandidateDoc {
  id: string;
  data: SlotBooking;
}

export interface MatchDecision {
  peerId: string;
  newCandidateId: string;
}

/**
 * Pure matching logic, extracted from the Firestore trigger so it can be unit tested without
 * mocking Firestore. `candidates` must be every doc currently in the slot's candidates
 * subcollection, read inside the enclosing transaction (not before it) - that's what prevents
 * two near-simultaneous bookings from both seeing "no peer yet" and both staying waiting.
 */
export function decideMatch(
  candidates: CandidateDoc[],
  newCandidateId: string
): MatchDecision | null {
  const newCandidate = candidates.find((c) => c.id === newCandidateId);
  if (!newCandidate || newCandidate.data.status !== 'waiting') {
    return null;
  }

  const waiting = candidates.filter((c) => c.data.status === 'waiting');
  if (waiting.length > MAX_CANDIDATES_PER_SLOT) {
    return null;
  }

  const waitingPeers = waiting.filter((c) => c.id !== newCandidateId);
  if (waitingPeers.length === 0) {
    return null;
  }

  const peer = [...waitingPeers].sort((a, b) => a.data.createdAt - b.data.createdAt)[0];
  return { peerId: peer.id, newCandidateId };
}
