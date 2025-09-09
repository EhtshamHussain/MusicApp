package com.example.musicapp.SingerData

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
//
//data class Singer(
//    val name : String ,
//    val imageUrl :String,
//)
//
//val list = listOf(
//Singer(name = "Taylor Swift", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/b/b1/Taylor_Swift_at_the_2019_American_Music_Awards.png"),
//Singer(name= "Ariana Grande", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/3/37/Ariana_Grande_at_the_2018_MTV_Video_Music_Awards.jpg"),
//Singer(name= "Billie Eilish", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/2/2a/Billie_Eilish_2019_by_Glenn_Francis.jpg"),
//Singer(name= "Ed Sheeran", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/c/c1/Ed_Sheeran-6886_%28cropped%29.jpg"),
//Singer(name= "The Weeknd", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/0a/The_Weeknd_2015.jpg"),
//Singer(name= "Justin Bieber", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/72/Justin_Bieber_2015.jpg"),
//Singer(name= "Dua Lipa", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/1/1a/Dua_Lipa_2019.jpg"),
//Singer(name= "Adele", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/7c/Adele_2016.jpg"),
//Singer(name= "Beyoncé", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/1/17/Beyonc%C3%A9_at_The_Lion_King_European_Premiere_2019.png"),
//Singer(name= "Drake", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/2/28/Drake_July_2016.jpg"),
//Singer(name= "Lady Gaga", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/8d/Lady_Gaga_at_Joker_Folie_%C3%A0_Deux_premiere_2024.jpg"),
//Singer(name= "Michael Jackson", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/3/31/Michael_Jackson_in_1988.jpg"),
//Singer(name= "Freddie Mercury", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/e/ef/Freddie_Mercury_performing_in_1977.jpg"),
//Singer(name= "Aretha Franklin", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/5/5f/Aretha_Franklin_1968.jpg"),
//Singer(name= "Elvis Presley", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/80/Elvis_Presley_Jailhouse_Rock.jpg"),
//Singer(name= "Frank Sinatra", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/a/af/Frank_Sinatra_%2757.jpg"),
//Singer(name= "Stevie Wonder", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/4/48/Stevie_Wonder_1973.JPG"),
//Singer(name= "Mariah Carey", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/6/68/Mariah_Carey_in_1998.jpg"),
//Singer(name= "Whitney Houston", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/3/3f/Whitney_Houston_%28cropped%29.jpg"),
//Singer(name= "Prince", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/0f/Prince_at_Coachella_2008.jpg"),
//Singer(name= "Lata Mangeshkar", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/5/58/Lata_Mangeshkar_-_cropped.jpg"),
//Singer(name= "Bob Marley", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/2/24/Bob_Marley_1980.jpg"),
//Singer(name= "John Lennon", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/8a/John_Lennon_1969_%28cropped%29.jpg"),
//Singer(name= "Madonna", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/9/98/Madonna_Rebel_Heart_Tour_2015_-_Stockholm_%2823051487179%29_%28cropped%29.jpg"),
//Singer(name= "Rihanna", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/6/67/Rihanna_in_2012.jpg"),
//Singer(name= "Bruno Mars", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/88/Bruno_Mars_2010.jpg"),
//Singer(name= "David Bowie", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/02/David_Bowie_1974.jpg"),
//Singer(name= "Celine Dion", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/3/3e/Celine_Dion_1997.jpg"),
//Singer(name= "Sam Cooke", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/4/46/Sam_Cooke_2.jpg"),
//Singer(name= "Ray Charles", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/9/9e/Ray_Charles_%281960%29_%28cropped%29.jpg"),
//Singer(name= "Shakira", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/9/9c/Shakira_in_2011.jpg"),
//Singer(name= "Alicia Keys", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/7b/Alicia_Keys_2013.jpg"),
//Singer(name= "Post Malone", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/5/58/Post_Malone_2018.jpg"),
//Singer(name= "Chris Brown", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/0e/Chris_Brown_5_%28cropped%29.jpg"),
//Singer(name= "Katy Perry", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/9/95/Katy_Perry_June_2017.jpg"),
//Singer(name= "Nicki Minaj", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/07/Nicki_Minaj_2011_%28cropped%29.jpg"),
//Singer(name= "Arijit Singh", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/76/Arijit_Singh_2016.jpg"),
//Singer(name= "Atif Aslam", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/2/28/Atif_Aslam_2016.jpg"),
//Singer(name= "Shreya Ghoshal", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/1/1e/Shreya_Ghoshal_2015.jpg"),
//Singer(name= "Enrique Iglesias", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/5/5b/Enrique_Iglesias_2011%2C_2.jpg"),
//Singer(name= "Jennifer Lopez", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/71/Jennifer_Lopez_2012_%28cropped%29.jpg"),
//Singer(name= "SZA", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/4/4e/SZA_2017_%28cropped%29.jpg"),
//Singer(name= "Selena Gomez", imageUrl="https://upload.wikimedia.org/wikipedia/commons/6/60/Selena_Gomez_2016.jpg"),
//Singer(name= "Miley Cyrus", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/3/34/Miley_Cyrus_2015_%28cropped%29.jpg"),
//Singer(name= "Kendrick Lamar", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/88/Kendrick_Lamar_2018_%28cropped%29.jpg"),
//Singer(name= "Amy Winehouse", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/81/Amy_Winehouse_2007.jpg"),
//Singer(name= "Tina Turner", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/8/86/Tina_Turner_1985.jpg"),
//Singer(name= "James Brown", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/7e/James_Brown_1969.jpg"),
//Singer(name= "Luciano Pavarotti", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/0/0c/Luciano_Pavarotti_2002.jpg"),
//Singer(name= "Celia Cruz", imageUrl= "https://upload.wikimedia.org/wikipedia/commons/7/74/Celia_Cruz_1976.jpg")
//)

