package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.FragmentAllPhotoBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

class AllPhotoFragment : Fragment() {

    private var _binding: FragmentAllPhotoBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private lateinit var allPhotoAdapter: AllPhotoAdapter

    companion object {
        lateinit var imageList: ArrayList<ImageData>

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context = activity?.applicationContext!!
        _binding = FragmentAllPhotoBinding.inflate(inflater, container, false)

        imageList = getImageList()

        binding.allPhotoRecyclerView.setHasFixedSize(true)
        binding.allPhotoRecyclerView.layoutManager = GridLayoutManager(context, 4)
        allPhotoAdapter = AllPhotoAdapter(context, imageList, "AllPhotos")
        binding.allPhotoRecyclerView.adapter = allPhotoAdapter

        return binding.root

    }


    private fun getImageList(): ArrayList<ImageData> {
        val tempMediaList = ArrayList<ImageData>()

        //Querying for images and videos
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?) "

        val selectionArgs = arrayOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(), MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString())

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

        if(cursor != null && cursor.moveToFirst()) {

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
            val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_TAKEN)
            val mediaTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MEDIA_TYPE)

            do {

                val id = cursor.getLong(idColumn)
                val path = cursor.getString(pathColumn)
                val dateTaken = cursor.getLong(dateTakenColumn)
                val mediaType = cursor.getString(mediaTypeColumn)

                val mediaUri = ContentUris.withAppendedId(queryUri,id)

                val isVideo = mediaType == MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()

                val formattedDate = if(dateTaken != 0L) {
                    dateFormat.format(dateTaken)
                } else {
                    dateFormat.format(getDateModified(path))
                }
                val mediaItem = ImageData(mediaUri,formattedDate,isVideo)

                tempMediaList.add(mediaItem)

            } while(cursor.moveToNext())
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