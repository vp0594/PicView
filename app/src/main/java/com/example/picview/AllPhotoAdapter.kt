package com.example.picview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.picview.databinding.AllImageRawBinding

class AllPhotoAdapter(private val context: Context, private val imageList: ArrayList<ImageData>) :
    RecyclerView.Adapter<AllPhotoAdapter.ViewHolder>() {
    class ViewHolder(binding: AllImageRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.allPhotoImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(AllImageRawBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(imageList[position].imageUri).into(holder.image)
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullScreenImage::class.java)
            intent.putExtra("CurrentPosition", position)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}