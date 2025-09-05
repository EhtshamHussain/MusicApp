package com.example.musicapp.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import java.util.UUID

data class NavigationItem(
    val title: String,
    val icon : ImageVector,
    val route : String
)


val navigationItem = listOf(
    NavigationItem(
        title = "Home",
        icon = Icons.Default.Home,
        route = "HomeScreen"
    ),
    NavigationItem(
        title = "Library",
        icon = Icons.Default.LibraryMusic,
        route = "MenuScreen"
    ),
    NavigationItem(
        title = "Settings",
        icon = Icons.Default.Settings,
        route = "SettingScreen"
    )


)
data class VideoItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String? = null,
)

data class Playlist(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val videos: List<VideoItem> = emptyList()
)