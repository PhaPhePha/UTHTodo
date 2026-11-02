package com.uthtodo.app.data.reminder

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.content.ContextCompat
import com.uthtodo.app.MainActivity
import com.uthtodo.app.domain.model.TodoTask

object ReminderScheduler {
    private const val CHANNEL_ID = "todo_reminders"
    private const val EXTRA_TASK_ID = "task_id"
    private const val EXTRA_TITLE = "task_title"

    fun schedule(context: Context, task: TodoTask) {
        val dueAt = task.dueAt ?: return
        if (task.completed || dueAt <= System.currentTimeMillis()) return
        createChannel(context)
        val intent = Intent(context, ReminderReceiver::class.java)
            .putExtra(EXTRA_TASK_ID, task.id)
            .putExtra(EXTRA_TITLE, task.title)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(task.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager?.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, dueAt, pendingIntent)
    }

    fun cancel(context: Context, task: TodoTask) {
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode(task.id),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        pendingIntent?.let {
            context.getSystemService(AlarmManager::class.java)?.cancel(it)
            it.cancel()
        }
    }

    fun showNotification(context: Context, title: String, taskId: String) {
        createChannel(context)
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TASK_ID, taskId)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            requestCode(taskId),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.Notification.Builder(context, CHANNEL_ID)
                .setSmallIcon(com.uthtodo.app.R.drawable.ic_uth_todo_app)
                .setContentTitle("Nhắc việc UTH Todo")
                .setContentText(title)
                .setAutoCancel(true)
                .setContentIntent(openPendingIntent)
                .build()
        } else {
            @Suppress("DEPRECATION")
            android.app.Notification.Builder(context)
                .setSmallIcon(com.uthtodo.app.R.drawable.ic_uth_todo_app)
                .setContentTitle("Nhắc việc UTH Todo")
                .setContentText(title)
                .setAutoCancel(true)
                .setContentIntent(openPendingIntent)
                .build()
        }
        ContextCompat.getSystemService(context, NotificationManager::class.java)
            ?.notify(requestCode(taskId), notification)
    }

    private fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Nhắc việc", NotificationManager.IMPORTANCE_HIGH).apply {
                description = "Thông báo cho các nhiệm vụ sắp đến hạn"
            }
            context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
    }

    private fun requestCode(id: String): Int = id.hashCode() and 0x7fffffff
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        ReminderScheduler.showNotification(
            context = context,
            title = intent.getStringExtra("task_title") ?: "Bạn có một nhiệm vụ sắp đến hạn.",
            taskId = intent.getStringExtra("task_id").orEmpty()
        )
    }
}
