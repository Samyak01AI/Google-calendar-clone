package com.example.googlecalendarapplication.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.googlecalendarapplication.MainActivity
import com.example.googlecalendarapplication.R

class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val eventTitle = intent.getStringExtra("EVENT_TITLE") ?: "Event Reminder"
        val eventId = intent.getLongExtra("EVENT_ID", 0)

        showNotification(context, eventTitle, eventId)
    }

    private fun showNotification(context: Context, title: String, eventId: Long) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "calendar_events_channel"

        // 1. Create Notification Channel (Required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Calendar Events",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for calendar events"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 2. Create Intent to open App when clicked
        val activityIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            eventId.toInt(),
            activityIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 3. Build and Show Notification
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_clock_outline) // Ensure you have this icon
            .setContentTitle(title)
            .setContentText("Happening now!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true) // Removes notification when tapped
            .build()

        notificationManager.notify(eventId.toInt(), notification)
    }
}