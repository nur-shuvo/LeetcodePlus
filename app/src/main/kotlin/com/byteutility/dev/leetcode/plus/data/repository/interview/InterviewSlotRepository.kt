package com.byteutility.dev.leetcode.plus.data.repository.interview

import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewRole
import com.byteutility.dev.leetcode.plus.data.model.interview.InterviewSlot

interface InterviewSlotRepository {

    fun getAvailableSlots(role: InterviewRole): List<InterviewSlot>

    suspend fun bookSlot(slot: InterviewSlot)
}
