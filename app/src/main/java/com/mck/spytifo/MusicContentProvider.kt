package com.mck.spytifo

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.net.Uri
import android.os.ParcelFileDescriptor
import java.io.FileNotFoundException

class MusicContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "com.mck.spytifo.provider"
        private const val SONGS = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "songs/#", SONGS) // URI pattern for song files
        }
    }



    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        throw UnsupportedOperationException("Delete not supported")
    }

    override fun getType(uri: Uri): String? {
        return "audio/mpeg"

    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        throw UnsupportedOperationException("Insert not supported")
    }

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return null // Not implemented for raw files
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        throw UnsupportedOperationException("Update not supported")
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        return when (uriMatcher.match(uri)) {
            SONGS -> {
                val songId = uri.lastPathSegment?.toIntOrNull()
                if (songId != null) {
                    getRawFileDescriptor(songId)
                } else {
                    throw FileNotFoundException("Invalid song ID")
                }
            }
            else -> throw FileNotFoundException("Unknown URI: $uri")
        }
    }

    private fun getRawFileDescriptor(songId: Int): ParcelFileDescriptor? {
        val resId = when (songId) {
            1 -> R.raw.sample_sound // Map song IDs to raw resource files
            else -> throw FileNotFoundException("Song not found")
        }
        val afd: AssetFileDescriptor = context!!.resources.openRawResourceFd(resId)
        return afd.parcelFileDescriptor
    }
}