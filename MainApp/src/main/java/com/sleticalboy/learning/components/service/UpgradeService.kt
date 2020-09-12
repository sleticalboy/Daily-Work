package com.sleticalboy.learning.components.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

/**
 * Created on 20-9-12.
 *
 * @author binlee sleticalboy@gmail.com
 */
class UpgradeService : Service() {

    override fun onCreate() {
        Log.d(TAG, "onCreate() called")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            mgr.createNotificationChannel(NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT))
            startForeground(1, Notification.Builder(this, CHANNEL_ID).build())
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        Log.d(TAG, "onBind() called with: intent = $intent")
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val mac = intent?.getStringExtra("_mac")
        val fileUrl = intent?.getStringExtra("_file_url")
        Log.d(TAG, "onStartCommand() intent = $intent, mac = $mac, file url = $fileUrl")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d(TAG, "onDestroy() called")
    }

    companion object {
        private const val CHANNEL_ID = "Upgrade channel"
        private const val TAG = "UpgradeService"
    }
}