data class Singer(val name: String, val imageUrl: String)

val singers = listOf(
    Singer("Taylor Swift", "https://i.scdn.co/image/ab6761610000e5eb2c2a5b0b8c6e25f1e7d63a3a"),
    Singer("Ariana Grande", "https://i.scdn.co/image/ab6761610000e5ebc2f1d6a2c9e7b5f4e7f8d3a2"),
    Singer("Billie Eilish", "https://i.scdn.co/image/ab6761610000e5eb0b2c6a63d8f9c7e8a3c5f0a1"),
    Singer("Ed Sheeran", "https://i.scdn.co/image/ab6761610000e5ebd56f0d3b2a5e3db7a62a8a7e"),
    Singer("The Weeknd", "https://i.scdn.co/image/ab6761610000e5eb0d1f2e3f4a5b6c7d8e9f0g1h"),
    Singer("Justin Bieber", "https://i.scdn.co/image/ab6761610000e5eb3b4f09a1e9c7e1b4c5b07d91"),
    Singer("Dua Lipa", "https://i.scdn.co/image/ab6761610000e5eb5c1f2f3f4a5b6c7d8e9f0a1b"),
    Singer("Adele", "https://i.scdn.co/image/ab6761610000e5eb7f6caa9c82a1c8e8e5e7f0a3"),
    Singer("Beyoncé", "https://i.scdn.co/image/ab6761610000e5ebb5fba6a353ca2ce4b993a2d6"),
    Singer("Drake", "https://i.scdn.co/image/ab6761610000e5eb9f8f6ed427d6a8f8b0cb9f3b")
)
@Composable
fun abc(modifier: Modifier = Modifier) {
    LazyColumn(modifier= Modifier.fillMaxSize()
        .padding(22.dp)) {
        items(singers){
            AsyncImage(it.imageUrl, contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
            contentScale = ContentScale.Fit,
                )
            Spacer(modifier.height(16.dp))
            Text(it.name , fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
    }
}