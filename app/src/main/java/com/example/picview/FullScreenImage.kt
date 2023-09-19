package com.example.picview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenImageBinding
import kotlin.system.exitProcess

class FullScreenImage : AppCompatActivity(),
    FullScreenImageAdapter.SideShowButtonClickListener {

    private lateinit var binding: ActivityFullScreenImageBinding
    private lateinit var fullScreenImageAdapter: FullScreenImageAdapter
    private lateinit var dataBase: FavouritesDataBase
    private var currentPosition = 0

    private val slideshowHandler = Handler()
    private var allPhotoList = ArrayList<ImageData>()

    companion object {
        var slideShow = false
        var external = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val contentUri = if(intent.data?.scheme.contentEquals("content")) {
            external = true
            intent.data!!
        } else {
            Uri.parse("")
        }

        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenImageBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dataBase = FavouritesDataBase(this)

            allPhotoList = if (external) {
                allPhotoList.add(ImageData(contentUri,""))
                allPhotoList
            }
            else if (intent.getStringExtra("from") == "AllPhotos") {
                AllPhotoFragment.imageList
            } else if (intent.getStringExtra("from") == "Albums") {
                AlbumsFragment.imageList
            } else {
                dataBase.getFavouritesImageList()
            }

        fullScreenImageAdapter =
            FullScreenImageAdapter(applicationContext, allPhotoList, this)
        binding.fullScreenViewPager.adapter = fullScreenImageAdapter
        currentPosition = intent.getIntExtra("CurrentPosition", 0)

        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                TopActionFragment.binding.dateTextView.text =
                    allPhotoList[position].dateTake

                checkImageInFavorites(position)
            }
        })

    }

    override fun onStart() {
        super.onStart()
        checkImageInFavorites(currentPosition)
        if(!external) {
            BottomActionFragment.binding.favoritesButton.setOnClickListener {
                if (dataBase.ifImageExits(allPhotoList[binding.fullScreenViewPager.currentItem].imageUri.toString())) {
                    dataBase.removeFavourites(allPhotoList[binding.fullScreenViewPager.currentItem].imageUri.toString())
                    BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    dataBase.addFavourites(allPhotoList[binding.fullScreenViewPager.currentItem])
                    BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_filled)
                }
            }
        }

        BottomActionFragment.binding.shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "image/*"
            shareIntent.putExtra(
                Intent.EXTRA_STREAM,
                allPhotoList[binding.fullScreenViewPager.currentItem].imageUri
            )
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }
    }

    private fun stopSlideshow() {
        slideshowHandler.removeCallbacksAndMessages(null)
    }

    private fun startSlideshow(position: Int) {
        var currentImagePosition = position
        val delayMillis = 2000L

        val runnable = object : Runnable {
            override fun run() {
                currentImagePosition++

                if (currentImagePosition >= allPhotoList.size) {
                    currentImagePosition = 0
                }
                binding.fullScreenViewPager.setCurrentItem(currentImagePosition, true)
                slideshowHandler.postDelayed(this, delayMillis)
            }
        }
        slideshowHandler.postDelayed(runnable, delayMillis)
    }

    override fun onSideShowButtonClick(position: Int) {
        startSlideshow(position)
    }

    override fun offSideShowButtonClick() {
        stopSlideshow()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if(external) {
            exitProcess(1)
        }
    }

    fun checkImageInFavorites(position: Int) {
        if (dataBase.ifImageExits(allPhotoList[position].imageUri.toString())) {
            BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }
}