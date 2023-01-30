package com.nyp.sit.aws.project.onlyplants.Model

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.nyp.sit.aws.project.onlyplants.MainActivity
import com.nyp.sit.aws.project.onlyplants.R
import com.nyp.sit.aws.project.onlyplants.ReminderFormActivity

class myFirebaseMessagingService: FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Retrieve and extract message
        if (message.notification != null) {
            val data = message.data
            val title = data["title"]
            val msg = data["message"]

            sendNotification(title, msg)
        }
    }

    private fun sendNotification(title: String?, msg: String?) {
        // Intent to open watered plant page (note)
        val intent = Intent(this, ReminderFormActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        val channelID = "notification_channel"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(msg)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(channelID,
            "Channel human readable title",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(0, notificationBuilder.build())
    }
}