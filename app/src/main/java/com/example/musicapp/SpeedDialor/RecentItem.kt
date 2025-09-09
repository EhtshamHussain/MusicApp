package com.example.musicapp.SpeedDialor

import com.example.musicapp.Model.Playlist
import com.example.musicapp.Model.VideoItem

sealed class RecentItem{
    data class RecentPlaylist(val playlist: Playlist) : RecentItem()
    data class RecentVideo(val video: VideoItem) : RecentItem()
}
