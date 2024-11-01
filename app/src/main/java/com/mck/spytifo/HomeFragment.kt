package com.mck.spytifo

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

class HomeFragment : Fragment() {

    private lateinit var playlistImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        playlistImageView = view.findViewById(R.id.playlistImageView)

        // Load the playlist image using the ContentProvider URI
//        val playlistImageUri = Uri.parse("content://com.mck.spytifo.provider/playlist_images/1")
//        playlistImageView.setImageURI(playlistImageUri)

        playlistImageView.setImageResource(R.drawable.suyt_1)

//        val contentUri = Uri.parse("content://com.mck.spytifo.provider/playlist_images/1")
//        val parcelFileDescriptor = requireContext().contentResolver.openFileDescriptor(contentUri, "r")
//        parcelFileDescriptor?.let {
//            val bitmap = BitmapFactory.decodeFileDescriptor(it.fileDescriptor)
//            playlistImageView.setImageBitmap(bitmap)
//            it.close()
//        } ?: Log.e("HomeFragment", "Failed to load WebP image from URI: $contentUri")

        return view
    }
}