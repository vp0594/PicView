package com.example.picview

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.MediaSliderBinding

class FullScreenMediaAdapter(
    private val context: Context,
    private val mediaList: ArrayList<MediaData>,
    private val sideShowClickListener: SideShowButtonClickListener,
    private val videoActionListener: VideoActionListener,
    private val activity: Activity
) : RecyclerView.Adapter<FullScreenMediaAdapter.ViewHolder>() {
    class ViewHolder(binding: MediaSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.sliderImageView
        val video = binding.sliderVideoView
    }

    interface SideShowButtonClickListener {
        fun onSideShowButtonClick(position: Int)
        fun offSideShowButtonClick()
    }

    interface VideoActionListener {
        fun hideVideoAction()
        fun showVideoAction()
        fun playPauseVideo(holder: ViewHolder)
        fun setMediaController(holder: ViewHolder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(MediaSliderBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        holder.image.visibility = View.VISIBLE
        Glide.with(context).load(mediaList[position].mediaUri).into(holder.image)

        TopActionFragment.binding.dateTextView.text = mediaList[position].dateTake

        VideoActionFragment.binding.playPauseButton.setOnClickListener {
            val intent = Intent(context, VideoPlayer::class.java)
            intent.putExtra("VideoUri", mediaList[position].mediaUri.toString())
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
        holder.image.setOnClickListener {

            if (FullScreenMedia.slideShow) {
                sideShowClickListener.offSideShowButtonClick()
                FullScreenMedia.slideShow = false
                BottomActionFragment.binding.root.visibility = View.VISIBLE
                TopActionFragment.binding.root.visibility = View.VISIBLE
                return@setOnClickListener
            }

            if (BottomActionFragment.binding.root.visibility == View.VISIBLE) {
                BottomActionFragment.binding.root.visibility = View.GONE
                TopActionFragment.binding.root.visibility = View.GONE
            } else {
                BottomActionFragment.binding.root.visibility = View.VISIBLE
                TopActionFragment.binding.root.visibility = View.VISIBLE
            }
        }

        BottomActionFragment.binding.sideShowButton.setOnClickListener {
            FullScreenMedia.slideShow = true
            BottomActionFragment.binding.root.visibility = View.GONE
            TopActionFragment.binding.root.visibility = View.GONE
            sideShowClickListener.onSideShowButtonClick(position)
        }

        holder.image.isClickable = false

        val gestureDetector = GestureDetector(context, GestureTap(holder.image))
        holder.image.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)

            false
        }
    }

    class GestureTap(private var view: View) : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapUp(e: MotionEvent): Boolean {
            view.performClick()
            return true
        }
    }
}