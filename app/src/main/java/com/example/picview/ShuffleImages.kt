package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.ActivityShuffleImagesBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class ShuffleImages : AppCompatActivity() {

    private lateinit var binding: ActivityShuffleImagesBinding
    private lateinit var context: Context
    private lateinit var shuffleMediaAdapter: AllMediaAdapter
    private lateinit var shuffleMediaList: ArrayList<MediaData>

    companion object {
        var mediaList = ArrayList<MediaData>()
        var isFromFolderSelection = false

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shuffle_images)

        binding = ActivityShuffleImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        setUpRecyclerView()

        binding.swipeRefresh.setOnRefreshListener {
            setUpRecyclerView()
        }

        binding.editFolder.setOnClickListener {
            startActivity(Intent(context, FolderSelection::class.java))
        }

    }

    override fun onResume() {
        super.onResume()

        if (isFromFolderSelection) {
            setUpRecyclerView()
            isFromFolderSelection = false
        }

    }

    private fun setUpRecyclerView() {
        shuffleMediaList = getShuffledMediaList()
        binding.shufflePhotoRecyclerView.setHasFixedSize(true)
        binding.shufflePhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        shuffleMediaAdapter = AllMediaAdapter(context, shuffleMediaList, "ShuffledPhotos")
        binding.shufflePhotoRecyclerView.adapter = shuffleMediaAdapter
        mediaList = shuffleMediaList
    }

    private fun getShuffledMediaList(): ArrayList<MediaData> {
        val sharedPreferences = getSharedPreferences("FolderPreferences", Context.MODE_PRIVATE)
        val selectedFolders =
            sharedPreferences.getStringSet("SelectedFolders", emptySet()) ?: emptySet()

        val mediaList = ArrayList<MediaData>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
        val queryUri = MediaStore.Files.getContentUri("external")

        val cursor =
            contentResolver.query(queryUri, projection, selection, selectionArgs, sortOrder)

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val path = cursor.getString(dataColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val mediaType = cursor.getInt(mediaTypeColumn)
                val folderPath = File(path).parent

                if (folderPath in selectedFolders) {
                    val mediaUri = ContentUris.withAppendedId(queryUri, id)
                    val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO
                    val formattedDate =
                        dateFormat.format(if (dateTaken != 0L) dateTaken else File(path).lastModified())

                    mediaList.add(MediaData(mediaUri, formattedDate, isVideo))
                }
            }
        }

        mediaList.shuffle()
        return mediaList
    }


    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}