package com.kvsoftware.backgroundlocation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class LocationBackgroundConsentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_background_consent)

        findViewById<Button>(R.id.button_allow_permissions).setOnClickListener {
            if (!LocationPermissionHelper.hasLocationPermission(this)) {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (LocationPermissionHelper.hasLocationPermission(this)) {
            finish()
        }
    }

    override fun onBackPressed() {
        setResult(LocationActivity.PERMISSION_RESULT_CODE)
        finish()
    }

}