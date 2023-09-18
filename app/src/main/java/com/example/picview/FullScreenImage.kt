package com.example.picview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenImageBinding

class FullScreenImage : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var fullScreenImageAdapter: FullScreenImageAdapter
    private var currentPosition = 0
    private var allPhotoList = ArrayList<ImageData>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allPhotoList = if (intent.getStringExtra("from") == "AllPhotos") {
            AllPhotoFragment.imageList
        } else {
            AlbumsFragment.imageList
        }
        fullScreenImageAdapter = FullScreenImageAdapter(applicationContext, allPhotoList)
        binding.fullScreenViewPager.adapter = fullScreenImageAdapter

        val currentPosition = intent.getIntExtra("CurrentPosition", 1)
        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)


        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                TopActionFragment.binding.dateTextView.text =
                    allPhotoList[currentPosition].dateTake
            }
        })

    }

    override fun onStart() {
        super.onStart()
        TopActionFragment.binding.dateTextView.text =
            allPhotoList[currentPosition].dateTake
    }


}