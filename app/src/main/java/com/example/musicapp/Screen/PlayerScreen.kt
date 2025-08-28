package com.example.musicapp.Screen

import android.annotation.SuppressLint
import android.text.Layout
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.contentType
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@SuppressLint("RememberReturnType")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
//@Preview(device = "spec:width=1080dp,height=2340dp,dpi=440,isRound=false,chinSize=0dp")

@Composable
fun PlayerScreen(viewModel: MusicViewModel, navController: NavController) {

    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val offsetY = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val currentVideo = state.currentPlaylist.getOrNull(state.currentIndex)
    val imageRequest = remember(currentVideo?.thumbnailUrl) {
        ImageRequest.Builder(context)
            .data(currentVideo?.thumbnailUrl)
            .crossfade(true)
            .build()
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colorScheme.background
            ), containerColor = Color.Transparent, topBar = {
            val alpha by remember {
                derivedStateOf {
                    if (offsetY.value <= 0f) 1f
                    else if (offsetY.value >= 300f) 0f
                    else 1f - (offsetY.value / 300f)
                }
            }
            TopAppBar(
                modifier = Modifier.alpha(alpha), title = {
                    Text(
                        "NOW PLAYING",
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }, navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Minimize",
                            modifier = Modifier.size(50.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }, actions = {
                    IconButton(onClick = {
                        val currentVideo = state.currentPlaylist.getOrNull(state.currentIndex)
                        if (currentVideo != null) {
                            viewModel.addToFavourites(currentVideo)
                        }
                    }) {
                        val currentVideo = state.currentPlaylist.getOrNull(state.currentIndex)
                        val isFavorite = currentVideo != null && state.favorites.any { it.videoId == currentVideo.videoId }

                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favourite",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }, colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .offset { IntOffset(0, offsetY.value.roundToInt()) }
                .draggable(state = rememberDraggableState { delta ->
                    coroutineScope.launch {
                        val potential = offsetY.value + delta
                        if (potential >= 0) {  // Allow only downward drag
                            offsetY.snapTo(potential)
                        }
                    }
                }, orientation = Orientation.Vertical, onDragStopped = { velocity ->
                    coroutineScope.launch {
                        if (offsetY.value > 200 || velocity > 1000f) {
                            offsetY.animateTo(2000f, animationSpec = tween(300))
                            navController.popBackStack()
                        } else {
                            offsetY.animateTo(0f, animationSpec = tween(300))
                        }
                    }
                }), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.isLoading == true) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .padding(16.dp),
                    color = MaterialTheme.colorScheme.onBackground
                )
            } else {
                Box {


                    AsyncImage(
                        model = imageRequest,
                        contentDescription = null,
                        placeholder = painterResource(R.drawable.imageloader),
                        error = painterResource(R.drawable.imageloader), // important
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(330.dp)
                            .padding(10.dp)

                    )
                }


                Text(
                    state.currentPlaylist.getOrNull(state.currentIndex)?.title ?: "",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .basicMarquee(),
                    textAlign = TextAlign.Center
                )

//                var progress by remember { mutableStateOf(0f) }
//                LaunchedEffect(viewModel.exoPlayer) {
//                    while (viewModel.exoPlayer != null) {
//                        progress = (viewModel.exoPlayer?.currentPosition?.toFloat()
//                            ?: 0f) / (viewModel.exoPlayer?.duration?.toFloat() ?: 1f)
//                        delay(100)
//                    }
//                }

////                }
//                var progress by remember { mutableStateOf(0f) }
//                LaunchedEffect(Unit) { // Unit = always survive recompositions
//                    while (true) {
//                        viewModel.exoPlayer?.let {
//                            progress =
//                                (it.currentPosition.toFloat()) / (it.duration.takeIf { d -> d > 0 }
//                                    ?.toFloat() ?: 1f)
//                        }
//                        delay(100)
//                    }
//                }
                Log.d("check", "PlayerScreen: ${viewModel.progress}")

                val progress by viewModel.progress.collectAsState()

                Slider(
                    value = progress,
                    onValueChange = { newValue ->
                        viewModel.seekTo(
                            (newValue * (viewModel.exoPlayer?.duration ?: 1L)).toLong()
                        )
//                        view = newValue
                    },
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .align(Alignment.CenterHorizontally),
                    thumb = {
                        SliderDefaults.Thumb(
                            interactionSource = remember { MutableInteractionSource() },
                            thumbSize = DpSize(10.dp, 10.dp),
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.onBackground
                            )
                        )
                    },
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Green, inactiveTrackColor = Color.Gray
                    )
                )


                Row(
                    modifier = Modifier
                        .fillMaxWidth(.9f)
                        .padding(horizontal = 19.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = formatTime(viewModel.exoPlayer?.currentPosition ?: 0L),
                        color = Color.White
                    )
                    Text(
                        text = formatTime(viewModel.exoPlayer?.duration ?: 1L), color = Color.White
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { viewModel.playPrevious() }) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(
                        onClick = {
                            if (viewModel.exoPlayer?.isPlaying == true) viewModel.pause() else viewModel.resume()
                        },
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF304FFE), Color(0xFF00B8D4))
                                ), shape = CircleShape
                            ),
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.Transparent)
                    ) {
                        Icon(
                            imageVector = if (viewModel.exoPlayer?.isPlaying == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    IconButton(onClick = { viewModel.playNext() }) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(40.dp),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MiniPlayer(viewModel: MusicViewModel, onExpand: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    val currentVideo = state.currentPlaylist.getOrNull(state.currentIndex) ?: return
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    MaterialTheme.colorScheme.surface
                )  // Dark background to match gradient
                .clickable { onExpand() }
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(currentVideo.thumbnailUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                placeholder = painterResource(R.drawable.imageloader),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = currentVideo.title,
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
//                maxLines = 1,
                modifier = Modifier
                    .weight(1f)
                    .basicMarquee()
            )

//        }
            IconButton(onClick = {
                if (viewModel.exoPlayer?.isPlaying == true) viewModel.pause() else viewModel.resume()
            }) {
                Icon(
                    imageVector = if (viewModel.exoPlayer?.isPlaying == true) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
        }
//        var progress by remember { mutableStateOf(0f) }
//        LaunchedEffect(Unit) { // Unit = always survive recompositions
//            while (true) {
//                viewModel.exoPlayer?.let {
//                    progress = (it.currentPosition.toFloat()) / (it.duration.takeIf { d -> d > 0 }
//                        ?.toFloat() ?: 1f)
//                }
//                delay(100)
//            }
//        }
        val progress by viewModel.progress.collectAsState()

        Log.d("check", "PlayerScreen: ${viewModel.progress}")
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .size(1.dp),
            color = MaterialTheme.colorScheme.onBackground,
            trackColor = MaterialTheme.colorScheme.primary,
            strokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
        )
    }
}

// Your existing formatTime and abc preview remain the same...
fun formatTime(milliseconds: Long): String {
    val seconds = (milliseconds / 1000) % 60
    val minutes = (milliseconds / (1000 * 60)) % 60
    return String.format("%02d:%02d", minutes, seconds)
}


@Composable
fun CustomSeekBar(
    progress: Float,              // 0f..1f
    bufferProgress: Float = 0f,   // 0f..1f (optional, for buffered progress like YouTube)
    onProgressChange: (Float) -> Unit
) {
    val thumbRadius = 6.dp
    val trackHeight = 4.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp) // enough height for touch area
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                    onProgressChange(newProgress)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                    onProgressChange(newProgress)
                }
            }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barY = size.height / 2

            // background track (grey)
            drawRoundRect(
                color = Color.Gray.copy(alpha = 0.4f),
                topLeft = Offset(0f, barY - trackHeight.toPx() / 2),
                size = Size(size.width, trackHeight.toPx()),
                cornerRadius = CornerRadius(trackHeight.toPx() / 2)
            )

            // buffered track (light grey)
            drawRoundRect(
                color = Color.LightGray,
                topLeft = Offset(0f, barY - trackHeight.toPx() / 2),
                size = Size(size.width * bufferProgress, trackHeight.toPx()),
                cornerRadius = CornerRadius(trackHeight.toPx() / 2)
            )

            // progress track (red like YouTube)
            drawRoundRect(
                color = Color.Red,
                topLeft = Offset(0f, barY - trackHeight.toPx() / 2),
                size = Size(size.width * progress, trackHeight.toPx()),
                cornerRadius = CornerRadius(trackHeight.toPx() / 2)
            )

            // thumb (circle, flat no shadow)
            drawCircle(
                color = Color.Red,
                radius = thumbRadius.toPx(),
                center = Offset(size.width * progress, barY)
            )
        }
    }
}
