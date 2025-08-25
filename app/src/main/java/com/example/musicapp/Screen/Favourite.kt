package com.example.musicapp.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicapp.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Favourite(
    navController: NavController,
    libraryNavController: NavController,
    viewModel: MusicViewModel
) {

    val state by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {},
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier
                            .clickable { libraryNavController.popBackStack() }
                            .padding(end = 16.dp)
                    )
                },
                actions = {
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
                .background(color = Color.Black),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(state.favorites) {
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
        }
    }


}