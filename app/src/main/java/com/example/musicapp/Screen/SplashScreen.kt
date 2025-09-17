package com.example.musicapp.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.example.musicapp.MusicViewModel
import com.example.musicapp.R
import kotlinx.coroutines.delay
@Composable
fun SplashScreen(navController: NavController, viewModel: MusicViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.musicimage),
            contentDescription = null,
            contentScale = ContentScale.FillHeight,
            modifier = Modifier.fillMaxSize(),
        )
        LaunchedEffect(Unit) {
            delay(3000)
            if (viewModel.isLoggedIn()) {
                navController.navigate("MainScreen") {
                    popUpTo("LogIn") {
                        inclusive = true
                    }
                    popUpTo("SplashScreen"){
                        inclusive=true
                    }
                }
            } else {
                navController.navigate("LogIn") {
                    popUpTo("SplashScreen") {
                        inclusive = true
                    }
                }
            }
        }
    }
}