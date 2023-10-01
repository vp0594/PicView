package com.example.picview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.picview.databinding.ActivityAboutBinding

class About : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animationZoomIn = AnimationUtils.loadAnimation(this,R.anim.git_hub)
        binding.gitHubLinkButton.animation = animationZoomIn

        binding.gitHubLinkButton.setOnClickListener {
            val openGithub = Intent(Intent.ACTION_VIEW)
            openGithub.data = Uri.parse("https://github.com/vp0594/PicView/")
            startActivity(openGithub)
        }
    }
}