package com.kvsoftware.backgroundlocation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*

class LocationService : Service() {

    companion object {
        private const val NOTIFICATION_ID = 123
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "Channel Name"
    }

    private val locationRequest: LocationRequest by lazy {
        LocationRequest.create().also {
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.interval = 10 * 1000; // 10 seconds
            it.fastestInterval = 5 * 1000; // 5 seconds
        }
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability?) {
            }

            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.let { }
            }
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onCreate() {
        super.onCreate()
        displayForegroundNotification()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocation()
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocation()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? = null

    private fun startLocation() {
        Log.i("Kv", "startLocation")
    }

    private fun stopLocation() {
        Log.i("Kv", "stopLocation")
    }

    private fun displayForegroundNotification() {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
        builder.setContentTitle(getString(R.string.app_name))
        builder.setContentText(getString(R.string.notification_update_location))
        builder.setSmallIcon(R.drawable.ic_launcher_foreground)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
            startForeground(NOTIFICATION_ID, builder.build())
        } else {
            startForeground(NOTIFICATION_ID, builder.build())
        }
    }

}