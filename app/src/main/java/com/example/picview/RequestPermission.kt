package com.example.picview

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.picview.databinding.ActivityRequestPermissionBinding
import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi

class RequestPermission : AppCompatActivity() {
    private lateinit var binding: ActivityRequestPermissionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRequestPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPermission()
    }

    private fun setPermission() {
        binding.requestPermissionButton.setOnClickListener {
            // Open app settings to allow the user to grant permissions
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            intent.data = Uri.parse("package:$packageName")
            startActivity(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onResume() {
        super.onResume()
        checkHasPermission()

        if (MainActivity.hasPermissionImages && MainActivity.hasPermissionVideos) {
            // All permissions are granted, navigate back to the MainActivity
            startActivity(Intent(this@RequestPermission, MainActivity::class.java))
            finish() // Finish this activity to prevent returning to it
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkHasPermission() {
        // Check for both image and video permissions
        MainActivity.hasPermissionImages =
            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    hasPermission(Manifest.permission.READ_MEDIA_IMAGES)

        MainActivity.hasPermissionVideos =
            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    hasPermission(Manifest.permission.READ_MEDIA_VIDEO)
    }

    private fun hasPermission(permission: String): Boolean {
        // Check if the app has the specified permission
        return ActivityCompat.checkSelfPermission(this@RequestPermission, permission) ==
                PackageManager.PERMISSION_GRANTED
    }
}
