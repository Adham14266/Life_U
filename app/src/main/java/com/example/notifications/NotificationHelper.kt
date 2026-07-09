package com.example.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R

object NotificationHelper {

    const val CHANNEL_STUDY_REMINDERS = "study_reminders"
    const val CHANNEL_TASK_DEADLINES = "task_deadlines"
    const val CHANNEL_POMODORO = "pomodoro_timer"
    const val CHANNEL_CHAT_BOT = "chat_bot"
    const val CHANNEL_WELLNESS = "wellness_check"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(NotificationManager::class.java) ?: return

            val channels = listOf(
                NotificationChannel(
                    CHANNEL_STUDY_REMINDERS,
                    "Study Reminders",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Reminders to study and review materials"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_TASK_DEADLINES,
                    "Task Deadlines",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "Alerts for upcoming task deadlines"
                    enableVibration(true)
                },
                NotificationChannel(
                    CHANNEL_POMODORO,
                    "Pomodoro Timer",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Pomodoro session start/end notifications"
                },
                NotificationChannel(
                    CHANNEL_CHAT_BOT,
                    "U AI",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Messages from U AI assistant"
                },
                NotificationChannel(
                    CHANNEL_WELLNESS,
                    "Wellness Check-ins",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Mental health and wellness reminders"
                    enableVibration(true)
                }
            )

            channels.forEach { manager.createNotificationChannel(it) }
        }
    }

    fun showNotification(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        notificationId: Int = System.currentTimeMillis().toInt()
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (_: SecurityException) {
            // Permission not granted
        }
    }

    fun scheduleNotification(
        context: Context,
        channelId: String,
        title: String,
        body: String,
        triggerAtMillis: Long,
        requestCode: Int
    ) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra("channel_id", channelId)
            putExtra("title", title)
            putExtra("body", body)
            putExtra("notification_id", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (_: SecurityException) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
        }
    }

    fun cancelScheduledNotification(context: Context, requestCode: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, requestCode, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleWellnessCheckIn(context: Context) {
        val triggerTime = System.currentTimeMillis() + 4 * 60 * 60 * 1000L // 4 hours
        scheduleNotification(
            context = context,
            channelId = CHANNEL_WELLNESS,
            title = "How are you feeling? 💙",
            body = "Take a moment to check in with yourself. U is here if you need to talk!",
            triggerAtMillis = triggerTime,
            requestCode = 9999
        )
    }

    fun scheduleStudyReminder(context: Context, subject: String, triggerAtMillis: Long) {
        scheduleNotification(
            context = context,
            channelId = CHANNEL_STUDY_REMINDERS,
            title = "Time to study! 📚",
            body = "Don't forget to review $subject. Open U for a quick quiz!",
            triggerAtMillis = triggerAtMillis,
            requestCode = subject.hashCode()
        )
    }
}

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val channelId = intent.getStringExtra("channel_id") ?: NotificationHelper.CHANNEL_STUDY_REMINDERS
        val title = intent.getStringExtra("title") ?: "Life U"
        val body = intent.getStringExtra("body") ?: "You have a reminder!"
        val notificationId = intent.getIntExtra("notification_id", System.currentTimeMillis().toInt())

        NotificationHelper.showNotification(context, channelId, title, body, notificationId)
    }
}
