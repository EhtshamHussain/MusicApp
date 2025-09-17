package com.example.musicapp.SingerData

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.Screen.MusicSheet

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun SingerScreen(singerName : String , navController: NavController , bottomNavController: NavController , viewModel: MusicViewModel) {
    val state by viewModel.uiState.collectAsState()
    val showBottomSheet = remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<VideoItem?>(null) }
    val listState = rememberLazyListState()
    if(state.isLoading){
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center){
            CircularProgressIndicator(modifier = Modifier.size(50.dp),
                color = MaterialTheme.colorScheme.onBackground,)
        }
    }else{
        LazyColumn(
            state = listState,
            modifier = Modifier.padding(start = 8.dp, top = 17.dp, bottom = 16.dp)
            ) {
            item {
                Text(singerName, color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    modifier = Modifier.padding(start =8.dp , bottom = 22.dp))

            }
            items(state.results) { video ->
                var expanded by remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(82.dp)
                        .combinedClickable(
                            onClick = {
                                val index = state.results.indexOf(video)
                                viewModel.playVideo(
                                    video.videoId,
                                    state.results,
                                    index
                                )
                                viewModel.addRecentVideo(video)
                                viewModel.addToRecentlyPlayed(video)
                                navController.navigate("PlayerScreen")

                            },
                            onLongClick = { showBottomSheet.value = true }
                        )
                        .padding(8.dp)
                ) {

                    if (video.thumbnailUrl != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(video.thumbnailUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                placeholder = ColorPainter(Color(0xFF1E1E1E)),
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(100.dp)
                                    .aspectRatio(16f / 9f)
                                    .clip(RoundedCornerShape(8.dp))
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(
                                modifier = Modifier.weight(1f) // text ke liye jagah
                            ) {
                                Text(
                                    text = video.title,
                                    fontSize = 14.sp,
                                    color = Color.White,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            IconButton(
                                onClick = {
                                    showBottomSheet.value = true
                                    selectedItem = video
                                }

                            ) {
                                Icon(
                                    Icons.Default.MoreVert,
                                    contentDescription = "More options",
                                    tint = Color.White
                                )

                            }
                        }
                    }
                }
            }
            if (state.nextPage != null && !state.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth(), Alignment.Center) {
                        CircularProgressIndicator()  // Jab loading more ho
                    }
                }
            }
        }
        LaunchedEffect(listState) {
            snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                .collect { lastIndex ->
                    if (lastIndex != null && lastIndex >= state.results.size - 1 && state.nextPage != null) {
                        viewModel.loadMore()  // End pe load more
                    }
                }
        }

        if (showBottomSheet.value) {
            MusicSheet(viewModel, navController, showBottomSheet, selectedItem)
        }


    }

}