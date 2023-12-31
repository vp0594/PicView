package com.example.picview

import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.picview.databinding.FragmentAlbumsBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale


class AlbumsFragment : Fragment(), AlbumsAdapter.AlbumClickListener {

    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!
    private lateinit var context: Context
    private var albumsData = ArrayList<AlbumData>()

    companion object {
        lateinit var mediaList: ArrayList<MediaData>
        var s = false
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        context = activity?.applicationContext!!
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)

        setUpRecyclerView()

        binding.favoritesButton.setOnClickListener {
            val intent = Intent(requireContext(), AlbumMedia::class.java)
            intent.putExtra("FolderName", "UserFav")
            startActivity(intent)
        }

        binding.swipeRefresh.setOnRefreshListener {
            setUpRecyclerView()
            binding.swipeRefresh.isRefreshing = false
        }

        return binding.root
    }


    private fun setUpRecyclerView() {
        albumsData = fetchAlbumsData()
        val sharedPreferences: SharedPreferences =
            context.getSharedPreferences("sharePref", AppCompatActivity.MODE_PRIVATE)
        val numberOfColumnAlbums: Int = sharedPreferences.getInt("ColumnAlbums", 2)
        binding.albumsRecyclerView.layoutManager = GridLayoutManager(context, numberOfColumnAlbums)
        val albumsAdapter = AlbumsAdapter(requireContext(), albumsData, this)
        binding.albumsRecyclerView.adapter = albumsAdapter
    }

    private fun fetchAlbumsData(): ArrayList<AlbumData> {

        val tempMediaList = ArrayList<AlbumData>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?)"

        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortBy = "${MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME} ASC"

        val queryUri = MediaStore.Files.getContentUri("external")

        val cursor = context.contentResolver.query(
            queryUri, projection, selection, selectionArgs, sortBy
        )
        val albumsMap = mutableMapOf<String, String>()

        if (cursor != null && cursor.moveToFirst()) {
            val folderNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME)

            val imageCoverPathColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)


            do {
                val folderName = cursor.getString(folderNameColumn)
                val imageCoverPath = cursor.getString(imageCoverPathColumn)

                if (!albumsMap.containsKey(folderName)) {
                    albumsMap[folderName] = imageCoverPath
                }


            } while (cursor.moveToNext())
            albumsMap.forEach { (folderName, imageCoverPath) ->
                tempMediaList.add(
                    AlbumData(
                        folderName, imageCoverPath
                    )
                )
            }

        }
        cursor?.close()
        return tempMediaList
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onAlbumClick(folderName: String) {
        mediaList = getMediaList(folderName)
        val intent = Intent(requireContext(), AlbumMedia::class.java)
        intent.putExtra("FolderName", folderName)
        startActivity(intent)
    }

    private fun getMediaList(folderName: String): ArrayList<MediaData> {
        val tempMediaList = ArrayList<MediaData>()

        //Querying for images and videos
        val projection = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DATE_TAKEN,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
        )

        val selection =
            "${MediaStore.Files.FileColumns.BUCKET_DISPLAY_NAME} = ? AND ${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?) "

        val selectionArgs = arrayOf(
            folderName,
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val sortBy = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"

        val queryUri = MediaStore.Files.getContentUri("external")

        val cursor = requireContext().contentResolver.query(
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

                tempMediaList.add(mediaItem)

            } while (cursor.moveToNext())
        }
        cursor?.close()

        return tempMediaList
    }

    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }


}