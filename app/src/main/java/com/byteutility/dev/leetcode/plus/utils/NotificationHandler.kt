package com.byteutility.dev.leetcode.plus.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.byteutility.dev.leetcode.plus.R
import com.byteutility.dev.leetcode.plus.ui.MainActivity

object NotificationHandler {

    private const val GOAL_CHANNEL_ID = "goal_reminder_channel"
    private const val DAILY_PROBLEM_CHANNEL_ID = "daily_problem_channel"

    @SuppressLint("MissingPermission")
    fun createWeeklyGoalNotification(context: Context, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(
            context,
            "Weekly Goal",
            GOAL_CHANNEL_ID,
            "Notification for your weekly leetcode goal"
        )

        val builder = NotificationCompat.Builder(context, GOAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.leetcode_logo_small)
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
    fun createDailyProblemNotification(context: Context, message: String) {
        // Go to the exact problem page instead
        val url = "https://leetcode.com/problems/"
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1,
            browserIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        createNotificationChannel(
            context,
            "Leetcode Daily",
            DAILY_PROBLEM_CHANNEL_ID,
            "Notification for leetcode daily problem"
        )

        val builder = NotificationCompat.Builder(context, DAILY_PROBLEM_CHANNEL_ID)
            .setSmallIcon(R.drawable.leetcode_logo_small)
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

    private fun createNotificationChannel(
        context: Context,
        channelName: String,
        channelID: String,
        descriptionText: String
    ) {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelID, channelName, importance).apply {
            description = descriptionText
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