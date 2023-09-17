package com.example.picview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.ActivityFullScreenImageBinding
import com.example.picview.databinding.ImageSliderBinding

class FullScreenImageAdapter(
    private val context: Context,
    private val imageList: ArrayList<ImageData>
) :
    RecyclerView.Adapter<FullScreenImageAdapter.ViewHolder>() {
    class ViewHolder(binding: ImageSliderBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.sliderImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ImageSliderBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imageList[position].imageUri).into(holder.image)
    }
}