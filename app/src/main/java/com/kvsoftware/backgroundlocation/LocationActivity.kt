package com.kvsoftware.backgroundlocation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val PERMISSION_REQUEST_CODE = 1001
        const val PERMISSION_RESULT_CODE_ALLOWED = 2001
        const val PERMISSION_RESULT_CODE_DENIED = 2002
        const val ZOOM_LEVEL = 15f
    }

    private var googleMap: GoogleMap? = null
    private var myLocationMarker: Marker? = null

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

    override fun onMapReady(p0: GoogleMap?) {
        googleMap = p0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            when (resultCode) {
                PERMISSION_RESULT_CODE_ALLOWED -> startService()
                PERMISSION_RESULT_CODE_DENIED -> finish()
            }
        }
    }

    private fun initializeMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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

    private fun updateMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        myLocationMarker?.remove()
        myLocationMarker = null
        myLocationMarker = googleMap?.addMarker(MarkerOptions().position(latLng))

        val camera = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL)
        googleMap?.animateCamera(camera)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(LocationService.ARG_LOCATION)
            location?.let {
                updateMarker(it)
            }
        }
    }

}