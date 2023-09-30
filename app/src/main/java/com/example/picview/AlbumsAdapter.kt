package com.example.picview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.picview.databinding.AlbumsRawBinding

class AlbumsAdapter(
    private val context: Context,
    private val albumsData: ArrayList<AlbumData>,
    private val albumClickListener: AlbumClickListener
) : RecyclerView.Adapter<AlbumsAdapter.ViewHolder>() {
    class ViewHolder(binding: AlbumsRawBinding) : RecyclerView.ViewHolder(binding.root) {
        val albumsCover = binding.albumsCoverImageView
        val albumsName = binding.albumsNameTextView
    }

    interface AlbumClickListener {
        fun onAlbumClick(folderName: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(AlbumsRawBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return albumsData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(albumsData[position].coverImage).into(holder.albumsCover)
        holder.albumsName.text = albumsData[position].folderName

        holder.itemView.setOnClickListener {
            albumClickListener.onAlbumClick(albumsData[position].folderName)
        }
    }

}