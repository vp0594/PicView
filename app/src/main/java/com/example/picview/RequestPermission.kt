package com.example.picview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.picview.databinding.ActivityRequestPermissionBinding

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
            startActivity(Intent(this@RequestPermission, MainActivity::class.java))
            finish()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkHasPermission() {
        MainActivity.hasPermissionImages =
            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || hasPermission(Manifest.permission.READ_MEDIA_IMAGES)

        MainActivity.hasPermissionVideos =
            hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE) || hasPermission(Manifest.permission.READ_MEDIA_VIDEO)
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            this@RequestPermission, permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}
