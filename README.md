# Sample background location
This is sample application demonstrates how to implement the background location which introduced in Android 10 (API level 29).

## Step
1. Add location permission and foreground service permission in AndroidManifest.xml file.
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_BACKGROUND_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

2. Create the 'LocationService' as a service and put the code for requesting location.
```
class LocationService : Service() {
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
            }
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
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

    @SuppressLint("MissingPermission")
    private fun startLocation() {
        if (LocationPermissionHelper.hasLocationPermission(this)) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun stopLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
```

3. Create notification and use 'startForeground(...)' to make the 'LocationService' to be foreground service.
```
class LocationService : Service() {
...
    companion object {
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "channel_id"
        private const val CHANNEL_NAME = "Channel Name"
    }

    override fun onCreate() {
        super.onCreate()
        displayForegroundNotification()
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
...
```

4. Broadcast the location data by using 'LocalBroadcastManager'.
```
class LocationService : Service() {
    companion object {
        ...
        const val ACTION_LOCATION = "action_location"
        const val ARG_LOCATION = "arg_location"
    }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationAvailability(p0: LocationAvailability?) {
            }

            override fun onLocationResult(p0: LocationResult?) {
                p0?.lastLocation?.let {
                    val intent = Intent(ACTION_LOCATION)
                    intent.putExtra(ARG_LOCATION, it)
                    LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
                }
            }
        }
    }
```

5. In the 'LocationActivity', create broadcast receiver to receive the location which broadcast from the 'LocationService'.
```
class LocationActivity : AppCompatActivity() {
...
    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationService.ARG_LOCATION)
            location?.let {
                // Do something
            }
        }
    }
...
```

6. Create the 'LocationBackgroundConsentActivity' to provide an in-app disclosure of your background location access due to new policy of Google Play as below.

[![](https://yt-embed.herokuapp.com/embed?v=b0I1Xq_iSK4)](https://www.youtube.com/watch?v=b0I1Xq_iSK4)

7. In the activity, start 'LocationService' when location permission are granted but open 'LocationBackgroundConsentActivity' when location are not granted.
```
class LocationActivity : AppCompatActivity() {
...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initializeMap()

        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            val intent = Intent(this, LocationBackgroundConsentActivity::class.java)
            startActivityForResult(intent, PERMISSION_REQUEST_CODE)
        } else {
            startService()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    private fun startService() {
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
        LocalBroadcastManager
            .getInstance(this)
            .registerReceiver(broadcastReceiver, IntentFilter(LocationService.ACTION_LOCATION))
    }

    private fun stopService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
        LocalBroadcastManager
             .getInstance(this)
             .unregisterReceiver(broadcastReceiver)
    }
...
```