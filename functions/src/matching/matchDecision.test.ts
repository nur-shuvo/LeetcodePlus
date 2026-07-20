import { decideMatch, CandidateDoc } from './matchDecision';
import { SlotBooking } from '../types';

function booking(overrides: Partial<SlotBooking> = {}): SlotBooking {
  return {
    uid: 'uid',
    slotId: 'slot1',
    role: 'android',
    status: 'waiting',
    startEpochMillis: 0,
    endEpochMillis: 0,
    createdAt: 0,
    ...overrides,
  };
}

function candidate(id: string, overrides: Partial<SlotBooking> = {}): CandidateDoc {
  return { id, data: booking({ uid: id, ...overrides }) };
}

describe('decideMatch', () => {
  it('returns null when the new candidate is the only one waiting', () => {
    const candidates = [candidate('a', { createdAt: 1 })];
    expect(decideMatch(candidates, 'a')).toBeNull();
  });

  it('matches the new candidate with the earliest other waiting candidate', () => {
    const candidates = [candidate('a', { createdAt: 1 }), candidate('b', { createdAt: 2 })];
    expect(decideMatch(candidates, 'b')).toEqual({ peerId: 'a', newCandidateId: 'b' });
  });

  it('prevents double-matching when the new candidate was already matched by a concurrent run', () => {
    const candidates = [
      candidate('a', { createdAt: 1 }),
      candidate('b', { createdAt: 2, status: 'matched' }),
    ];
    expect(decideMatch(candidates, 'b')).toBeNull();
  });

  it('returns null when the slot is already full', () => {
    const candidates = [
      candidate('a', { createdAt: 1 }),
      candidate('b', { createdAt: 2 }),
      candidate('c', { createdAt: 3 }),
    ];
    expect(decideMatch(candidates, 'c')).toBeNull();
  });

  it('returns null when the new candidate document is missing', () => {
    const candidates = [candidate('a', { createdAt: 1 })];
    expect(decideMatch(candidates, 'ghost')).toBeNull();
  });
});
