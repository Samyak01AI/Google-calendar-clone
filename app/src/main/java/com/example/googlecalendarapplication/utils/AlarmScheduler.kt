package com.example.googlecalendarapplication.utils
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.googlecalendarapplication.Event
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun scheduleAlarm(event: Event) {
        // Convert LocalDateTime to Milliseconds
        val triggerTime = event.startTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        // Create the Intent
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("EVENT_TITLE", event.title)
            putExtra("EVENT_ID", event.id)
        }

        // Use the Event ID as the unique request code so we can cancel it later
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Schedule it
        try {
            // "setExactAndAllowWhileIdle" ensures it rings even in Doze mode
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
            Log.d("AlarmScheduler", "Scheduled for $triggerTime")
        } catch (e: SecurityException) {
            Log.e("AlarmScheduler", "Permission missing for exact alarm")
        }
    }

    fun cancelAlarm(event: Event) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            event.id.toInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}