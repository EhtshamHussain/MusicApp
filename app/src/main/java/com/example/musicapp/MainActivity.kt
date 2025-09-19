package com.example.musicapp


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.musicapp.DownloadMusic.DownloadAudioButton
import com.example.musicapp.DownloaderImpl.DownloaderImpl
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.Screen.NavigationScreen
import com.example.musicapp.Screen.PlayListContent
import com.example.musicapp.ui.theme.MusicAppTheme
import org.schabi.newpipe.extractor.NewPipe


class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewPipe.init(DownloaderImpl.init(null))
        setContent {

            MusicAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavigationScreen(modifier = Modifier.padding(innerPadding))
                }



            }

        }
    }
}



//AIzaSyDBR_TEw8EZDnqyjTc4QSUxK4Zyz86YLPk
@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)

@Composable
fun playerItems(viewModel: MusicViewModel, video: VideoItem) {
    val state by viewModel.uiState.collectAsState()
    val openSheet = remember { mutableStateOf(false) }


    Column {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(start = 10.dp, end = 15.dp)
        ) {
            item {
                Surface(
                    modifier = Modifier
                        .width(150.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    color = Color(0xFF3D2A26)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val isFavorite = state.favorites.contains(video)
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                            contentDescription = null,
                            tint = if (isFavorite) Color(0xFFFA5B02) else MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .clickable {
                                    viewModel.addToFavourites(video)
                                }
                                .size(26.dp)
                        )
                        VerticalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(25.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        val disLike = state.disLiked.contains(video)
                        Icon(
                            imageVector = if (disLike) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .clickable {
                                    viewModel.addToDisLiked(video)
                                }
                                .size(26.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }
            item {
                Surface(
                    modifier = Modifier
                        .width(100.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    color = Color(0xFF3D2A26)
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                openSheet.value = true
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "save",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        )

                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
            }

            item {
                val context = LocalContext.current
                Surface(
                    modifier = Modifier
                        .width(170.dp)
                        .height(40.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    color = Color(0xFF3D2A26)
                ) {
                    DownloadAudioButton(context, video.title, video)
                }
            }

        }
        if (openSheet.value) {
            ModalBottomSheet(
                onDismissRequest = { openSheet.value = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                PlayListContent(
                    video = video,
                    viewModel = viewModel,
                    onClose = { openSheet.value = false }
                )
            }
        }
    }

}

