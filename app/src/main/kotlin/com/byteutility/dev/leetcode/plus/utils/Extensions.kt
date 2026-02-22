package com.byteutility.dev.leetcode.plus.utils

import android.content.Intent
import com.byteutility.dev.leetcode.plus.data.model.LeetCodeProblem
import com.byteutility.dev.leetcode.plus.data.model.UserBasicInfo
import com.byteutility.dev.leetcode.plus.data.model.UserContestInfo
import com.byteutility.dev.leetcode.plus.data.model.UserProblemSolvedInfo
import com.byteutility.dev.leetcode.plus.data.model.UserSubmission
import com.byteutility.dev.leetcode.plus.network.responseVo.DailyQuestionResponse
import com.byteutility.dev.leetcode.plus.network.responseVo.SubmissionVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserContestVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserProfileVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSolvedVo
import com.byteutility.dev.leetcode.plus.network.responseVo.UserSubmissionVo
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("MagicNumber")
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
        titleSlug = this.titleSlug
    )

fun UserProfileVo.toInternalModel() =
    UserBasicInfo(
        name = this.name,
        userName = this.username,
        avatar = this.avatar,
        ranking = this.ranking,
        country = this.country.toString()
    )

fun UserContestVo.toInternalModel() =
    UserContestInfo(
        rating = this.contestRating,
        globalRanking = this.contestGlobalRanking,
        attend = this.contestAttend
    )

fun UserSolvedVo.toInternalModel() =
    UserProblemSolvedInfo(
        easy = this.easySolved,
        medium = this.mediumSolved,
        hard = this.hardSolved,
        totalSolved = this.solvedProblem,
    )

fun DailyQuestionResponse.toInternalModel() =
    LeetCodeProblem(
        title = this.questionTitle,
        difficulty = this.difficulty,
        tag = this.topicTags.firstOrNull()?.name ?: "NO_TAG",
        titleSlug = this.titleSlug
    )

inline fun <reified T> Intent.putExtraJson(key: String, value: T) {
    val jsonString = Json.encodeToString(value)
    this.putExtra(key, jsonString)
}

inline fun <reified T> Intent.getJsonExtra(key: String): T? {
    val jsonString = this.getStringExtra(key)
    return if (jsonString != null) {
        try {
            Json.decodeFromString<T>(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    } else {
        null
    }
}

fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
}

