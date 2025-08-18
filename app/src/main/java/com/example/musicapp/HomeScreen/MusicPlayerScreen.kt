package com.example.musicapp.HomeScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R

@Composable
fun MusicPlayerScreen(viewModel: MusicViewModel, modifier: Modifier, navController: NavController) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = modifier.padding(16.dp)) {
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearch(it) },
            placeholder = { Text("Search Music") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(state.results) { video ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val index = state.results.indexOf(video)
                            viewModel.playVideo(
                                video.videoId,
                                state.results,
                                index
                            )
                            navController.navigate("PlayerScreen")
                        }
                        .padding(8.dp)
                ) {
                    if (video.thumbnailUrl != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(video.thumbnailUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = null,
                            placeholder = painterResource(R.drawable.imageloader),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp) // fix height for smooth layout
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(video.title)
                }
            }
        }
        // Log the state for debugging

    }
}