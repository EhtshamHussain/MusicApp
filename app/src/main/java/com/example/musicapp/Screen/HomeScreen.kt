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
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import com.example.musicapp.ui.theme.BottomBarColorYouTubeDark
import com.example.musicapp.ui.theme.DarkOnBackground
import com.example.musicapp.ui.theme.DarkOnPrimary
import com.example.musicapp.ui.theme.MusicAppTheme

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier = Modifier
        .fillMaxSize()
        .background(color = MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var state by remember { mutableStateOf(true) }
        if(state) {
            DialogBox(
                onClose = { state = false }

            )
        }
    }

}
@Composable
fun DialogBox(onClose: () -> Unit){
    var state by remember { mutableStateOf("") }
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

                TextField(value = state, onValueChange = {state=it},

                    placeholder = {
                        Text("title")
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = DarkOnBackground,
                        unfocusedContainerColor = BottomBarColorYouTubeDark
                    ))


                TextField(value = state, onValueChange = {state=it},
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
                        onClick = {onClose()},
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