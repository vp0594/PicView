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
        val tempImageList = ArrayList<ImageData>()

        // Query for images
        val imageProjection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED
        )

        val imageSortBy = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val imageCursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            imageProjection,
            null,
            null,
            imageSortBy
        )

        // Query for videos
        val videoProjection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DATE_TAKEN,
            MediaStore.Video.Media.DATA
        )

        val videoSortBy = "${MediaStore.Video.Media.DATE_TAKEN} DESC"
        val videoCursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            videoProjection,
            null,
            null,
            videoSortBy
        )
        var dateModified: Long
        // Process image results
        if (imageCursor != null) {
            if (imageCursor.moveToFirst()) {
                val idColumn = imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
                val dateModifiedColumn =
                    imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_MODIFIED)

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                do {
                    val path =
                        imageCursor.getString(imageCursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
                    val id = imageCursor.getLong(idColumn)
                    val dateTaken = imageCursor.getLong(dateTakenColumn)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    dateModified = imageCursor.getLong(dateModifiedColumn)

                    val formattedDate = if (dateTaken == 0L) {
                        dateFormat.format(getDateModified(path))
                    } else {
                        dateFormat.format(dateTaken)
                    }

                    tempImageList.add(ImageData(imageUri, formattedDate, false))
                } while (imageCursor.moveToNext())
            }
            imageCursor.close()
        }

        // Process video results
        if (videoCursor != null) {
            if (videoCursor.moveToFirst()) {
                val idColumn = videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
                val dateTakenColumn =
                    videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_TAKEN)

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                do {
                    val path =
                        videoCursor.getString(videoCursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA))
                    val id = videoCursor.getLong(idColumn)
                    val dateTaken = videoCursor.getLong(dateTakenColumn)
                    val videoUri =
                        ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)

                    val formattedDate = if (dateTaken == 0L) {
                        dateFormat.format(getDateModified(path))
                    } else {
                        dateFormat.format(dateTaken)
                    }
                    tempImageList.add(ImageData(videoUri, formattedDate, true))
                } while (videoCursor.moveToNext())
            }
            videoCursor.close()
        }

        //Sort the combined list by date
        tempImageList.sortByDescending { it.dateTake }

        return tempImageList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}