package com.example.musicapp.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.musicapp.Model.NavigationItem
import com.example.musicapp.ui.theme.BottomBarColorYouTubeDark
import com.example.musicapp.ui.theme.DarkOnBackground
import com.example.musicapp.ui.theme.DarkOnPrimary
import com.example.musicapp.ui.theme.MusicAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController , bottomNavController: NavController) {
    Scaffold(
       modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
             title = {
                 Text("HomeScreen" , color = MaterialTheme.colorScheme.onBackground,
                     fontSize = 16.sp)
             },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 5.dp , end = 5.dp),
                actions = {
                    Icon(imageVector = Icons.Default.Search , contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->

        Text("ksdjlfajk" , modifier = Modifier.padding(innerPadding))
    }
}
@Composable
fun DialogBox(
    onClose: () -> Unit,
    onCreate: (String , String) -> Unit,

    ){
    var name by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    Dialog(
        onDismissRequest = {
         onClose.invoke()
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth()
                .height(300.dp),
            color = BottomBarColorYouTubeDark,
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
            ) {
                Text(
                    text = "New Playlist",
                    color = DarkOnBackground,
                    fontWeight = FontWeight.Bold
                )

                TextField(value = name, onValueChange = {name=it},

                    placeholder = {
                        Text("title")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkOnBackground,
                        unfocusedContainerColor = BottomBarColorYouTubeDark
                    ))


                TextField(value = desc, onValueChange = {desc=it},
                    placeholder = {
                        Text("description")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkOnBackground,
                        unfocusedContainerColor = BottomBarColorYouTubeDark
                    ))

                Spacer(Modifier.height(22.dp))
                Row(Modifier.fillMaxWidth().padding(end = 16.dp),
                    horizontalArrangement = Arrangement.End) {
                    OutlinedButton(onClick = {onClose()}) {
                        Text("Cancel",color = DarkOnBackground)
                    }
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            onCreate(name,desc)
                            onClose()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.onBackground
                        )) {
                        Text("Create",color = MaterialTheme.colorScheme.background)
                    }
                }
            }

        }
    }
}