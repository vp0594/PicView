package com.example.picview

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.picview.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayoutMediator
import kotlin.system.exitProcess


@Suppress("NAME_SHADOWING")
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle

    companion object {
        private const val PERMISSION_REQUEST_EXTERNAL_STORAGE = 1
        private const val PERMISSION_REQUEST_MEDIA_IMAGES = 2
        private const val PERMISSION_REQUEST_MEDIA_VIDEO = 3
        var hasPermissionImages = false
        var hasPermissionVideos = false
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = binding.drawerLayout
        actionBarDrawerToggle =
            ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        checkAppPermissions()

        setUpTabLayout()

        setUpNavItemClick()

    }

    @SuppressLint("CutPasteId")
    private fun setUpNavItemClick() {
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.gridColumn -> {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.setting_layout)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val sharedPreferences = getSharedPreferences("sharePref", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    val numberOfColumnEditText = dialog.findViewById<EditText>(R.id.columnCount)
                    val yesButton = dialog.findViewById<Button>(R.id.yesColumn)
                    val noButton = dialog.findViewById<Button>(R.id.noColumn)

                    yesButton.setOnClickListener {
                        val numberOfColumn: Int = numberOfColumnEditText.text.toString().toInt()
                        editor.apply {
                            putInt("Column", numberOfColumn)
                        }.apply()
                        dialog.dismiss()
                    }

                    noButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()

                }

                R.id.gridColumnAlbums -> {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.setting_layout)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val sharedPreferences = getSharedPreferences("sharePref", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    val numberOfColumnAlbumsEditText =
                        dialog.findViewById<EditText>(R.id.columnCount)
                    val yesButton = dialog.findViewById<Button>(R.id.yesColumn)
                    val noButton = dialog.findViewById<Button>(R.id.noColumn)

                    numberOfColumnAlbumsEditText.hint = "Recommend: 2 to 4"

                    yesButton.setOnClickListener {
                        val numberOfColumnAlbums: Int =
                            numberOfColumnAlbumsEditText.text.toString().toInt()
                        editor.apply {
                            putInt("ColumnAlbums", numberOfColumnAlbums)
                        }.apply()
                        dialog.dismiss()
                    }

                    noButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()

                }

                R.id.shuffle -> startActivity(Intent(this@MainActivity, ShuffleImages::class.java))

                R.id.slideShow -> {
                    val dialog = Dialog(this)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setCancelable(false)
                    dialog.setContentView(R.layout.setting_layout)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    val sharedPreferences = getSharedPreferences("sharePref", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    val slideshowEditText = dialog.findViewById<EditText>(R.id.columnCount)
                    val yesButton = dialog.findViewById<Button>(R.id.yesColumn)
                    val noButton = dialog.findViewById<Button>(R.id.noColumn)

                    slideshowEditText.hint = "Enter Time in seconds"

                    yesButton.setOnClickListener {
                        val slideshowTimer: Float = slideshowEditText.text.toString().toFloat()
                        editor.apply {
                            putFloat("slideshowTime", slideshowTimer)

                        }.apply()
                        dialog.dismiss()
                    }

                    noButton.setOnClickListener {
                        dialog.dismiss()
                    }
                    dialog.show()

                }

                R.id.about -> startActivity(Intent(this@MainActivity, About::class.java))
                R.id.exit -> {
                    val dialog = MaterialAlertDialogBuilder(this)
                    dialog.setTitle("Exit!").setCancelable(false).setMessage("Exit App??")
                        .setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                            val activity = MainActivity()
                            activity.finish()
                            exitProcess(0)
                        }.setNegativeButton("No") { dialog, _: Int ->
                            dialog.dismiss()
                        }
                    dialog.show()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else super.onOptionsItemSelected(item)
    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun checkAppPermissions() {

        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_MEDIA_VIDEO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
        }

        if (permissionsToRequest.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this, permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_EXTERNAL_STORAGE
            )
        } else {
            hasPermissionImages = true
            hasPermissionVideos = true
            setUpTabLayout()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            PERMISSION_REQUEST_EXTERNAL_STORAGE, PERMISSION_REQUEST_MEDIA_IMAGES, PERMISSION_REQUEST_MEDIA_VIDEO -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    hasPermissionImages = true
                    hasPermissionVideos = true
                    setUpTabLayout()
                } else {
                    Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(applicationContext, RequestPermission::class.java))
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
