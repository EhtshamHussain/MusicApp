package com.example.musicapp.Screen

import android.icu.text.ListFormatter
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QueuePlayNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.DownloadMusic.DownloadAudioButton
import com.example.musicapp.EnumRemoveList.RemoveType
import com.example.musicapp.Model.NavigationItem
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    navController: NavController,
    bottomNavController: NavController,
    viewModel: MusicViewModel,
    playlistId: String,
) {
    val state by viewModel.uiState.collectAsState()
    val bottomSheet = remember { mutableStateOf(false) }

    val playlist = state.playlists.find { it.id == playlistId }
    var selectedItem by remember { mutableStateOf<VideoItem?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text("${playlist?.name} ", color = MaterialTheme.colorScheme.onBackground)
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { bottomNavController.popBackStack() }
                            .padding(end = 16.dp)
                    )
                },

                actions = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable { navController.navigate("SearchScreen") }

                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),


                )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                itemsIndexed(playlist?.videos ?: emptyList()) { index, it ->
                    PlayingItem(
                        it.videoId,
                        it.title,
                        it.thumbnailUrl ?: "",
                        onClick = {
                            selectedItem = it
                            viewModel.playVideo(it.videoId, playlist?.videos ?: emptyList(), index)
                            navController.navigate("PlayerScreen")
                            viewModel.addToRecentlyPlayed(it)
                            viewModel.addRecentVideo(it)
                        },
                        onOpen = {
                            selectedItem = it
                            bottomSheet.value = !bottomSheet.value
                            Log.d("TAG", "PlaylistDetailScreen: ${bottomSheet.value}")
                        }
                    )
                }
            }
        }
    }
    if (bottomSheet.value) {
        BottomSheet(
            selectedItem = selectedItem,
            viewModel = viewModel,
            playlistId,
            removeType = RemoveType.PLAYLIST,
            onDismiss = { bottomSheet.value = false })
    }
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    selectedItem: VideoItem?,
    viewModel: MusicViewModel,
    playlistId: String?=null,
    removeType: RemoveType,
    onDismiss: () -> Unit
) {
    if (selectedItem == null) return
    val video: VideoItem = selectedItem
    val bottomSheetState = rememberModalBottomSheetState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    ModalBottomSheet(
        sheetState = bottomSheetState,
        onDismissRequest = { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            // Thumbnail + Title + Like/Dislike Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(video.thumbnailUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    placeholder = painterResource(R.drawable.imageloader),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    video.title,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 11.sp,
                    maxLines = 2,
                    lineHeight = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.padding(start = 20.dp))

                IconButton(
                    onClick = {
                        viewModel.addToDisLiked(video)
                        onDismiss()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    val disLike = state.disLiked.contains(video)
                    Icon(
                        imageVector = if (disLike) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                    )
                }
                Spacer(Modifier.padding(start = 10.dp))
                IconButton(
                    onClick = {
                        viewModel.addToFavourites(video)
                        onDismiss()
                    },
                    modifier = Modifier.size(30.dp)
                ) {
                    val isFavorite = state.favorites.contains(video)
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = null,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.padding(top = 12.dp))


            TextButton(
                onClick = {
                    when (removeType) {
                        RemoveType.PLAYLIST -> {
                            if (playlistId != null) {
                                viewModel.removeFromSpecificPlaylist(playlistId, video)
                                Toast.makeText(context, "Removed From Playlist", Toast.LENGTH_SHORT).show()
                            }
                        }
                        RemoveType.FAVOURITES -> {
                            viewModel.removeFromFavourites(video.videoId)
                            Toast.makeText(context, "Removed From Favourites", Toast.LENGTH_SHORT).show()
                        }
                        RemoveType.HISTORY -> {
                            viewModel.removeFromHistory(video.videoId)
                            Toast.makeText(context, "Removed From History", Toast.LENGTH_SHORT).show()
                        }
                    }
                    onDismiss()
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        when (removeType) {
                            RemoveType.PLAYLIST -> "Remove From Playlist"
                            RemoveType.FAVOURITES -> "Remove From Favourites"
                            RemoveType.HISTORY -> "Remove From History"
                        },
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }


            DownloadAudioButton(context, video.title, video)


            TextButton(
                onClick = {
                    viewModel.playNext(video)
                    Toast.makeText(context, "Will play next", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(53.dp),
                colors = ButtonDefaults.textButtonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QueuePlayNext,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Play Next",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


