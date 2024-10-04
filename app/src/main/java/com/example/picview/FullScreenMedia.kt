package com.example.picview

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.widget.MediaController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.picview.databinding.ActivityFullScreenMediaBinding
import java.io.File
import kotlin.system.exitProcess

class FullScreenMedia : AppCompatActivity(), FullScreenMediaAdapter.SideShowButtonClickListener,
    FullScreenMediaAdapter.VideoActionListener {

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

        val attrib = window.attributes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            attrib.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

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
        } else if (intent.getStringExtra("from") == "ShuffledPhotos") {
            ShuffleImages.mediaList
        } else if (intent.getStringExtra("from") == "Albums") {
            AlbumsFragment.mediaList
        } else {
            dataBase.getFavouritesMediaList()
        }


        fullScreenMediaAdapter =
            FullScreenMediaAdapter(applicationContext, mediaList, this, this, this)
        binding.fullScreenViewPager.adapter = fullScreenMediaAdapter
        currentPosition = intent.getIntExtra("CurrentPosition", 0)

        binding.fullScreenViewPager.setCurrentItem(currentPosition, false)

        binding.fullScreenViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                TopActionFragment.binding.dateTextView.text = mediaList[position].dateTake

                checkVideo(position)
                checkImageInFavorites(position)
                VideoActionFragment.binding.playPauseButton.setImageResource(R.drawable.ic_video)
                currentPosition = position
                if (mediaList[currentPosition].isVideo) {
                    fullScreenMediaAdapter.notifyItemChanged(currentPosition)
                }
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
                if (dataBase.ifMediaExits(mediaList[binding.fullScreenViewPager.currentItem].mediaUri.toString())) {
                    dataBase.removeFavourites(mediaList[binding.fullScreenViewPager.currentItem].mediaUri.toString())
                    BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_border)
                } else {
                    dataBase.addFavourites(mediaList[binding.fullScreenViewPager.currentItem])
                    BottomActionFragment.binding.favoritesButton.setImageResource(R.drawable.ic_favorite_filled)
                }
            }
        }

        BottomActionFragment.binding.copyName.setOnClickListener {
            val currentMedia = mediaList[binding.fullScreenViewPager.currentItem]
            val fileName = getFileNameFromUri(currentMedia.mediaUri)

            if (fileName != null) {
                // Copying to clipboard
                val clipboard =
                    getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
                val clip = android.content.ClipData.newPlainText("Image Name", fileName)
                clipboard.setPrimaryClip(clip)

                // Inform the user
                Toast.makeText(
                    this, "Image name copied to clipboard", Toast.LENGTH_SHORT
                ).show()
            } else {
                Toast.makeText(this, "Failed to retrieve image name", Toast.LENGTH_SHORT).show()
            }
        }

        BottomActionFragment.binding.shareButton.setOnClickListener {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "image/*"
            shareIntent.putExtra(
                Intent.EXTRA_STREAM, mediaList[binding.fullScreenViewPager.currentItem].mediaUri
            )
            startActivity(Intent.createChooser(shareIntent, "Share Image"))
        }


    }

    private fun getFileNameFromUri(uri: Uri): String? {
        var fileName: String? = null

        // Query the content resolver to get the file details
        val projection =
            arrayOf(MediaStore.Images.Media.DISPLAY_NAME)  // DISPLAY_NAME contains the file name
        val cursor = contentResolver.query(uri, projection, null, null, null)

        cursor?.use {
            if (it.moveToFirst()) {
                // Get the file name with extension
                fileName =
                    it.getString(it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))

                // Remove the extension, if any
                fileName = fileName?.substringBeforeLast(".")
            }
        }

        return fileName
    }

    private fun stopSlideshow() {
        slideshowHandler.removeCallbacksAndMessages(null)
    }

    private fun startSlideshow(position: Int) {
        var currentImagePosition = position

        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharePref", AppCompatActivity.MODE_PRIVATE)
        val slideshowTimer: Float = sharedPreferences.getFloat("slideshowTime", 3F)
        val delayMillis = slideshowTimer * 1000

        val runnable = object : Runnable {
            override fun run() {
                currentImagePosition++

                if (currentImagePosition >= mediaList.size) {
                    currentImagePosition = 0
                }
                binding.fullScreenViewPager.setCurrentItem(currentImagePosition, true)
                slideshowHandler.postDelayed(this, delayMillis.toLong())
            }
        }
        slideshowHandler.postDelayed(runnable, delayMillis.toLong())
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
        if (dataBase.ifMediaExits(mediaList[position].mediaUri.toString())) {
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