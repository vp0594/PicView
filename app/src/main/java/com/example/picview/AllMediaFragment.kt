package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.FragmentAllMediaBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AllMediaFragment : Fragment() {

    private var _binding: FragmentAllMediaBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private lateinit var allMediaAdapter: AllMediaAdapter

    companion object {
        lateinit var mediaList: ArrayList<MediaData>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context = activity?.applicationContext!!
        _binding = FragmentAllMediaBinding.inflate(inflater, container, false)




        setUpRecyclerView()
        binding.swipeRefresh.setOnRefreshListener {
            setUpRecyclerView()
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root

    }

    private fun setUpRecyclerView() {
        mediaList = getMediaList()
        binding.allPhotoRecyclerView.setHasFixedSize(true)
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("sharePref", AppCompatActivity.MODE_PRIVATE)
        val numberOfColumn: Int = sharedPreferences.getInt("Column", 3)
        binding.allPhotoRecyclerView.layoutManager = GridLayoutManager(context, numberOfColumn)
        allMediaAdapter = AllMediaAdapter(context, mediaList, "AllPhotos")
        binding.allPhotoRecyclerView.adapter = allMediaAdapter
    }

    private fun getMediaList(): ArrayList<MediaData> {
        val tempMediaList = ArrayList<MediaData>()

        //Querying for images and videos
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
            queryUri,
            projection,
            selection,
            selectionArgs,
            sortBy
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

                tempMediaList.add(mediaItem)

            } while (cursor.moveToNext())
        }
        cursor?.close()

        return tempMediaList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}