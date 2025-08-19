package com.example.musicapp.Screen

import android.window.SplashScreen
import com.example.musicapp.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.example.musicapp.MusicViewModel
import kotlinx.coroutines.Delay
import kotlinx.coroutines.delay
import java.nio.file.WatchEvent


@Composable
fun SplashScreen(navController: NavController , viewModel: MusicViewModel) {

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {


            Image(
                painter = painterResource(R.drawable.musicimage),
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

        LaunchedEffect(Unit) {
            delay(3000)
            if(viewModel.isLoggedIn()){
                navController.navigate("MenuScreen"){
                    popUpTo("SplashScreen") {
                        inclusive = true
                    }
                }
            }else{
                navController.navigate("LogIn"){
                    popUpTo("SplashScreen") {
                        inclusive = true
                    }
                }
            }
        }


    }
    
}