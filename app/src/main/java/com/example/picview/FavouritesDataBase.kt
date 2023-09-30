package com.example.picview

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri

class FavouritesDataBase(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "FavouriteMedias"
        private const val DATABASE_VERSION = 1

        const val mediaUris = "imageUris"
        const val dateTaken = "dateTaken"
        const val isVideos = "false"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            "CREATE TABLE IF NOT EXISTS Favourites($mediaUris TEXT PRIMARY KEY,$dateTaken TEXT,$isVideos TEXT)"
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS Favourites")
        onCreate(db)
    }

    fun addFavourites(mediaData: MediaData) {
        val values = ContentValues()

        values.put(mediaUris, mediaData.mediaUri.toString())
        values.put(dateTaken, mediaData.dateTake)
        values.put(isVideos, mediaData.isVideo.toString())

        val db = this.writableDatabase
        db.insert("Favourites", null, values)

        db.close()
    }

    fun removeFavourites(mediaUri: String) {
        val db = this.writableDatabase
        val query = "DELETE FROM Favourites WHERE $mediaUris='$mediaUri'"
        db.execSQL(query)

    }

    fun ifMediaExits(mediaUri: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT $mediaUris FROM Favourites WHERE $mediaUris='$mediaUri'"
        val cursor = db.rawQuery(query, null)
        var value: String? = null


        if (cursor.moveToNext()) {
            value = cursor.getString(cursor.getColumnIndexOrThrow(mediaUris))
        }
        cursor.close()
        if (value != null)
            return true
        return false
    }

    fun getFavouritesMediaList(): ArrayList<MediaData> {
        val tempImageList = ArrayList<MediaData>()
        val db = this.readableDatabase
        val query = "SELECT * FROM Favourites"

        val cursor = db.rawQuery(query, null)

        if (cursor != null) {
            while (cursor.moveToNext()) {
                val imageUriStr = cursor.getString(cursor.getColumnIndexOrThrow(mediaUris))
                val formattedDate = cursor.getString(cursor.getColumnIndexOrThrow(dateTaken))
                val isVideo:Boolean = cursor.getString(cursor.getColumnIndexOrThrow(isVideos)) != "false"
                val mediaUri = Uri.parse(imageUriStr)

                tempImageList.add(MediaData(mediaUri, formattedDate, isVideo))
            }
            cursor.close()
        }

        return tempImageList
    }

}

