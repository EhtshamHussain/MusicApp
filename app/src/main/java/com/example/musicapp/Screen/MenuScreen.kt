package com.example.musicapp.Screen

import android.os.Build
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LibraryAdd
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.VideoItem
import kotlinx.coroutines.Dispatchers

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MenuScreen(
    navController: NavController,
    libraryNavController: NavController,
    viewModel: MusicViewModel,

    ) {

    val state by viewModel.uiState.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
         TopAppBar(
             modifier = Modifier.fillMaxWidth(),
             title = {
             Text("Library", color = Color.White)
             },
             actions = {
                 Icon(imageVector = Icons.Default.History, contentDescription = null ,
                     tint = Color.White,
                     modifier = Modifier.size(40.dp)
                         .padding(end=10.dp)
                         .clickable {
//                             viewModel.selecteTab.value=1
                             libraryNavController.navigate("HistoryScreen")
                         }

                 )

                 Icon(imageVector = Icons.Default.Search , contentDescription = null ,
                     tint = Color.White,
                     modifier = Modifier.size(40.dp)
                         .padding(end=10.dp)
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
                .verticalScroll(rememberScrollState())
                .background(
                    Color.Black
                )
                .padding(bottom = 60.dp)
                .padding(innerPadding)

        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Spacer(modifier = Modifier.height(30.dp))



            Text(
                "Recent Played", color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 21.dp)
            )
            LazyRow {
                items(state.recentlyPlayed) {
                    PlayingItem(
                        it.videoId,
                        it.title,
                        it.thumbnailUrl ?: ""
                    ) {
                        val index = state.recentlyPlayed.indexOf(it)
                        viewModel.playVideo(it.videoId, state.recentlyPlayed, index)
                        navController.navigate("PlayerScreen")
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "Favourite", color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 21.dp)
            )


            LazyRow {
                items(state.favorites) {
                    PlayingItem(
                        it.videoId, it.title, it.thumbnailUrl ?: ""
                    ) {
                        val index = state.favorites.indexOf(it)
                        viewModel.playVideo(it.videoId, state.favorites, index)
                        viewModel.addToRecentlyPlayed(it)
                        navController.navigate("PlayerScreen")
                    }
                }
            }


            Box(
                Modifier
                    .size(75.dp)
                    .background(color = Color.Gray)
                    .clickable {
                        libraryNavController.navigate("PlayList")
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
            }


            Spacer(modifier = Modifier.height(30.dp))
            Text(
                "PlayList", color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 21.dp)
            )


            LazyRow() {
                items(state.playList) {
                    PlayingItem(it.videoId, it.title, it.thumbnailUrl ?: "") { video ->
                        val index = state.playList.indexOf(video)
                        viewModel.playVideo(video.videoId, state.playList, index)
                        viewModel.addToRecentlyPlayed(video)
                        navController.navigate("PlayerScreen")

                    }
                }
            }


        }
    }
}

    @Composable
    fun PlayingItem(id: String, title: String, thumnail: String, onClick: (VideoItem) -> Unit) {

        var expanded by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
                .clickable { onClick(VideoItem(id, title, thumnail)) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(thumnail)
                    .crossfade(true)
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
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }


            Box(
                                        modifier = Modifier
                                            .padding(16.dp)
            ) {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = Color.White
                    )
                }


            }
        }
    }

//
//@Preview( )
//@Composable
//fun box(modifier: Modifier = Modifier) {
//Column ( Modifier
//    .fillMaxSize()
//    .background(Color.Black),
//    verticalArrangement = Arrangement.Center,
//    horizontalAlignment = Alignment.CenterHorizontally){
//    Box(
//        Modifier
//            .size(75.dp)
//            .background(color = Color.Gray)
//            .clickable {
//            },
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(imageVector = Icons.Default.FavoriteBorder, contentDescription = null)
//    }
//}
//
//
//}
