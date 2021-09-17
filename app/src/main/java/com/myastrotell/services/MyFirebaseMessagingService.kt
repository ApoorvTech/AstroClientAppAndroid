package com.myastrotell.services

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.myastrotell.BuildConfig
import com.myastrotell.R
import com.myastrotell.data.AppConstants
import com.myastrotell.data.DataManager
import com.myastrotell.data.preferences.PreferenceManager
import com.myastrotell.ui.home.HomeActivity
import java.util.concurrent.atomic.AtomicInteger


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val channelId = "1001"
    private val channelName = BuildConfig.NOTIFICATION_CHANNEL



    override fun onNewToken(token: String) {
        super.onNewToken(token)
        println("Firebase token ===> $token  ${Thread.currentThread().name}")
        DataManager.saveStringInPreference(PreferenceManager.FCM_ID, token)
    }


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        println("Firebase Message ===> ${remoteMessage.data}")

        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        if (title != null && message != null) {
            showNotification(title, message, remoteMessage.data)
        }

//        if (!isAppInBackground(this)) {
//            sendNotificationBroadcast(remoteMessage.data)
//        }

    }

    private fun showNotification(title: String, desc: String, data: Map<String, String>) {

        val intent = Intent(this, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        if (data[AppConstants.KEY_TARGET_ACTION].equals("6")){
            intent.putExtra(AppConstants.KEY_TITLE, data[AppConstants.KEY_TITLE])
            intent.putExtra(AppConstants.KEY_MESSAGE, data[AppConstants.KEY_MESSAGE])
            intent.putExtra(AppConstants.KEY_TARGET_ACTION, data[AppConstants.KEY_TARGET_ACTION])
            intent.putExtra(
                AppConstants.KEY_TARGET_ACTION_DATA, data[AppConstants.KEY_TARGET_ACTION_DATA]
            )
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent, PendingIntent.FLAG_ONE_SHOT
        )

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.new_launcher)
            .setContentText(desc)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.color = ContextCompat.getColor(this, R.color.colorAccent)
        }

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            mChannel.description = "All app notifications"
            mChannel.enableLights(true)
            mChannel.lightColor = Color.RED
            mChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mChannel.setShowBadge(true)
            notificationManager.createNotificationChannel(mChannel)
        }

        notificationManager.notify(
            NotificationID.id /* ID of notification */,
            notificationBuilder.build()
        )
    }


    private fun sendNotificationBroadcast(data: Map<String, String>) {
        val intent = Intent(AppConstants.FCM_FILTER_NAME)
        intent.putExtra(AppConstants.KEY_TITLE, data[AppConstants.KEY_TITLE])
        intent.putExtra(AppConstants.KEY_MESSAGE, data[AppConstants.KEY_MESSAGE])
        intent.putExtra(AppConstants.KEY_CLICK_ACTION, data[AppConstants.KEY_CLICK_ACTION])
        intent.putExtra(AppConstants.KEY_TARGET_ACTION, data[AppConstants.KEY_TARGET_ACTION])
        intent.putExtra(
            AppConstants.KEY_TARGET_ACTION_DATA, data[AppConstants.KEY_TARGET_ACTION_DATA]
        )
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    object NotificationID {
        private val c = AtomicInteger(0)
        val id: Int get() = c.incrementAndGet()
    }


    /**
     * @return true if app is in background else false
     */
    private fun isAppInBackground(context: Context): Boolean {
        var isInBackground = true
        val am = context.getSystemService(ACTIVITY_SERVICE) as? ActivityManager
        if (am != null) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                val runningProcesses = am.runningAppProcesses
                for (processInfo in runningProcesses) {
                    if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        for (activeProcess in processInfo.pkgList) {
                            if (activeProcess == context.packageName) {
                                isInBackground = false
                            }
                        }
                    }
                }
            } else {
                val taskInfo = am.getRunningTasks(1)
                val componentInfo = taskInfo[0].topActivity
                if (componentInfo!!.packageName == context.packageName) {
                    isInBackground = false
                }
            }
        }
        return isInBackground
    }

}