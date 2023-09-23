package com.example.picview

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.picview.databinding.ActivityMainBinding
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        var hasPermission = false

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        checkAppHasPermission()

        setContentView(binding.root)

    }

    private fun checkAppHasPermission() {
        hasPermission =
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU)
                ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            else
                ActivityCompat.checkSelfPermission(
                    this@MainActivity,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED

        if (!hasPermission)
            updateOrRequestPermission()
        else
            setUpTabLayout()
    }


    private fun updateOrRequestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                1
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                2
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requestCode == 2) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true
                    setUpTabLayout()
                } else {

                    startActivity(Intent(this@MainActivity, RequestPermission::class.java))
                    finish()

                }
            }
        } else {
            if (requestCode == 1) {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    hasPermission = true
                } else {

                    startActivity(Intent(this@MainActivity, RequestPermission::class.java))
                    finish()

                }
            }
        }
    }

    private fun setUpTabLayout() {
        binding.homePageViewPage.adapter = TabLayoutAdapter(supportFragmentManager, lifecycle)
        TabLayoutMediator(binding.homePageTabLayout, binding.homePageViewPage) { tab, position ->
            when (position) {
                0 -> tab.text = "All Photos"
                1 -> tab.text = "Albums"
            }
        }.attach()
    }
}
