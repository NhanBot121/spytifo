package com.mck.spytifo

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Request notification permission on Android 13+ before starting playback
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }

        // Get a reference to the NavHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Link the bottom navigation bar to the navigation controller
        val bottomNavView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        playButton = findViewById(R.id.playButton)
        pauseButton = findViewById(R.id.pauseButton)

        // Initially, show the play button and hide the pause button
        togglePlayPause(isPlaying = false)

        // Play button click listener
        playButton.setOnClickListener {
            val playIntent = Intent(this, PlaybackService::class.java).apply {
                action = PlaybackService.ACTION_PLAY
            }
            startService(playIntent)
            togglePlayPause(isPlaying = true)
        }

        // Pause button click listener
        pauseButton.setOnClickListener {
            val pauseIntent = Intent(this, PlaybackService::class.java).apply {
                action = PlaybackService.ACTION_PAUSE
            }
            startService(pauseIntent)
            togglePlayPause(isPlaying = false)
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("com.mck.spytifo.PLAYBACK_STATE")
        registerReceiver(playbackStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterReceiver(playbackStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.e("MainActivity", "Receiver not registered")
        }
    }

    private val playbackStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getStringExtra("state")
            if (state == "playing") {
                togglePlayPause(isPlaying = true)
            } else if (state == "paused") {
                togglePlayPause(isPlaying = false)
            }
        }
    }

    // Toggle play and pause button visibility
    private fun togglePlayPause(isPlaying: Boolean) {
        playButton.visibility = if (isPlaying) View.GONE else View.VISIBLE
        pauseButton.visibility = if (isPlaying) View.VISIBLE else View.GONE
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}
