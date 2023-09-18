package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.FragmentAlbumsBinding


class AlbumsFragment : Fragment(), AlbumsAdapter.AlbumClickListener {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private val context: Context? = activity?.applicationContext
    private val albumsData = ArrayList<AlbumData>()

    companion object {
        lateinit var imageList: ArrayList<ImageData>
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        fetchAlbumsData()
        val albumsAdapter = AlbumsAdapter(requireContext(), albumsData, this)

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAlbumClick(folderName: String) {
        imageList = getImageList(folderName)
        val intent = Intent(requireContext(), AlbumImages::class.java)
        startActivity(intent)
    }

    private fun getImageList(folderName: String): ArrayList<ImageData> {
        val imageList = ArrayList<ImageData>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATE_TAKEN
        )

        val selection =
            "${MediaStore.Images.Media.BUCKET_DISPLAY_NAME} = ?"

        val selectionArgs = arrayOf(folderName)

        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        if (cursor != null) {
            if (cursor.moveToNext()) {
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateTakenColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)

                do {
                    val id = cursor.getLong(idColumn)
                    val dateTaken = cursor.getLong(dateTakenColumn)
                    val imageUri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    imageList.add(ImageData(imageUri, dateTaken.toString()))
                } while (cursor.moveToNext())
            }
            cursor.close()
        }

        return imageList
    }


}