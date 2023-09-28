package com.example.picview

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import java.io.File

class FavouritesDataBase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "FavouriteImages"
        private const val DATABASE_VERSION = 1

        const val imageUris = "imageUris"
        const val dateTaken = "dateTaken"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE IF NOT EXISTS Favourites($imageUris TEXT PRIMARY KEY,$dateTaken TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Favourites")
        onCreate(db)
    }

    fun addFavourites(imageData: ImageData) {
        val values = ContentValues()

        values.put(imageUris, imageData.mediaUri.toString())
        values.put(dateTaken, imageData.dateTake)

        val db = this.writableDatabase
        db.insert("Favourites", null, values)

        db.close()
    }

    fun removeFavourites(imageUri: String) {
        val db = this.writableDatabase
        val query = "DELETE FROM Favourites WHERE $imageUris='$imageUri'"
        db.execSQL(query)

    }

    fun ifImageExits(imageUri: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $imageUris FROM Favourites WHERE $imageUris='$imageUri'"
        val cursor = db.rawQuery(query, null)
        var value: String? = null


        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndexOrThrow(imageUris))
        }
        cursor.close()
        if (value != null)
            return true
        return false
    }

    fun getFavouritesImageList(): ArrayList<ImageData> {
        val tempImageList = ArrayList<ImageData>()
        val db = this.readableDatabase
        val query = "SELECT * FROM Favourites"

        val cursor = db.rawQuery(query, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(imageUris))
                val formattedDate = cursor.getString(cursor.getColumnIndexOrThrow(dateTaken))
                val imageUri = Uri.parse(imageUriStr)

                tempImageList.add(ImageData(imageUri, formattedDate,false))
            }
            cursor.close()
        }

        return tempImageList
    }


    private fun getDateModified(path: String): Long {
        return File(path).lastModified()
    }
}