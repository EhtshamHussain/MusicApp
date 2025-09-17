package com.example.musicapp.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.insets.GradientProtection
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.ui.theme.DarkOnPrimary
import com.example.musicapp.ui.theme.DarkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MenuScreen(
    navController: NavController,
    libraryNavController: NavController,
    viewModel: MusicViewModel,
) {
    val state by viewModel.uiState.collectAsState()
    var openDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    val isScrolled by remember {
        derivedStateOf {
            scrollState.value>0
        }

    }

    val targetWidth by animateDpAsState(
        targetValue = if (isScrolled) 66.dp else 120.dp,
        label = "widthAnim"
    )
    val targetShape by animateDpAsState(
        targetValue = if (isScrolled) 50.dp else 24.dp,
        label = "shapeAnim"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Library", color = MaterialTheme.colorScheme.onBackground) },
                actions = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable {
                                libraryNavController.navigate("HistoryScreen")
                            }
                    )
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable { navController.navigate("SearchScreen") }
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        }
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 40.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Scrollable playlists
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(scrollState)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Spacer(Modifier.height(20.dp))

                // Liked Music
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp)
                        .clickable { libraryNavController.navigate("Favourite") },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { libraryNavController.navigate("Favourite") },
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0x1CDBD6D2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Favorite",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            "Liked Music",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("Auto playlist", color = DarkOnPrimary)
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Dynamic playlists
                state.playlists.forEach { playlist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 18.dp)
                            .clickable {
                                libraryNavController.navigate("PlaylistDetail/${playlist.id}")
                                viewModel.addRecentPlaylist(playlist)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                        ) {
                            if (playlist.videos.isNotEmpty()) {
                                AsyncImage(
                                    model = playlist.videos.first().thumbnailUrl,
                                    contentDescription = null,
                                    placeholder = ColorPainter(Color(0xFF1E1E1E)),
                                    modifier = Modifier.matchParentSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Black.copy(alpha = 0.2f),
                                                    Color.Transparent
                                                )
                                            )
                                        )
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .background(Color(0x1CDBD6D2)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                                        contentDescription = "Playlist ${playlist.name}",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(Modifier.weight(1f)) {
                            Text(
                                playlist.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(playlist.description, color = DarkOnPrimary)
                        }
                    }
                    Spacer(Modifier.height(20.dp))
                }

                Spacer(Modifier.height(80.dp))

                // New playlist dialog
                AnimatedVisibility(openDialog) {
                    DialogBox(
                        onClose = { openDialog = false },
                        onCreate = { name, description ->
                            val created = viewModel.createPlaylist(name, description)
                            if (created != null) {
                                libraryNavController.navigate("PlaylistDetail/${created.id}")
                                viewModel.addRecentPlaylist(created)
                            }
                        }
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .padding(end = 18.dp)
                    .width(targetWidth)
                    .height(50.dp)
                    .align(Alignment.BottomEnd),
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(targetShape),
                onClick = { openDialog = true }
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DarkOnPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                    if (scrollState.value < 50) {
                        Text("New", color = DarkPrimary, fontSize = 15.sp)
                    }
                }
            }
        }
    }
}


@Composable
fun PlayingItem(
    id: String, title: String, thumnail: String, onClick: (VideoItem) -> Unit, onOpen: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 10.dp)
            .clickable { onClick(VideoItem(id, title, thumnail)) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(thumnail).crossfade(true)
                .build(),
            contentDescription = null,
            placeholder = ColorPainter(Color(0xFF1E1E1E)),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(100.dp)
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(8.dp))
        )

        Spacer(
            modifier = Modifier
                .width(10.dp)
                .height(82.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }


        Box(
            modifier = Modifier.padding(16.dp)
        ) {
            IconButton(
                onClick = { onOpen() }) {
                Icon(
                    Icons.Default.MoreVert,
                    contentDescription = "More options",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}