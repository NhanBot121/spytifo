package com.mck.spytifo

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mck.spytifo.ui.theme.SpytifoTheme

class MainActivity : AppCompatActivity() {

    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Get a ref. to the navHostFragment
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        // Link the bottom navigation bar
        //to the navigation controller
        val bottomNavView = findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavView.setupWithNavController(navController)

        playButton = findViewById<ImageButton>(R.id.playButton)
        pauseButton = findViewById<ImageButton>(R.id.pauseButton)

        // Initially, show the play button and hide the pause button
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.GONE

        // Play button click listener
        playButton.setOnClickListener {
            // Start the playback service with the play action
            val playIntent = Intent(this, PlaybackService::class.java).apply {
                action = PlaybackService.ACTION_PLAY
            }
            startService(playIntent)

            // Toggle buttons
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }

        // Pause button click listener
        pauseButton.setOnClickListener {
            // Start the playback service with the pause action
            val pauseIntent = Intent(this, PlaybackService::class.java).apply {
                action = PlaybackService.ACTION_PAUSE
            }
            startService(pauseIntent)

            // Toggle buttons
            pauseButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter("com.mck.spytifo.PLAYBACK_STATE")
        registerReceiver(playbackStateReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(playbackStateReceiver)
    }

    private val playbackStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val state = intent?.getStringExtra("state")
            if (state == "playing") {
                playButton.visibility = View.GONE
                pauseButton.visibility = View.VISIBLE
            } else if (state == "paused") {
                playButton.visibility = View.VISIBLE
                pauseButton.visibility = View.GONE
            }
        }
    }
}