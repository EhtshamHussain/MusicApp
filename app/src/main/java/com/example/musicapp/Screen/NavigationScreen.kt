package com.example.musicapp.Screen

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.Authentication.LogIn
import com.example.musicapp.Authentication.SignUp
import com.example.musicapp.MusicViewModel

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun NavigationScreen(modifier: Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val viewModel = remember { MusicViewModel(context) }
    NavHost(navController = navController, startDestination = "SplashScreen") {


        composable("MenuScreen") {
            MenuScreen(navController , viewModel)
        }
        composable("SplashScreen") {
            SplashScreen(navController, viewModel)
        }


        composable("HomeScreen") {
            MusicPlayerScreen(
                viewModel = viewModel,
                modifier = modifier,
                navController = navController
            )
        }

        composable("PlayerScreen") {
            PlayerScreen(
                viewModel = viewModel,
                navController = navController
            )
        }

        composable("LogIn") {
            LogIn(navController, viewModel)
        }


        composable("SignUp") {
            SignUp(navController, viewModel)
        }





    }

}