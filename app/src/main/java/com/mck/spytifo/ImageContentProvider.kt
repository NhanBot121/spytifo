package com.mck.spytifo

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.FileNotFoundException

class ImageContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.mck.spytifo.provider"
        private const val IMAGE_PLAYLIST = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "playlist_images/#", IMAGE_PLAYLIST)
        }
    }

    override fun onCreate(): Boolean = true

    override fun getType(uri: Uri): String? {
        return "image/webp"  // MIME type for PNG images; adjust for your image type
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return when (uriMatcher.match(uri)) {
            IMAGE_PLAYLIST -> {
                val playlistId = uri.lastPathSegment?.toIntOrNull() ?: 0
                val resourceId = getImageResourceId(playlistId)  // Fetch the drawable resource ID
                if (resourceId != 0) {
                    val afd: AssetFileDescriptor = context!!.resources.openRawResourceFd(resourceId)
                    afd.parcelFileDescriptor
                } else {
                    throw FileNotFoundException("Resource not found for URI: $uri")
                }
            }
            else -> throw FileNotFoundException("Resource not found for URI: $uri")
        }
    }



    private fun getImageResourceId(playlistId: Int): Int {
        return when (playlistId) {
            1 -> R.drawable.suyt_1
            2 -> R.drawable.suyt_2
            else -> 0
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        TODO("Implement this to handle requests to delete one or more rows")
    }


    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        TODO("Implement this to handle requests to insert a new row.")
    }



    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        TODO("Implement this to handle query requests from clients.")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        TODO("Implement this to handle requests to update one or more rows.")
    }
}