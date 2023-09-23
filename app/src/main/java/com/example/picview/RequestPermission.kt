package com.example.picview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
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
            startActivity(
                Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:$packageName")
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        checkHasPermission()
        if (MainActivity.hasPermission) {
            startActivity(Intent(this@RequestPermission, MainActivity::class.java))
        }
    }

    private fun checkHasPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU)
            MainActivity.hasPermission = ActivityCompat.checkSelfPermission(
                this@RequestPermission,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        else
            MainActivity.hasPermission = ActivityCompat.checkSelfPermission(
                this@RequestPermission,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
    }

}