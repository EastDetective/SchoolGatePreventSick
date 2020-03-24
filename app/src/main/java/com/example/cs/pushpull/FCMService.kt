package com.example.cs.pushpull

import android.app.Notification.DEFAULT_ALL
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.firebase.messaging.RemoteMessage.PRIORITY_HIGH

class FCMService : FirebaseMessagingService() {

    companion object {
        const val TAG = "FireBase"
    }

    override fun onMessageReceived(message: RemoteMessage?) {

        super.onMessageReceived(message)

        Log.d(TAG, message?.notification?.body.toString())
        Log.d(TAG, message?.data.toString())

        message?.let {

            initChannel(baseContext)

            val foreNotification = NotificationCompat.Builder(this, "default")
                .setContentTitle(it.notification?.title)
                .setContentText(it.notification?.body)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(baseContext, R.color.colorPrimary))
                .setDefaults(DEFAULT_ALL)
                .setPriority(PRIORITY_HIGH)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(0, foreNotification)
        }

//        message?.run {
//            Log.d(TAG, "From: $from")
//
//            if (data.isNotEmpty()) {
//                Log.d(TAG, "Message data payload: $data")
//
////                if (/* Check if data needs to be processed by long running job */ true) {
////                    // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                // TODO : scheduleJob
////                } else {
////                    // Handle message within 10 seconds
//                // TODO : Handle
////                }
//            }
//
//            notification?.run {
//                Log.d(TAG, "Message Notification Body: $body")
//            }
//        } ?: run {
//            Log.d(TAG, "From is null")
//        }

//        super.onMessageReceived(message)
    }

    override fun onNewToken(token: String?) {

        Log.d(TAG, "Refreshed Token: $token")
    }

    private fun initChannel(context: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val notificationManager = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        val channel = NotificationChannel("default", "firebase", NotificationManager.IMPORTANCE_HIGH)

        channel.description = "Channel description"
        notificationManager.createNotificationChannel(channel)
    }
}
