package com.example.picview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenImageBinding

class FullScreenImage : AppCompatActivity(), FullScreenImageAdapter.ShareButtonClickListener {

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

        fullScreenImageAdapter = FullScreenImageAdapter(applicationContext, allPhotoList, this)
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

    override fun onShareButtonClick(imageUri: Uri) {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        shareIntent.type = "image/*"
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
        startActivity(Intent.createChooser(shareIntent, "Share Image"))
    }
}