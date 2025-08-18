package com.example.musicapp.ExoPlayer

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer

fun playAudio(context: Context, audioUrl: String): ExoPlayer {
    val player = ExoPlayer.Builder(context).build()
    val mediaItem = MediaItem.fromUri(audioUrl)
    player.setMediaItem(mediaItem)
    player.prepare()
    player.play()
    return player
}