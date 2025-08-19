package com.example.musicapp.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.VideoItem
import kotlinx.coroutines.Dispatchers

@Composable
fun MenuScreen(navController: NavController, viewModel: MusicViewModel) {

    val state by viewModel.uiState.collectAsState()
//

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(
                brush = Brush.linearGradient(
                    listOf(Color(0xFF4F08BD), Color(0xFF0D171A))
                )
            )
            .padding(bottom = 60.dp)

    ) {
        Spacer(modifier = Modifier.height(20.dp))
        Surface(
            modifier = Modifier
                .fillMaxWidth(.95f)
                .clickable { navController.navigate("HomeScreen") }
                .align(Alignment.CenterHorizontally)
                .height(35.dp),
            shape = RoundedCornerShape(20.dp),
            color = Color(0x979E0BE8)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Search, contentDescription = null,
                    modifier = Modifier.padding(top = 7.dp, start = 10.dp),
                    tint = Color.White
                )
                Text(
                    "Search", modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 7.dp, start = 10.dp),
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
        Text(
            "Recently Played", color = Color.White,
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

@Composable
fun PlayingItem(id: String, title: String, thumnail: String, onClick: (VideoItem) -> Unit) {
    Column(
        modifier = Modifier
            .clickable { onClick(VideoItem(id, title, thumnail)) }
            .width(200.dp),
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Spacer(modifier = Modifier.height(12.dp))
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(thumnail)
                .crossfade(true)
                .dispatcher(Dispatchers.IO)
                .build(),
            contentDescription = "",
            placeholder = painterResource(R.drawable.imageloader),
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .height(120.dp)
                .padding(start = 15.dp)
                .clip(RoundedCornerShape(25.dp))

        )

        Text(
            text = title,
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            maxLines = 1,
            fontSize = 12.sp,
            textAlign = TextAlign.Center,

            )
        Spacer(modifier = Modifier.width(12.dp))
    }
}

//
//data class Item(
//    val title: String,
//    val thumbnail: Int,
//)


//val recentPlayed = remember {
//        mutableListOf(
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//
//            )
//    }
//
//    val favouriteList = remember {
//        mutableListOf(
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//
//            )
//    }
//
//    val playList = remember {
//        mutableListOf(
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//            Item("YouTube Video", R.drawable.image),
//
//            )
//    }