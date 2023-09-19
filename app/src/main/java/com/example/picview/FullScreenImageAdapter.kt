package com.example.picview

import android.annotation.SuppressLint
import android.content.Context
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.ImageSliderBinding

class FullScreenImageAdapter(
    private val context: Context,
    private val imageList: ArrayList<ImageData>,
    private val sideShowClickListener: SideShowButtonClickListener
) :
    RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder>() {
    class ViewHolder(binding: ImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.sliderImageView
    }

    interface SideShowButtonClickListener {
        fun onSideShowButtonClick()
        fun offSideShowButtonClick()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImageSliderBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imageList[position].imageUri).into(holder.image)

        holder.image.setOnClickListener {

            if (FullScreenImage.slideShow) {
                sideShowClickListener.offSideShowButtonClick()
                FullScreenImage.slideShow = false
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
            FullScreenImage.slideShow = true
            BottomActionFragment.binding.root.visibility = View.GONE
            TopActionFragment.binding.root.visibility = View.GONE
            sideShowClickListener.onSideShowButtonClick()
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