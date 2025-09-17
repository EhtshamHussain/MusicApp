package com.example.musicapp.Screen

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.musicapp.EnumRemoveList.RemoveType
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.MusicViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    libraryNavController: NavController,
    viewModel: MusicViewModel
) {
    val bottomSheet = remember {mutableStateOf(false)  }
    var video by remember { mutableStateOf<VideoItem?>(null) }
    val state by viewModel.uiState.collectAsState()
    Scaffold(
        modifier = Modifier.fillMaxWidth(),
        topBar = {
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = {
                    Text("History ", color = MaterialTheme.colorScheme.onBackground)
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .clickable { libraryNavController.popBackStack() }
                            .padding(end = 16.dp)
                    )
                },

                actions = {
                    Icon(imageVector = Icons.Default.Search , contentDescription = null ,
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(40.dp)
                            .padding(end = 10.dp)
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
                .background(color = MaterialTheme.colorScheme.background),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LazyColumn(modifier = Modifier.padding(innerPadding)) {
                items(state.recentlyPlayed) {
                    PlayingItem(
                        it.videoId,
                        it.title,
                        it.thumbnailUrl ?: "",
                        onClick = {
                            val index = state.recentlyPlayed.indexOf(it)
                            viewModel.playVideo(it.videoId, state.recentlyPlayed, index)
                            navController.navigate("PlayerScreen")
                        },
                        onOpen = {
                            video = it
                            bottomSheet.value = true}
                    )
                }
            }
    }
        if(bottomSheet.value){
            BottomSheet(
                selectedItem = video,
                viewModel = viewModel,
                removeType = RemoveType.HISTORY,
                onDismiss = { bottomSheet.value = false}
            )
        }
}
}


