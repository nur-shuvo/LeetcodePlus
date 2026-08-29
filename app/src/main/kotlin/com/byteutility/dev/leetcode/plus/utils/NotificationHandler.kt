package com.byteutility.dev.leetcode.plus.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.MainActivity

@Suppress("MagicNumber")
object NotificationHandler {

    private const val GOAL_CHANNEL_ID = "goal_reminder_channel"
    private const val DAILY_PROBLEM_CHANNEL_ID = "daily_problem_channel"
    private const val CONTEST_REMINDER_CHANNEL_ID = "contest_reminder_channel"
    private const val INTERVIEW_MATCHED_CHANNEL_ID = "interview_matched_channel"
    private const val INTERVIEW_REMINDER_CHANNEL_ID = "interview_reminder_channel"
    private const val INTERVIEW_FEEDBACK_CHANNEL_ID = "interview_feedback_channel"

    @SuppressLint("MissingPermission")
    fun createWeeklyGoalNotification(context: Context, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(
            context,
            "Weekly Goal",
            GOAL_CHANNEL_ID,
            "Notification for your weekly leetcode goal",
            false
        )

        val builder = NotificationCompat.Builder(context, GOAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_playstore)
            .setContentTitle("Finish your goal")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hasPostNotificationsPermission(context)) {
                    notify(1, builder.build())
                }
            } else {
                notify(1, builder.build())
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun createDailyProblemNotification(context: Context, message: String, titleSlug: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("dailyProblemTitleSlug", titleSlug)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createNotificationChannel(
            context,
            "Leetcode Daily",
            DAILY_PROBLEM_CHANNEL_ID,
            "Notification for leetcode daily problem",
            false
        )

        val builder = NotificationCompat.Builder(context, DAILY_PROBLEM_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_playstore)
            .setContentTitle("Leetcode daily")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hasPostNotificationsPermission(context)) {
                    notify(2, builder.build())
                }
            } else {
                notify(2, builder.build())
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun createContestReminderNotification(context: Context, title: String, url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(
            context,
            "Contest Reminder",
            CONTEST_REMINDER_CHANNEL_ID,
            "Notification for upcoming leetcode contests",
            true
        )

        val builder = NotificationCompat.Builder(context, CONTEST_REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.app_icon_playstore)
            .setContentTitle("Contest Reminder")
            .setContentText("Contest '$title' is about to start!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hasPostNotificationsPermission(context)) {
                    notify(3, builder.build())
                }
            } else {
                notify(3, builder.build())
            }
        }
    }

    private data class InterviewNotificationSpec(
        val channelId: String,
        val channelName: String,
        val channelDescription: String,
        val title: String,
        val notificationId: Int,
    )

    @SuppressLint("MissingPermission")
    fun createInterviewMatchedNotification(context: Context, sessionId: String, message: String) {
        showInterviewNotification(
            context,
            sessionId,
            message,
            InterviewNotificationSpec(
                channelId = INTERVIEW_MATCHED_CHANNEL_ID,
                channelName = "Mock Interview Matched",
                channelDescription = "Notification when you're matched for a mock interview",
                title = "You're matched!",
                notificationId = 4,
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun createInterviewReminderNotification(context: Context, sessionId: String, message: String) {
        showInterviewNotification(
            context,
            sessionId,
            message,
            InterviewNotificationSpec(
                channelId = INTERVIEW_REMINDER_CHANNEL_ID,
                channelName = "Mock Interview Reminder",
                channelDescription = "Reminder before your mock interview starts",
                title = "Mock interview starting soon",
                notificationId = 5,
            )
        )
    }

    @SuppressLint("MissingPermission")
    fun createInterviewFeedbackPromptNotification(
        context: Context,
        sessionId: String,
        message: String
    ) {
        showInterviewNotification(
            context,
            sessionId,
            message,
            InterviewNotificationSpec(
                channelId = INTERVIEW_FEEDBACK_CHANNEL_ID,
                channelName = "Mock Interview Feedback",
                channelDescription = "Reminder to leave feedback after a mock interview",
                title = "How did it go?",
                notificationId = 6,
            )
        )
    }

    @SuppressLint("MissingPermission")
    private fun showInterviewNotification(
        context: Context,
        sessionId: String,
        message: String,
        spec: InterviewNotificationSpec,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("interviewSessionId", sessionId)
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            spec.notificationId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        createNotificationChannel(context, spec.channelName, spec.channelId, spec.channelDescription, false)

        val builder = NotificationCompat.Builder(context, spec.channelId)
            .setSmallIcon(R.drawable.app_icon_playstore)
            .setContentTitle(spec.title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (hasPostNotificationsPermission(context)) {
                    notify(spec.notificationId, builder.build())
                }
            } else {
                notify(spec.notificationId, builder.build())
            }
        }
    }

    private fun createNotificationChannel(
        context: Context,
        channelName: String,
        channelID: String,
        descriptionText: String,
        isHighImportance: Boolean = false
    ) {
        val importance =
            if (isHighImportance) NotificationManager.IMPORTANCE_HIGH else NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(channelID, channelName, importance).apply {
            description = descriptionText
        }
        if (isHighImportance) {
            val soundUri =
                Uri.parse("android.resource://" + context.packageName + "/" + R.raw.in_app_contest_notify)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            channel.enableLights(true)
            channel.enableVibration(true)
            channel.setSound(soundUri, audioAttributes)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    @SuppressLint("InlinedApi")
    private fun hasPostNotificationsPermission(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }
}
