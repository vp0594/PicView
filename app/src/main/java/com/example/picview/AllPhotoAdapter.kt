package com.example.picview

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.ItemRawBinding


class AllPhotoAdapter(
    private val context: Context,
    private val imageList: ArrayList<ImageData>,
    private val from: String
) : RecyclerView.Adapter<AllPhotoAdapter.ViewHolder>() {

    class ViewHolder(binding: ItemRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val image = binding.allPhotoImageView
        val videoIcon = binding.itemVideo
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemRawBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (imageList[position].isVideo)
            holder.videoIcon.visibility = View.VISIBLE
        Glide.with(context).load(imageList[position].mediaUri).into(holder.image)

//        holder.itemView.setOnLongClickListener {
//            //set all checkbox visibility for all images
//            holder.checkBox.visibility = View.VISIBLE
//            true
//        }

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FullScreenImage::class.java)
            Toast.makeText(context,imageList[position].dateTake.toString() + imageList[position].isVideo.toString(), Toast.LENGTH_SHORT).show()
            intent.putExtra("CurrentPosition", position)
            intent.putExtra("from", from)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}

