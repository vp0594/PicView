package com.example.picview

import android.content.ContentUris
import android.content.Context
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
        val mediaList = ArrayList<MediaData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shuffle_images)

        binding = ActivityShuffleImagesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        context = this
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        shuffleMediaList = getShuffledMediaList()
        binding.shufflePhotoRecyclerView.setHasFixedSize(true)
        binding.shufflePhotoRecyclerView.layoutManager = GridLayoutManager(context, 3)
        shuffleMediaAdapter = AllMediaAdapter(context, shuffleMediaList, "ShuffledPhotos")
        binding.shufflePhotoRecyclerView.adapter = shuffleMediaAdapter
    }

    private fun getShuffledMediaList(): ArrayList<MediaData> {


        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?) "

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortBy = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        val cursor = context.contentResolver.query(
            queryUri, projection, selection, selectionArgs, sortBy
        )

        val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

        if (cursor != null && cursor.moveToFirst()) {

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val mediaTypeColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            do {

                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val mediaType = cursor.getString(mediaTypeColumn)

                val mediaUri = ContentUris.withAppendedId(queryUri, id)

                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()

                val formattedDate = if (dateTaken != 0L) {
                    dateFormat.format(dateTaken)
                } else {
                    dateFormat.format(getDateModified(path))
                }

                val mediaItem = MediaData(mediaUri, formattedDate, isVideo)

                mediaList.add(mediaItem)

            } while (cursor.moveToNext())
        }
        cursor?.close()

        mediaList.shuffle()
        return mediaList
    }

    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}