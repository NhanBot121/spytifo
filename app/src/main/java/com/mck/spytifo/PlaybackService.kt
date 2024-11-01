package com.mck.spytifo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContentProviderCompat.requireContext

class PlaybackService : Service() {

    private lateinit var mediaPlayer: MediaPlayer
    private val channelId = "music_playback_channel"

    /*
    Set-up the Service class structure
     */
    companion object {
        const val ACTION_PLAY = "action_play"
        const val ACTION_PAUSE = "action_pause"
        const val ACTION_STOP = "action_stop"
    }

    override fun onCreate() {
        super.onCreate()

        val contentUri: Uri = Uri.parse("content://com.mck.spytifo.provider/audio/sample_sound")

        mediaPlayer = MediaPlayer.create(this, R.raw.sample_sound_1)
        mediaPlayer.start()
        mediaPlayer.isLooping = true

//        mediaPlayer = MediaPlayer().apply {
//            setDataSource(applicationContext, contentUri)  // Use the content URI as the source
//            prepare()  // Or use prepareAsync() for asynchronous preparation
//            start()    // Start playback when ready
//        }

        createNotificationChannel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    /*
    Implement onStartCommand for playback control
     */
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show()
        val action = intent?.action
        when (action) {
            ACTION_PLAY -> playMusic()
            ACTION_PAUSE -> pauseMusic()
            ACTION_STOP -> stopMusic()
        }

        // Start the service in the foreground with a notification
        startForeground(1, createNotification("Playing music"))

        return START_NOT_STICKY
    }

    /*
    Implement playback control
     */
    private fun playMusic() {
        if (!mediaPlayer.isPlaying) {
            mediaPlayer.start()
            updateNotification("Playing music")
            sendPlaybackState("playing")

        }
    }

    private fun pauseMusic() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
            updateNotification("Paused music")
            sendPlaybackState("paused")

        }
    }

    // Reimplement later
    private fun stopMusic() {
        mediaPlayer.stop()
        mediaPlayer.reset()

        stopSelf()  // Stop the service
    }

    private fun sendPlaybackState(state: String) {
        val intent = Intent("com.mck.spytifo.PLAYBACK_STATE")
        intent.putExtra("state", state)
        sendBroadcast(intent)
    }

    /*
    Create the Notification Channel
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                channelId,
                "Music Playback Channel",
                NotificationManager.IMPORTANCE_LOW // Use IMPORTANCE_LOW or IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for music playback controls"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }


    /*
    Build the Notification with Media Controls
     */
    private fun createNotification(content: String): Notification {
        val playIntent = Intent(this, PlaybackService::class.java).apply {

            action = ACTION_PLAY
        }
        val pauseIntent = Intent(this, PlaybackService::class.java).apply { action = ACTION_PAUSE }
        val stopIntent = Intent(this, PlaybackService::class.java).apply { action = ACTION_STOP }

        val playPendingIntent = PendingIntent.getService(
            this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val pausePendingIntent = PendingIntent.getService(
            this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val stopPendingIntent = PendingIntent.getService(
            this, 2, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Simple Music Player")
            .setContentText(content)
            .setSmallIcon(R.drawable.ic_music_note)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .addAction(R.drawable.ic_play, "Play", playPendingIntent)
            .addAction(R.drawable.ic_pause, "Pause", pausePendingIntent)
            .addAction(R.drawable.ic_stop, "Stop", stopPendingIntent)
            .setStyle(androidx.media.app.NotificationCompat.MediaStyle())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
    }

    /*
    Update notification state
     */
    private fun updateNotification(content: String) {
        val notification = createNotification(content)
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notification)
    }

    /*
    Clean up resource
     */
    override fun onDestroy() {
        mediaPlayer.release()
        super.onDestroy()
        Toast.makeText(this, "service stopped", Toast.LENGTH_SHORT).show()
    }


}
