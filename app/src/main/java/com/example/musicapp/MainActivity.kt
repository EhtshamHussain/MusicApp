package com.example.musicapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.musicapp.DownloaderImpl.DownloaderImpl
import com.example.musicapp.Screen.NavigationScreen
import com.example.musicapp.ui.theme.MusicAppTheme
import org.schabi.newpipe.extractor.NewPipe


class MainActivity : ComponentActivity() {
    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        NewPipe.init(DownloaderImpl.init(null))
        setContent {
            MusicAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    MusicPlayerScreen(
//                        viewModel = MusicViewModel(this),
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    NavigationScreen(modifier = Modifier.padding(innerPadding))
//                    PlayerScreen()
                }
            }

        }
    }
}


//AIzaSyDBR_TEw8EZDnqyjTc4QSUxK4Zyz86YLPk