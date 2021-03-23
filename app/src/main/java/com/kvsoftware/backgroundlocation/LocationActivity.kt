package com.kvsoftware.backgroundlocation

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment

class LocationActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        const val REQUEST_CODE = 1000
        const val RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)
        initializeMap()
        startService()
    }

    override fun onResume() {
        super.onResume()
        if (!LocationPermissionHelper.hasLocationPermission(this)) {
            val intent = Intent(this, LocationBackgroundConsentActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE) {
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService()
    }

    override fun onMapReady(p0: GoogleMap?) {
    }

    private fun initializeMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun startService() {
        val intent = Intent(this, LocationService::class.java)
        startService(intent)
    }

    private fun stopService() {
        val intent = Intent(this, LocationService::class.java)
        stopService(intent)
    }

}