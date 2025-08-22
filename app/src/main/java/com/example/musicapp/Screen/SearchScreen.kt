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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import com.example.musicapp.SharedPreferences.SearchPrefManager
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
                tint = Color.White)
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

                                    Box(
                                        modifier = Modifier
                                            .padding(1.dp)
                                    ) {
                                        IconButton(onClick = { expanded = !expanded }) {
                                            Icon(
                                                Icons.Default.MoreVert,
                                                contentDescription = "More options",
                                                tint = Color.White
                                            )
                                        }
                                        val context = LocalContext.current
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false }
                                        ) {
                                            DropdownMenuItem(
                                                text = { Text("Watch Latter") },
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
    }
}



