package com.example.picview

import android.content.SharedPreferences
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.ActivityAlbumMediaBinding

class AlbumMedia : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumMediaBinding
    private lateinit var dataBase: FavouritesDataBase
    private var from: String = ""
    private lateinit var mediaList: ArrayList<MediaData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumMediaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBase = FavouritesDataBase(this)


        if (intent.getStringExtra("FolderName") == "UserFav") {
            mediaList = dataBase.getFavouritesMediaList()
            from = "Fav"
            binding.albumsTextView.text = "Favourites"

        } else {
            mediaList = AlbumsFragment.mediaList
            from = "Albums"
            binding.albumsTextView.text = intent.getStringExtra("FolderName")
        }

        setRecyclerview()
    }

    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra("FolderName") == "UserFav") {
            mediaList = dataBase.getFavouritesMediaList()
        }
        setRecyclerview()
    }

    private fun setRecyclerview() {

        val allMediaAdapter = AllMediaAdapter(applicationContext, mediaList, from)
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("sharePref", AppCompatActivity.MODE_PRIVATE)
        val numberOfColumn: Int = sharedPreferences.getInt("Column", 3)
        binding.albumsImagesRecyclerView.layoutManager =
            GridLayoutManager(applicationContext, numberOfColumn)
        binding.albumsImagesRecyclerView.adapter = allMediaAdapter
    }
}