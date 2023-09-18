package com.example.picview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
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

        binding.fullScreenViewPager.adapter = fullScreenImageAdapter
        val currentPosition = intent.getIntExtra("CurrentPosition", 1)
        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                BottomActionFragment.binding.shareButton.setOnClickListener {
                    Toast.makeText(applicationContext, "yes", Toast.LENGTH_SHORT).show()
                }
                //   TopActionFragment.binding.dateTextView.text = fullImageList[position].dateTake
            }
        })


    }


}