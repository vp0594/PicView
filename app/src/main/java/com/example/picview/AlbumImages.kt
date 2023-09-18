package com.example.picview

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.ActivityAlbumImagesBinding

class AlbumImages : AppCompatActivity() {

    private lateinit var binding: ActivityAlbumImagesBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAlbumImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val allPhotoAdapter = AllPhotoAdapter(applicationContext, AlbumsFragment.imageList)
        binding.albumsImagesRecylerView.layoutManager = GridLayoutManager(applicationContext, 4)
        binding.albumsImagesRecylerView.adapter = allPhotoAdapter
    }
}