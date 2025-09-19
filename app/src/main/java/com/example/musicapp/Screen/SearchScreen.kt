package com.example.musicapp.Screen

import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.layout.windowInsetsEndWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.QueuePlayNext
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Precision
import com.example.musicapp.DownloadMusic.DownloadAudioButton
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.SharedPreferences.SearchPrefManager
import com.example.musicapp.ui.theme.DarkOnPrimary
import com.example.musicapp.ui.theme.DarkPrimary
import kotlinx.coroutines.Dispatchers

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicPlayerScreen(viewModel: MusicViewModel, modifier: Modifier, navController: NavController) {
    val state by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val manager = remember { SearchPrefManager(context) }

    val listState = rememberLazyListState()

    var selectedItem by remember { mutableStateOf<VideoItem?>(null) }

    val showBottomSheet = remember { mutableStateOf(false) }
    val searches = remember { mutableStateListOf<String>() }

    LaunchedEffect(Dispatchers.IO) {
        viewModel.clearSearchResults()
        searches.clear()
        searches.addAll(manager.getSearches())
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start

        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "back Arrow",
                modifier = Modifier
                    .clickable {
                        viewModel.clearSearchResults()
                        viewModel.selecteTab.value = 1
                        val poped = navController.popBackStack()
                        if (!poped) {
                            navController.navigate("MainScreen") {
                                popUpTo("MainScreen") {
                                    inclusive = true
                                }
                                launchSingleTop = true
                            }
                        }
//                        navController.navigate("MainScreen")
                    }
                    .size(35.dp)
                    .padding(start = 5.dp, end = 5.dp),
                tint = MaterialTheme.colorScheme.onBackground
            )
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search Music", color = Color.LightGray) },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search, contentDescription = "Search",
                        modifier = Modifier
                            .clickable {
                                if (state.searchQuery.isNotEmpty()) {
                                    viewModel.updateSearch(state.searchQuery)
                                    keyboardController?.hide()
                                    focusManager.clearFocus()
                                    manager.saveSearch(state.searchQuery)
                                }
                            }
                            .size(32.dp),
                        tint = Color.White)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(end = 10.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (state.searchQuery.isNotEmpty()) {
                            viewModel.updateSearch(state.searchQuery)
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            manager.saveSearch(state.searchQuery)
                        }

                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0x9F444242),
                    unfocusedContainerColor = Color(0x9F444242),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                )

            )

        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        } else {
            if (state.results.isEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(searches) { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.updateSearchQuery(item)
                                        viewModel.updateSearch(item)
                                        keyboardController?.hide()
                                        focusManager.clearFocus()
                                    }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "History",
                                    tint = Color.Gray
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = item, color = Color.White)
                            }
                            IconButton(onClick = {
                                manager.deleteSearch(item)
                                searches.remove(item)
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                }

            } else {
                Spacer(modifier = Modifier.height(8.dp))
                LazyColumn(state = listState) {
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
//                                            .precision(Precision.EXACT)
                                            .build(),
                                        contentDescription = null,
                                        placeholder = ColorPainter(Color(0xFF1E1E1E)),
//                                        placeholder = painterResource(R.drawable.imageloader),
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .width(100.dp)
                                            .aspectRatio(16f / 9f)
                                            .clip(RoundedCornerShape(8.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(
                                        modifier = Modifier.weight(1f)
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
                                viewModel.loadMore()
                            }
                        }
                }

                if (showBottomSheet.value) {
                    MusicSheet(viewModel, navController, showBottomSheet, selectedItem)
                }


            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusicSheet(
    viewModel: MusicViewModel,
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    selectedItem: VideoItem? = VideoItem("", "", ""),

    ) {
    val context = LocalContext.current
    var showPlayListSheet by remember { mutableStateOf(false) }
    val video: VideoItem = selectedItem ?: return
    ModalBottomSheet(
        onDismissRequest = {
            showBottomSheet.value = false
        },
        sheetState = rememberModalBottomSheetState(),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {

        if (showPlayListSheet) {
            PlayListContent(
                video,
                viewModel,
                onClose = {
                    showPlayListSheet = false
                    showBottomSheet.value = false
                })
        } else {
            MusicSheetContent(
                video, viewModel,
                onclick = { showBottomSheet.value = false },
                showPlayListContent = { showPlayListSheet = true }
            )
        }

    }
}

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MusicSheetContent(
    video: VideoItem,
    viewModel: MusicViewModel,
    onclick: () -> Unit,
    showPlayListContent: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(video.thumbnailUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(R.drawable.imageloader),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                video.title,
                modifier = Modifier
                    .weight(1f),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 11.sp,
                maxLines = 2,
                lineHeight = 15.sp,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.padding(start = 20.dp))

            IconButton(
                onClick = {
                    viewModel.addToDisLiked(video)
                    onclick()
                },
                modifier = Modifier.size(30.dp)
            ) {
                val disLike = state.disLiked.contains(video)
                Icon(
                    imageVector = if (disLike) Icons.Default.ThumbDown else Icons.Default.ThumbDownOffAlt,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground,
                )
            }
            Spacer(Modifier.padding(start = 10.dp))
            IconButton(
                onClick = {
                    viewModel.addToFavourites(video)
                    onclick()
                },
                modifier = Modifier.size(30.dp)
            ) {
                val isFavorite = state.favorites.contains(video)
                Icon(
                    imageVector = if (isFavorite) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                    contentDescription = null,
                )
            }

        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))



        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,

                ) {

                FilledIconButton(
                    onClick = {
                        showPlayListContent()
                    },
                    modifier = Modifier
                        .width(70.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = null,
                    )
                }
                Text(
                    "Save to\n playlist",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )
            }

            Spacer(modifier = Modifier.width(23.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                FilledIconButton(
                    onClick = { onclick() },
                    modifier = Modifier
                        .width(70.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(17.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onBackground
                    ),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                        contentDescription = null,
                    )
                }
                Text(
                    "Save to \n WatchLetter",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )


            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        DownloadAudioButton(context, video.title, video)


        TextButton(
            onClick = {
                viewModel.playNext(video)
                Toast.makeText(context, "Will play next", Toast.LENGTH_SHORT).show()
                onclick()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(53.dp),
            colors = ButtonDefaults.textButtonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.colorScheme.onBackground,
            )
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QueuePlayNext,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Play Next",
                    fontSize = 16.sp,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }


    }
}


@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun PlayListContent(video: VideoItem, viewModel: MusicViewModel, onClose: () -> Unit) {
    val state by viewModel.uiState.collectAsState()
    var openNewDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (openNewDialog) {
        DialogBox(
            onClose = {
                openNewDialog = false
                onClose()
            },
            onCreate = { name, description ->
                val created = viewModel.createPlaylist(name, description)
                if (created != null) {
                    viewModel.addToSpecificPlaylist(created.id, video)
                    viewModel.addRecentPlaylist(created)
                    Toast.makeText(context, "Music added", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Scrollable content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Save to playlist",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 16.sp
                )
                Icon(
                    Icons.Default.Close,
                    null,
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.clickable {
                        onClose()

                    }
                )
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            // Liked music section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.addToFavourites(video)
                        onClose()
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        Toast.makeText(context, "Music added", Toast.LENGTH_SHORT).show()
                        onClose()
                    },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x1CDBD6D2))
                ) {
                    Icon(
                        Icons.Default.Favorite,
                        null,
                        tint = Color.White,
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
                    Text("${state.favorites.size} tracks", color = DarkOnPrimary)
                }
            }

            Spacer(Modifier.height(12.dp))

            // dynamic playlists
            state.playlists.forEach { playlist ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.addToSpecificPlaylist(playlist.id, video)
                            onClose()
                            Toast.makeText(context, "Music added", Toast.LENGTH_SHORT).show()
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
                    Column {
                        Text(
                            playlist.name,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text("${playlist.videos.size} tracks", color = DarkOnPrimary)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }

        // Floating "New" button
        Surface(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top=250.dp , end=16.dp)
                .width(100.dp)
                .height(45.dp)
                .clickable { openNewDialog = true },
            color = MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(24.dp),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, null, tint = DarkOnPrimary, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(6.dp))
                Text("New", color = DarkPrimary, fontSize = 15.sp)
            }
        }
    }
}


