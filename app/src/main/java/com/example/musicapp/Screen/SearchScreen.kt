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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.bringIntoViewRequester
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
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbDownOffAlt
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.ThumbUpOffAlt
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.SharedPreferences.SearchPrefManager
import com.example.musicapp.VideoItem
import com.example.musicapp.ui.theme.DarkBackground
import com.example.musicapp.ui.theme.DarkOnPrimary
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

    var selectedItem by remember { mutableStateOf<VideoItem?>(null) }

    val showBottomSheet = remember { mutableStateOf(false) }
    val searches = remember { mutableStateListOf<String>() }

    LaunchedEffect(Dispatchers.IO) {
        state.searchQuery = ""
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
                        state.results = emptyList()
                        viewModel.selecteTab.value = 1
                        navController.popBackStack()
//                        navController.navigate("MainScreen")
                    }
                    .size(35.dp)
                    .padding(start = 5.dp, end = 5.dp),
                tint = MaterialTheme.colorScheme.onBackground)
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
                )

            )

        }
        Spacer(modifier = Modifier.height(8.dp))

        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
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
                LazyColumn {
                    items(state.results) { video ->
                        var expanded by remember { mutableStateOf(false) }
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(82.dp)
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
                                        placeholder = painterResource(R.drawable.imageloader),
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

                                    IconButton(onClick = {
                                        showBottomSheet.value = true
                                        selectedItem = video
                                    }) {
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
                }
                if (showBottomSheet.value) {
                    MusicSheet(viewModel, showBottomSheet, selectedItem)
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
    showBottomSheet: MutableState<Boolean>,
    selectedItem: VideoItem? = VideoItem("", "", ""),

    ) {
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
//                Spacer(Modifier.padding(start = 10.dp))


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
                        viewModel.addToPlayList(video)
//                        onclick()
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
                    "Save to\n WatchLetter",
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 5.dp),
                    fontSize = 11.sp,
                    lineHeight = 13.sp
                )

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayListContent(video: VideoItem,viewModel: MusicViewModel, onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(15.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            horizontalArrangement = Arrangement.SpaceBetween

        ) {
            Text(
                "Save 1 music to playlist",
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp,
            )
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.clickable { onClose() }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.addToFavourites(video)
                },
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {  viewModel.addToFavourites(video) },
                Modifier
                    .size(30.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color = DarkOnPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.ThumbUp, // 👈 built-in Like icon
                    contentDescription = "Favorite ",
                    tint = Color.White, // color change kar sakte ho
                    modifier = Modifier.size(35.dp)
                )
            }
            Spacer(Modifier.width(18.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.addToFavourites(video)
                    },
            ) {
                Text(
                    "Liked Music", color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    modifier = Modifier
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PushPin, contentDescription = null,
                        tint = DarkOnPrimary,
                        modifier = Modifier
                            .size(25.dp)
                            .padding(end = 5.dp),
                        )
                Text("Auto playlist", color = DarkOnPrimary)
                }
            }


        }

        Surface(
            modifier = Modifier.width(110.dp)
                .height(40.dp)
                .align(Alignment.End),
            color = MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()
                .padding(5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(24.dp)
                )
                Text("New", color = DarkBackground, fontSize = 20.sp)
            }
        }


    }
}
//@Preview(showBackground = true)
@Composable
fun text() {
    Column(modifier= Modifier.fillMaxSize()
        .background(color = DarkOnPrimary),verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,) {
        Surface(
            modifier = Modifier.width(120.dp)
                .height(50.dp),
            contentColor = MaterialTheme.colorScheme.onBackground,
            shape = RoundedCornerShape(24.dp)
            ) {
            Row(modifier = Modifier.fillMaxWidth()
                .padding(10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically) {
               Icon(imageVector = Icons.Default.Add,
                   contentDescription = null,
                   tint = DarkBackground,
                   modifier = Modifier.size(34.dp)
               )
                Text("New", color = DarkBackground, fontSize = 24.sp)
            }
        }
    }
}