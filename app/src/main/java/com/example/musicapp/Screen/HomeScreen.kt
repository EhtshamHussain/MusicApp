package com.example.musicapp.Screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(viewModel: MusicViewModel, modifier: Modifier, navController: NavController) {
    val state by viewModel.uiState.collectAsState()


    Column(
        modifier = modifier.padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
//        viewModel.updateSearch(it)
        TextField(
            value = state.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("Search Music") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                viewModel.updateSearch(state.searchQuery)
            },
        ) {
            Text("Search ", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {

                CircularProgressIndicator()
            }


        } else {

            Spacer(modifier = Modifier.height(8.dp))
            LazyColumn {
                items(state.results) { video ->
                    var expanded by remember { mutableStateOf(false) }
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
                                viewModel.addToRecentlyPlayed(video)
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                video.title,
                                modifier = Modifier.weight(1f),
                                maxLines = 1,
                            )

                            Box(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        Icons.Default.MoreVert,
                                        contentDescription = "More options"
                                    )
                                }
                                val context = LocalContext.current
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Watch Letter") },
                                        onClick = {

                                            viewModel.addToPlayList(video)
                                            Toast.makeText(
                                                context,
                                                "Added to Playlist",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }


                    }
                }
            }
        }

    }
}



