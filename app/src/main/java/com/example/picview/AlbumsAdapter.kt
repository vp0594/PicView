package com.example.picview

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
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
        val albumsLayout = binding.albumsLinearLayout
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
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("sharePref", AppCompatActivity.MODE_PRIVATE)
        val numberOfColumn:Int = sharedPreferences.getInt("ColumnAlbums",2)

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay

        Glide.with(context).load(albumsData[position].coverImage).into(holder.albumsCover)
        holder.albumsName.text = albumsData[position].folderName


        val size= display.width

        holder.albumsCover.minimumHeight=size/numberOfColumn
        holder.albumsCover.minimumWidth=size/numberOfColumn

        holder.itemView.setOnClickListener {
            albumClickListener.onAlbumClick(albumsData[position].folderName)
        }
    }

}