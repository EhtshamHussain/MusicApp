package com.example.musicapp.Screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.musicapp.MusicViewModel
import com.example.musicapp.SingerData.topSingersRow
import com.example.musicapp.SpeedDialor.RecentInteractionsSection
import com.example.musicapp.ui.theme.BottomBarColorYouTubeDark
import com.example.musicapp.ui.theme.DarkOnBackground

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController , bottomNavController: NavController,
               viewModel: MusicViewModel) {
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text("Home Screen", color = MaterialTheme.colorScheme.onBackground)
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                RecentInteractionsSection(
                    items = state.recentInteractions,
                    onPlayVideo = { videoItem ->
                        val singlePlaylist = listOf(videoItem)
                        val index = 0
                        viewModel.playVideo(videoItem.videoId, singlePlaylist, index)
                        viewModel.addRecentVideo(videoItem)
                        navController.navigate("PlayerScreen")
                    },
                    onOpenPlaylist = { playlist ->
                        bottomNavController.navigate("PlaylistDetail/${playlist.id}")
                        viewModel.addRecentPlaylist(playlist)
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.padding(top = 22.dp))
                Text(
                    "Top Singers ",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .padding(start = 16.dp, top = 10.dp)
                )
//                Spacer(modifier = Modifier.padding(top = 22.dp))


                topSingersRow(){singer ->
//                    Log.d("name", "HomeScreen: ${singer.name}")
                    viewModel.updateSearch(singer.name)
                    bottomNavController.navigate("SingerScreen/${singer.name}")

                }

            }
        }
    }
}





















@Composable
fun DialogBox(
    onClose: () -> Unit,
    onCreate: (String , String) -> Unit,

    ){
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = {
         onClose.invoke()
        }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            color = BottomBarColorYouTubeDark,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = "New Playlist",
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )

                TextField(value = name, onValueChange = {name=it},

                    placeholder = {
                        Text("title")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkOnBackground,
                        unfocusedContainerColor = BottomBarColorYouTubeDark
                    ))


                TextField(value = desc, onValueChange = {desc=it},
                    placeholder = {
                        Text("description")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkOnBackground,
                        unfocusedContainerColor = BottomBarColorYouTubeDark
                    ))

                Spacer(Modifier.height(22.dp))
                Row(Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = {onClose()}) {
                        Text("Cancel",color = DarkOnBackground)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onCreate(name,desc)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground
                        )) {
                        Text("Create",color = MaterialTheme.colorScheme.background)
                    }
                }
            }

        }
    }
}

