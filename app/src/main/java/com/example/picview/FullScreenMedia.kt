package com.example.picview

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenMediaBinding
import kotlin.system.exitProcess

class FullScreenMedia : AppCompatActivity(),
    FullScreenMediaAdapter.SideShowButtonClickListener, FullScreenMediaAdapter.VideoActionListener {

    private lateinit var binding: ActivityFullScreenMediaBinding
    private lateinit var fullScreenMediaAdapter: FullScreenMediaAdapter
    private lateinit var dataBase: FavouritesDataBase
    private var currentPosition = 0

    private val slideshowHandler = Handler()
    private var mediaList = ArrayList<MediaData>()

    companion object {
        var slideShow = false
        var external = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        val contentUri = if (intent.data?.scheme.contentEquals("content")) {
            external = true
            intent.data!!
        } else {
            Uri.parse("")
        }

        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        dataBase = FavouritesDataBase(this)

        mediaList = if (external) {
            mediaList.add(MediaData(contentUri, "", false))
            mediaList
        } else if (intent.getStringExtra("from") == "AllPhotos") {
            AllMediaFragment.mediaList
        } else if (intent.getStringExtra("from") == "Albums") {
            AlbumsFragment.mediaList
        } else {
            dataBase.getFavouritesImageList()
        }

        fullScreenMediaAdapter =
            FullScreenMediaAdapter(applicationContext, mediaList, this, this)
        binding.fullScreenViewPager.adapter = fullScreenMediaAdapter
        currentPosition = intent.getIntExtra("CurrentPosition", 0)

        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                TopActionFragment.binding.dateTextView.text =
                    mediaList[position].dateTake

                checkVideo(position)
                checkImageInFavorites(position)
                VideoActionFragment.binding.playPauseButton.setImageResource(R.drawable.ic_video)
                currentPosition = position
                if(mediaList[currentPosition].isVideo){
                    fullScreenMediaAdapter.notifyItemChanged(currentPosition)
                }
                //setMediaController(FullScreenImageAdapter.ViewHolder)

//                binding.fullScreenViewPager.adapter = fullScreenImageAdapter
//                binding.fullScreenViewPager.setCurrentItem(currentPosition, false)
            }
        })


    }


    private fun checkVideo(position: Int) {
        if (mediaList[position].isVideo) {
            showVideoAction()
        } else {
            hideVideoAction()
        }
    }

    override fun onStart() {
        super.onStart()
        checkImageInFavorites(currentPosition)
        checkVideo(currentPosition)
        if (!external) {
            BottomActionFragment.binding.favoritesButton.setOnClickListener {
                if (dataBase.ifImageExits(mediaList[binding.fullScreenViewPager.currentItem].mediaUri.toString())) {
                    dataBase.removeFavourites(mediaList[binding.fullScreenViewPager.currentItem].mediaUri.toString())
                    BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    dataBase.addFavourites(mediaList[binding.fullScreenViewPager.currentItem])
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
                mediaList[binding.fullScreenViewPager.currentItem].mediaUri
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

                if (currentImagePosition >= mediaList.size) {
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

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        if (external) {
            exitProcess(1)
        }
    }

    fun checkImageInFavorites(position: Int) {
        if (dataBase.ifImageExits(mediaList[position].mediaUri.toString())) {
            BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_filled)
        } else {
            BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_border)
        }
    }

    override fun hideVideoAction() {
        VideoActionFragment.binding.root.visibility = View.GONE
    }

    override fun showVideoAction() {
        VideoActionFragment.binding.root.visibility = View.VISIBLE
    }

    override fun playPauseVideo(holder: FullScreenMediaAdapter.ViewHolder) {
        if (holder.video.isPlaying) {
            holder.image.visibility = View.VISIBLE
            holder.video.visibility = View.GONE

            VideoActionFragment.binding.playPauseButton.setImageResource(R.drawable.ic_video)
            holder.video.stopPlayback()

            BottomActionFragment.binding.root.visibility = View.VISIBLE
            TopActionFragment.binding.root.visibility = View.VISIBLE

            Toast.makeText(applicationContext, "pause", Toast.LENGTH_SHORT).show()
        } else {

            holder.image.visibility = View.GONE
            holder.video.visibility = View.VISIBLE

            BottomActionFragment.binding.root.visibility = View.GONE
            TopActionFragment.binding.root.visibility = View.GONE

            holder.video.setVideoURI(mediaList[currentPosition].mediaUri)
            VideoActionFragment.binding.playPauseButton.setImageResource(R.drawable.ic_pause)

            holder.video.start()
        }
    }

    override fun setMediaController(holder: FullScreenMediaAdapter.ViewHolder) {
        val mediaController: MediaController = MediaController(this)
        mediaController.setAnchorView(holder.video)
        holder.video.setMediaController(mediaController)
    }

}