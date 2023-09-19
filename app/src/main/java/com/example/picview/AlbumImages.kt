package com.example.picview

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.ActivityAlbumImagesBinding

class AlbumImages : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumImagesBinding
    private lateinit var dataBase: FavouritesDataBase
    private var from: String = ""
    private lateinit var imageList: ArrayList<ImageData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBase = FavouritesDataBase(this)


        if (intent.getStringExtra("FolderName") == "Fav") {
            imageList = dataBase.getFavouritesImageList()
            from = "Fav"
            binding.albumsTextView.text = "Favourites"

        } else {
            imageList = AlbumsFragment.imageList
            from = "Albums"
            binding.albumsTextView.text = intent.getStringExtra("FolderName")
        }

        setRecyclerview()
    }

    override fun onResume() {
        super.onResume()
        if (intent.getStringExtra("FolderName") == "Fav") {
            imageList = dataBase.getFavouritesImageList()
        }
        setRecyclerview()
    }

    private fun setRecyclerview() {
        val allPhotoAdapter =
            AllPhotoAdapter(applicationContext, imageList, from)
        binding.albumsImagesRecyclerView.layoutManager = GridLayoutManager(applicationContext, 4)
        binding.albumsImagesRecyclerView.adapter = allPhotoAdapter
    }
}