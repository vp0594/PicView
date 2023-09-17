package com.example.picview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.picview.databinding.ActivityFullScreenImageBinding

class FullScreenImage : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var fullScreenImageAdapter: FullScreenImageAdapter

    companion object {
        lateinit var fullImageList: ArrayList<ImageData>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fullImageList = AllPhotoFragment.imageList

        fullScreenImageAdapter = FullScreenImageAdapter(applicationContext, fullImageList)
        binding.fullScreenViewPager.adapter = fullScreenImageAdapter
        binding.fullScreenViewPager.setCurrentItem(intent.getIntExtra("CurrentPosition",1),false)

    }
}