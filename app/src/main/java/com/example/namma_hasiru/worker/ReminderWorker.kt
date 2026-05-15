package com.example.namma_hasiru.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.namma_hasiru.R

class ReminderWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val speciesName = inputData.getString("species_name") ?: "your tree"
        
        sendNotification(speciesName)
        
        return Result.success()
    }

    private fun sendNotification(speciesName: String) {
        val channelId = "tree_reminder_channel"
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Tree Check-up Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Reminders to check on your saplings"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Sustainability Mission: Check-up Time!")
            .setContentText("Your $speciesName needs a status update. Help it survive!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}
