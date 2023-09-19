package com.example.picview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenImageBinding

class FullScreenImage : AppCompatActivity(), FullScreenImageAdapter.ShareButtonClickListener,
    FullScreenImageAdapter.SideShowButtonClickListener {

    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var fullScreenImageAdapter: FullScreenImageAdapter
    private var currentPosition = 0

    private val slideshowHandler = Handler()
    private var allPhotoList = ArrayList<ImageData>()

    companion object{
        var slideShow = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        allPhotoList = if (intent.getStringExtra("from") == "AllPhotos") {
            AllPhotoFragment.imageList
        } else {
            AlbumsFragment.imageList
        }

        fullScreenImageAdapter =
            FullScreenImageAdapter(applicationContext, allPhotoList, this, this)
        binding.fullScreenViewPager.adapter = fullScreenImageAdapter
        currentPosition = intent.getIntExtra("CurrentPosition", 1)

        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                TopActionFragment.binding.dateTextView.text =
                    allPhotoList[position].dateTake
            }
        })

    }

    private fun stopSlideshow() {
        slideshowHandler.removeCallbacksAndMessages(null)
    }

    private fun startSlideshow() {

        val delayMillis = 3000L

        val runnable = object : Runnable {
            override fun run() {
                currentPosition++

                if (currentPosition >= allPhotoList.size) {
                    currentPosition = 0
                }
                binding.fullScreenViewPager.setCurrentItem(currentPosition, true)
                slideshowHandler.postDelayed(this, delayMillis)
            }
        }
        slideshowHandler.postDelayed(runnable, delayMillis)
    }


    override fun onShareButtonClick(imageUri: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }

    override fun onSideShowButtonClick() {
        startSlideshow()
    }

    override fun offSideShowButtonClick() {
        stopSlideshow()
    }


}