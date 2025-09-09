package com.example.musicapp.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.musicapp.MusicViewModel
import com.example.musicapp.Model.navigationItem
import com.example.musicapp.ui.theme.BottomBarColorYouTubeDark

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainScreen(navController: NavController,  viewModel: MusicViewModel) {
    val selectedIndex = viewModel.selecteTab
    val bottomNavController = rememberNavController()
//    val bottomNavController = bottomNavController

    val navBackStackEntry = bottomNavController.currentBackStackEntryAsState()
    val currentDestination  = navBackStackEntry.value?.destination?.route

    LaunchedEffect(currentDestination ) {

        when (currentDestination) {
            "HomeScreen" -> viewModel.selecteTab.value = 0
            "MenuScreen" -> viewModel.selecteTab.value = 1
            "SettingScreen" -> viewModel.selecteTab.value = 2
        }
    }
    Scaffold(
        bottomBar = {
            Column {
                val state = viewModel.uiState.collectAsState().value
                val currentRoute = navController.currentDestination?.route
                if (state.currentPlaylist.isNotEmpty() && currentRoute != "PlayerScreen") {
                    MiniPlayer(viewModel, onExpand = { navController.navigate("PlayerScreen") })
                }


                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    navigationItem.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = selectedIndex.value == index,
                            onClick = {
                                selectedIndex.value = index
                                when (index) {
                                    0 -> bottomNavController.navigate("HomeScreen") {
                                        popUpTo(bottomNavController.graph.startDestinationId) {
                                            saveState = true
                                            inclusive = false
                                        }
                                        restoreState = true
                                        launchSingleTop = true
                                    }

                                    1 -> bottomNavController.navigate("MenuScreen") {
                                        popUpTo(bottomNavController.graph.startDestinationId) {
                                            saveState = true
                                            inclusive = false
                                        }
                                        restoreState = false
                                        launchSingleTop = true
                                    }

                                    2 -> bottomNavController.navigate("SettingScreen") {
                                        popUpTo(bottomNavController.graph.startDestinationId) {
                                            saveState = true
                                            inclusive = false
                                        }
                                        restoreState = true
                                        launchSingleTop = true
                                    }


                                }

                            },
                            icon = {
                                Icon(imageVector = item.icon, contentDescription = item.title)
                            },
                            label = {
                                Text(item.title)
                            },
                            alwaysShowLabel = true,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.onBackground,
                                unselectedIconColor = Color.Gray,
                                selectedTextColor = MaterialTheme.colorScheme.onBackground,
                                unselectedTextColor = Color.Gray,
                                indicatorColor = Color(0x902E4144)
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "HomeScreen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("HomeScreen") { HomeScreen(navController , bottomNavController,viewModel) }
            composable("MenuScreen") { MenuScreen(navController, bottomNavController, viewModel) }

            composable("SettingScreen") { SettingScreen() }
            composable("HistoryScreen") { HistoryScreen(navController ,bottomNavController , viewModel) }
            composable("Favourite") { Favourite(navController ,bottomNavController , viewModel) }

            composable(
                route = "PlaylistDetail/{playlistId}",
                arguments = listOf(navArgument("playlistId"){type = NavType.StringType})


            ) { navBackStackEntry->
                val playlistId = navBackStackEntry.arguments?.getString("playlistId") ?: ""
                PlaylistDetailScreen(
                    navController = navController,
                    bottomNavController = bottomNavController,
                    viewModel = viewModel,
                    playlistId = playlistId
                )
            }


        }

    }
}
