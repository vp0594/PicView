package com.example.picview

data class FolderData(
    val folderPath: String,
    val folderName: String,
    var isSelected: Boolean,
    val firstImageUri: String?
)
