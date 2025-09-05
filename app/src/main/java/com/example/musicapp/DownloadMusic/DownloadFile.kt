package com.example.musicapp.DownloadMusic

import android.Manifest
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.NewPipe.getAudioUrl
import kotlinx.coroutines.launch
import java.io.File

@Composable
fun DownloadAudioButton(
    context: Context = LocalContext.current,
    fileName: String = "",
    viewItem: VideoItem
) {
    var showDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Launcher for WRITE_EXTERNAL_STORAGE (Android 9 or below)
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions.values.all { it }
        if (granted) {
            scope.launch {
                isLoading = true
                val audioUrl = getAudioUrl(viewItem.videoId)
                isLoading = false
                if (audioUrl.isNotBlank()) {
                    downloadFile(context, audioUrl, fileName)
                } else {
                    Toast.makeText(context, "Audio URL not found!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Storage permission required for download", Toast.LENGTH_LONG).show()
        }
    }

    // Launcher for POST_NOTIFICATIONS (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            scope.launch {
                isLoading = true
                val audioUrl = getAudioUrl(viewItem.videoId)
                isLoading = false
                if (audioUrl.isNotBlank()) {
                    downloadFile(context, audioUrl, fileName)
                } else {
                    Toast.makeText(context, "Audio URL not found!", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(context, "Notification permission required for download progress", Toast.LENGTH_LONG).show()
        }
    }

    // Button UI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true }
            .height(52.dp)
            .padding(5.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Download, contentDescription = "Download",
            modifier = Modifier
                .padding(start = 5.dp)
                .size(24.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(
            if (isLoading) "Fetching URL..." else "Download",
            fontSize = 17.sp,
            modifier = Modifier.weight(1f)
        )
    }

    // Confirmation Dialog
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Download Music?") },
            text = { Text("This music will be saved to your phone's Music folder and appear in music players") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        // Check permissions based on Android version
                        when {
                            // Android 13+: Check POST_NOTIFICATIONS
                            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.POST_NOTIFICATIONS
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    scope.launch {
                                        isLoading = true
                                        val audioUrl = getAudioUrl(viewItem.videoId)
                                        isLoading = false
                                        if (audioUrl.isNotBlank()) {
                                            downloadFile(context, audioUrl, fileName)
                                        } else {
                                            Toast.makeText(context, "Audio URL not found!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                            }
                            // Android 9 or below: Check storage permissions
                            Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                                if (ContextCompat.checkSelfPermission(
                                        context, Manifest.permission.WRITE_EXTERNAL_STORAGE
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    scope.launch {
                                        isLoading = true
                                        val audioUrl = getAudioUrl(viewItem.videoId)
                                        isLoading = false
                                        if (audioUrl.isNotBlank()) {
                                            downloadFile(context, audioUrl, fileName)
                                        } else {
                                            Toast.makeText(context, "Audio URL not found!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } else {
                                    storagePermissionLauncher.launch(
                                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    )
                                }
                            }
                            // Android 10+: No permissions needed for DownloadManager
                            else -> {
                                scope.launch {
                                    isLoading = true
                                    val audioUrl = getAudioUrl(viewItem.videoId)
                                    isLoading = false
                                    if (audioUrl.isNotBlank()) {
                                        downloadFile(context, audioUrl, fileName)
                                    } else {
                                        Toast.makeText(context, "Audio URL not found!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Text("Download", color = MaterialTheme.colorScheme.onBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

fun downloadFile(context: Context, url: String, fileName: String) {
    // Validate URL
    if (url.isBlank() || !url.startsWith("https://")) {
        Toast.makeText(context, "Invalid URL!", Toast.LENGTH_SHORT).show()
        return
    }

    // Sanitize file name and add .mp3 extension
    val safeTitle = fileName.replace("[^a-zA-Z0-9._\\s-]".toRegex(), "_") + ".mp3"

    try {
        // Use DownloadManager for all Android versions
        val request = DownloadManager.Request(Uri.parse(url)).apply {
            setTitle(fileName)
            setDescription("Downloading music...")
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            setMimeType("audio/mpeg")
            setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, safeTitle)
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            setAllowedOverRoaming(false)
        }

        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(context, "Download started! Check Music folder", Toast.LENGTH_LONG).show()

        // For Android 9 or below, scan file to make it visible in music players
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            val musicFile = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC),
                safeTitle
            )
            MediaScannerConnection.scanFile(
                context,
                arrayOf(musicFile.absolutePath),
                arrayOf("audio/mpeg"),
                null
            )
        }
    } catch (e: Exception) {
        Log.e("DownloadError", "Download failed: ${e.message}")
        Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}
