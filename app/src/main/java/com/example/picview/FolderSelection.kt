package com.example.picview

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.picview.databinding.ActivityFolderSelectionBinding
import java.io.File

class FolderSelection : AppCompatActivity() {


    private lateinit var binding: ActivityFolderSelectionBinding
    private val selectedFolders = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityFolderSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val folders = getAllMediaFolders()
        selectedFolders.addAll(folders.filter { it.isSelected }
            .map { it.folderPath }) // Add selected folders

        binding.folderRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.folderRecyclerView.adapter = FolderAdapter(this, folders) { folder ->
            if (folder.isSelected) {
                selectedFolders.add(folder.folderPath)
            } else {
                selectedFolders.remove(folder.folderPath)
            }
        }
        binding.folderRecyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())

        binding.saveButton.setOnClickListener {
            saveSelectedFolders(selectedFolders)
            finish()
        }
    }


    private fun saveSelectedFolders(selectedFolders: Set<String>) {
        val sharedPreferences = getSharedPreferences("FolderPreferences", Context.MODE_PRIVATE)
        sharedPreferences.edit().putStringSet("SelectedFolders", selectedFolders).apply()
    }


    private fun getAllMediaFolders(): List<FolderData> {
        val sharedPreferences = getSharedPreferences("FolderPreferences", Context.MODE_PRIVATE)
        val savedSelectedFolders =
            sharedPreferences.getStringSet("SelectedFolders", emptySet()) ?: emptySet()

        val folderList = mutableMapOf<String, FolderData>()

        val projection = arrayOf(
            MediaStore.Files.FileColumns.DATA, MediaStore.Files.FileColumns.MEDIA_TYPE
        )

        val selection = "${MediaStore.Files.FileColumns.MEDIA_TYPE} IN (?,?)"
        val selectionArgs = arrayOf(
            MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE.toString(),
            MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO.toString()
        )

        val cursor = contentResolver.query(
            MediaStore.Files.getContentUri("external"), projection, selection, selectionArgs, null
        )

        cursor?.use {
            val pathColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)

            while (cursor.moveToNext()) {
                val filePath = cursor.getString(pathColumn)
                val folderPath = File(filePath).parent ?: continue
                val folderName = File(folderPath).name

                if (!folderList.containsKey(folderPath)) {
                    val isSelected = folderPath in savedSelectedFolders
                    // Fetch the first image URI for the folder
                    val firstImageUri = getFirstImageFromFolder(folderPath)

                    folderList[folderPath] = FolderData(
                        folderPath, folderName, isSelected, firstImageUri
                    )
                }
            }
        }

        return folderList.values.toList()
    }

    private fun getFirstImageFromFolder(folderPath: String): String? {
        val folder = File(folderPath)
        val files = folder.listFiles { file ->
            file.isFile && (file.extension.equals("jpg", true) || file.extension.equals(
                "png", true
            ) || file.extension.equals("mp4", true))
        }
        return files?.firstOrNull()?.absolutePath
    }


    override fun onPause() {
        super.onPause()

        ShuffleImages.isFromFolderSelection=true
    }
}


