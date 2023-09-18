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
import com.example.picview.databinding.FragmentAlbumsBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AlbumsFragment : Fragment(), AlbumsAdapter.AlbumClickListener {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private val context: Context? = activity?.applicationContext
    private val albumsData = ArrayList<AlbumData>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        fetchAlbumsData()
        val albumsAdapter = AlbumsAdapter(requireContext(), albumsData)

        binding.albumsRecyclerView.layoutManager = GridLayoutManager(context, 2)
        binding.albumsRecyclerView.adapter = albumsAdapter



        return binding.root
    }

    private fun fetchAlbumsData() {

        val projection =
            arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.DATA)

        val sortBy = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} ASC"

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortBy
        )
        val albumMap = mutableMapOf<String, String>()
        if (cursor != null) {
            if (cursor.moveToNext()) {
                val folderNameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                val imageCoverPathColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)

                do {
                    val folderName = cursor.getString(folderNameColumn)
                    val imageCoverPath = cursor.getString(imageCoverPathColumn)

                    if (!albumMap.containsKey(folderName)) {
                        albumMap[folderName] = imageCoverPath
                    }

                } while (cursor.moveToNext())
                albumMap.forEach { (folderName, imagePath) ->
                    albumsData.add(AlbumData(folderName, imagePath))
                }
            }
            cursor.close()
        }
    }

    private fun fetchImagesList(folderName: String): ArrayList<ImageData> {
        val tempImageList = ArrayList<ImageData>()

        val selection = "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"

        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)

        val sortBy = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = context?.contentResolver?.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortBy
        )

        if (cursor != null) {
            if (cursor.moveToNext()) {

                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

                val dateFormat = SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault())

                do {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

                    val formattedDate = dateFormat.format(Date(dateTaken))

                    tempImageList.add(ImageData(imageUri, formattedDate))
                } while (cursor.moveToNext())
            }
        }

        cursor?.close()
        return tempImageList
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAlbumClick(folderName: String, imagePath: String) {

    }
}