package com.example.musicapp.Model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

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