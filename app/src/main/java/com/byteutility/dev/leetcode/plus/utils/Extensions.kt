package com.byteutility.dev.leetcode.plus.utils

import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSubmissionVo
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun String.getDateFromTimestamp(): String {
    val date = Date(this.toLong() * 1000)
    val dateFormat = SimpleDateFormat("MMMM dd, yyyy HH:mm:ss a", Locale.getDefault())
    return dateFormat.format(date)
}

fun UserSubmissionVo.toInternalModel() =
    this.submissionVo.map {
        it.toInternalModel()
    }


fun SubmissionVo.toInternalModel() =
    UserSubmission(
        title = this.title,
        lang = this.lang,
        statusDisplay = this.statusDisplay,
        timestamp = this.timestamp.getDateFromTimestamp(),
    )
