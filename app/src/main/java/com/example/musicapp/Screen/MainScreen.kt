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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.musicapp.MusicViewModel
import com.example.musicapp.Model.navigationItem

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun MainScreen(navController: NavController, viewModel: MusicViewModel) {
    val selectedIndex = viewModel.selecteTab
    val bottomNavController = rememberNavController()

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
            NavigationBar(containerColor = Color(0xFF1D1C1C)) {
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
                            selectedIconColor = Color.White,
                            unselectedIconColor = Color.Gray,
                            selectedTextColor = Color.White,
                            unselectedTextColor = Color.Gray,
                            indicatorColor = Color(0x902E4144)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = "HomeScreen",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("HomeScreen") { HomeScreen() }
            composable("MenuScreen") { MenuScreen(navController, bottomNavController, viewModel) }
            composable("PlayList") { PlayList(navController, bottomNavController, viewModel) }
            composable("SettingScreen") { SettingScreen() }
        }

    }
}
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.BottomAppBar
//import androidx.compose.material3.Icon
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.NavigationBarItemDefaults
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavController
//import com.example.musicapp.MusicViewModel
////import androidx.compose.runtime.getValue
//import androidx.compose.ui.graphics.Color
//import androidx.navigation.NavHost
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.navigation
//import androidx.navigation.compose.rememberNavController
//import com.example.musicapp.Model.navigationItem
//
//@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
//@Composable
//fun MainScreen(navController: NavController,viewModel: MusicViewModel) {
//    var selectedIndex = viewModel.selecteTab
//    val libraryNavController = rememberNavController()
//    Scaffold(
//        bottomBar = {
//            NavigationBar(containerColor = Color(0xFF1D1C1C)) {
//                navigationItem.forEachIndexed { index, item ->
//                    NavigationBarItem(
//                        selected = selectedIndex.value == index,
//                        onClick = {
//                            selectedIndex.value = index
//                            when (index) {
//                                0 -> navController.navigate("HomeScreen") {
//                                    popUpTo(navController.graph.startDestinationId){
//                                        inclusive = false
//                                    }
//                                    launchSingleTop = true
//                                }
//
//                                1 -> navController.navigate("Library/MenuScreen") {
//                                    popUpTo(libraryNavController.graph.startDestinationId){
//                                        inclusive = false
//                                    }
//                                    launchSingleTop = true
//                                }
//
//                                2 -> navController.navigate("SettingScreen") {
//                                    popUpTo(navController.graph.startDestinationId){
//                                        inclusive = false
//                                    }
//                                    launchSingleTop = true
//                                }
//
//                            }
//
//                        },
//                        icon = {
//                            Icon(imageVector = item.icon, contentDescription = item.title)
//                        },
//                        label = {
//                            Text(
//                                item.title,
//                            )
//                        },
//                        alwaysShowLabel = true,
////                        colors = NavigationBarItemDefaults.colors(
////                            selectedIconColor = Color.White,
////                            selectedTextColor = Color.White,
////                            unselectedTextColor = Color.Gray,
////                            indicatorColor = Color.Transparent,
////                            unselectedIconColor = Color.Gray
////                        )
//                        colors = NavigationBarItemDefaults.colors(
//                            selectedIconColor = Color.White,
//                            unselectedIconColor = Color.Gray,
//                            selectedTextColor = Color.White,
//                            unselectedTextColor = Color.Gray,
//                            indicatorColor = Color(0x902E4144)
//
//                        )
//                    )
//
//                }
//            }
//        }
//    ) { innerPadding ->
//        Column(modifier = Modifier.padding(innerPadding)) {
//            NavHost(
//                navController = libraryNavController,
//                startDestination = "MenuScreen"
//            ) {
//                composable("HomeScreen") {
//                    HomeScreen()
//                }
//                navigation(startDestination = "MenuScreen", route = "Library") {
//                    composable("MenuScreen") {
//                        MenuScreen(
//                            navController = navController,
//                            libraryNavController = libraryNavController,
//                            viewModel = viewModel
//                        )
//                    }
//                    composable("PlayList") {
//                        PlayList(
//                            navController = navController,
//                            libraryNavController = libraryNavController,
//                            viewModel = viewModel
//                        )
//                    }
//                }
//                composable("SettingScreen") {
//                    SettingScreen()
//                }
//            }
//        }
//    }
//}