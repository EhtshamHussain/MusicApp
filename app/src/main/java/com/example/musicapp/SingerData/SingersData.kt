package com.example.musicapp.SingerData

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp.R

data class Singer(val name: String, val imageUrl: Int)

val singers = listOf(

    Singer("Rahat Fateh Ali Khan", R.drawable.rahet),
    Singer("Atif Aslam", R.drawable.atif),
    Singer( "Nusrat Fateh Ali Khan", R.drawable.nusrut),
    Singer(" Amjad Sabri", R.drawable.amjad),
    Singer( "Shafqat Amanat Ali", R.drawable.shafqat),

    Singer("Arijit Singh", R.drawable.argitsingh),
    Singer("Ali Zafar", R.drawable.alizafar),
)

@Composable
fun topSingersRow(onSingerClick: (Singer) -> Unit){
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
    ) {
        items(singers) { singer ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(18.dp)
                    .clickable{onSingerClick(singer)}
            ) {
                Box(
                    modifier = Modifier
                        .size(220.dp)

                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Image(
                        painter = painterResource(id = singer.imageUrl),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = singer.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

    }
}