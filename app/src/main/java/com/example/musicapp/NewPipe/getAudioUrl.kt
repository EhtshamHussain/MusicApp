package com.example.musicapp.NewPipe

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.schabi.newpipe.extractor.stream.StreamInfo

suspend fun getAudioUrl(videoId: String): String {
    val url = "https://www.youtube.com/watch?v=$videoId"
    val streamInfo = withContext(Dispatchers.IO) {
        StreamInfo.getInfo(url)
    }
    val audioStream = streamInfo.audioStreams.firstOrNull()
    return audioStream?.url ?: throw Exception("No audio stream found")
}