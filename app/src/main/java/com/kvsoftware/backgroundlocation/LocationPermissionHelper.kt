package com.kvsoftware.backgroundlocation

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object LocationPermissionHelper {
    private const val LOCATION_PERMISSION_CODE = 1000
    private const val LOCATION_COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    private const val LOCATION_FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

    @RequiresApi(Build.VERSION_CODES.Q)
    private const val LOCATION_BACKGROUND_PERMISSION =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION


    fun hasLocationPermission(activity: Activity): Boolean {
        var hasPermissions = true
        if (!hasLocationCoarsePermission(activity)) {
            hasPermissions = false
        }
        if (!hasLocationFinePermission(activity)) {
            hasPermissions = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !hasLocationBackgroundPermission(activity)
        ) {
            hasPermissions = false
        }
        return hasPermissions
    }

    private fun hasLocationCoarsePermission(activity: Activity): Boolean =
        (ContextCompat.checkSelfPermission(
            activity,
            LOCATION_COARSE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

    private fun hasLocationFinePermission(activity: Activity): Boolean =
        (ContextCompat.checkSelfPermission(
            activity,
            LOCATION_FINE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasLocationBackgroundPermission(activity: Activity): Boolean =
        (ContextCompat.checkSelfPermission(
            activity,
            LOCATION_BACKGROUND_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

    fun requestLocationPermission(activity: Activity) {
        val permissions = arrayListOf<String>().apply {
            if (!hasLocationCoarsePermission(activity)) {
                add(LOCATION_COARSE_PERMISSION)
            }
            if (!hasLocationFinePermission(activity)) {
                add(LOCATION_FINE_PERMISSION)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && !hasLocationBackgroundPermission(activity)
            ) {
                add(LOCATION_BACKGROUND_PERMISSION)
            }
        }
        if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                activity,
                permissions.toTypedArray(),
                LOCATION_PERMISSION_CODE
            )
        }
    }

}