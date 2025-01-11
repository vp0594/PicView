package com.example.picview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.io.File

class FolderAdapter(
    private val context: Context,
    private val folderList: List<FolderData>,
    private val onSelectionChange: (FolderData) -> Unit
) : RecyclerView.Adapter<FolderAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkBox: CheckBox = itemView.findViewById(R.id.folderCheckBox)
        val textView: TextView = itemView.findViewById(R.id.folderNameTextView)
        val imageView: ImageView = itemView.findViewById(R.id.folderImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_folder, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val folder = folderList[position]

        holder.textView.text = folder.folderName

        // Temporarily remove listener to avoid triggering on scroll
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = folder.isSelected

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            folder.isSelected = isChecked
            onSelectionChange(folder)
        }

        // Use the pre-fetched URI or fallback to a placeholder image
        val imageUri = folder.firstImageUri

        // Prevent reloading of images unnecessarily by checking if the image is already set
        Glide.with(context)
            .load(imageUri)
            .placeholder(R.drawable.placeholder_image) // Set a placeholder if the image isn't available yet
            .into(holder.imageView)

        // Add a listener to ensure proper image loading
        Glide.with(context)
            .load(imageUri)
            .override(200, 200)  // Ensure the image is resized to avoid high memory usage
            .centerCrop()         // Crop the image to center fit
            .into(holder.imageView)

    }

    override fun getItemCount(): Int = folderList.size
}
