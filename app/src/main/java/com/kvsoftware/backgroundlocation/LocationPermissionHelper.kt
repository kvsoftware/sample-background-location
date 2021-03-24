package com.kvsoftware.backgroundlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

object LocationPermissionHelper {
    private const val LOCATION_COARSE_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    private const val LOCATION_FINE_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

    @RequiresApi(Build.VERSION_CODES.Q)
    private const val LOCATION_BACKGROUND_PERMISSION =
        Manifest.permission.ACCESS_BACKGROUND_LOCATION


    fun hasLocationPermission(context: Context): Boolean {
        var hasPermissions = true
        if (!hasLocationCoarsePermission(context)) {
            hasPermissions = false
        }
        if (!hasLocationFinePermission(context)) {
            hasPermissions = false
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            && !hasLocationBackgroundPermission(context)
        ) {
            hasPermissions = false
        }
        return hasPermissions
    }

    private fun hasLocationCoarsePermission(context: Context): Boolean =
        (ContextCompat.checkSelfPermission(
            context,
            LOCATION_COARSE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

    private fun hasLocationFinePermission(context: Context): Boolean =
        (ContextCompat.checkSelfPermission(
            context,
            LOCATION_FINE_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun hasLocationBackgroundPermission(context: Context): Boolean =
        (ContextCompat.checkSelfPermission(
            context,
            LOCATION_BACKGROUND_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED)

}