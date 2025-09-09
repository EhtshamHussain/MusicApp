package com.example.musicapp.Screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

    Scaffold(
        modifier = Modifier.fillMaxWidth(), topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text("Library", color = MaterialTheme.colorScheme.onBackground)
                },
                actions = {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
                            .clickable {
//                             viewModel.selecteTab.value=1
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


                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),


                )
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)


                .verticalScroll(rememberScrollState())
                .background(
                    MaterialTheme.colorScheme.background
                )
//                .padding(bottom = 60.dp)

        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 18.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { libraryNavController.navigate("Favourite") },
                        Modifier
                            .size(60.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(color = Color(0xFF2196F3))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite, // 👈 built-in Like icon
                            contentDescription = "Favorite ",
                            tint = Color.Blue, // color change kar sakte ho
                            modifier = Modifier.size(35.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                libraryNavController.navigate("Favourite")
                            },
                    ) {
                        Text(
                            "Liked Music",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier
                        )
                        Text("Auto playlist", color = DarkOnPrimary)
                    }

                }
            }

            Spacer(modifier = Modifier.height(20.dp))


            // dynamic playlists
            state.playlists.forEach { playlist ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 18.dp),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                libraryNavController.navigate("PlaylistDetail/${playlist.id}")
                                viewModel.addRecentPlaylist(playlist)
                            },  // assume new screen
                            Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(color = Color(0xFF2196F3))
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,  // default playlist icon
                                contentDescription = "Playlist ${playlist.name}",
                                tint = Color.White,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { libraryNavController.navigate("PlaylistDetail/${playlist.id}") },
                        ) {
                            Text(
                                playlist.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(playlist.description, color = DarkOnPrimary)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }



            Spacer(modifier = Modifier.height(20.dp))
            Surface(modifier = Modifier
                .width(120.dp)
                .clickable {
                    openDialog = true
                }
                .padding(end = 18.dp)
                .height(40.dp)
                .align(Alignment.End),
                color = MaterialTheme.colorScheme.onBackground,
                shape = RoundedCornerShape(24.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = DarkOnPrimary,
                        modifier = Modifier.size(30.dp)
                    )
                    Text("New", color = DarkPrimary, fontSize = 15.sp)
                }
            }
            if (openDialog) {
                DialogBox(onClose = {
                    openDialog = false }, onCreate = { name, description ->
                    val created = viewModel.createPlaylist(name, description)
                    if (created != null) {
                        libraryNavController.navigate("PlaylistDetail/${created.id}")
                        viewModel.addRecentPlaylist(created)
                    }
                })

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
            placeholder = painterResource(R.drawable.imageloader),
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
            modifier = Modifier.weight(1f) // text ke liye jagah
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


