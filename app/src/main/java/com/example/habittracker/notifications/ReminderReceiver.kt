package com.example.habittracker.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.habittracker.R

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val habitName = intent.getStringExtra("habit_name") ?: "Habit"
        val habitId = intent.getLongExtra("habit_id", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "habit_reminders"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminders",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val messages = listOf(
            Pair("SPIKE IS HUNGRY! 🦔🍴", "Feed me: '$habitName'! I need those completion points!"),
            Pair("SPIKE: ACTION TIME! 💥", "BAM! Let's conquer this habit: '$habitName'!"),
            Pair("SPIKE IS WAITING! 👀", "Spike says: 'Don't let my health decay! Complete: $habitName'"),
            Pair("SPIKE WANTS XP! 📈", "Help Spike evolve! Time for your habit: $habitName")
        )
        val (title, text) = messages.random()

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(text)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(habitId.toInt(), notification)
    }
}
