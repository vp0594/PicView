package com.example.picview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.ItemRawBinding


class AllMediaAdapter(
    private val context: Context,
    private val mediaList: ArrayList<MediaData>,
    private val from: String
) : RecyclerView.Adapter<AllMediaAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.allPhotoImageView
        val videoIcon = binding.itemVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRawBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (mediaList[position].isVideo) {
            holder.videoIcon.visibility = View.VISIBLE
            Glide.with(context).load(mediaList[position].mediaUri).into(holder.image)
        } else {
            holder.videoIcon.visibility = View.GONE
            Glide.with(context).load(mediaList[position].mediaUri).into(holder.image)
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullScreenMedia::class.java)
            intent.putExtra("CurrentPosition", position)
            intent.putExtra("from", from)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

