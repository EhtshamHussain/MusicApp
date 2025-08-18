package com.example.musicapp


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.ExoPlayer.playAudio
import com.example.musicapp.NewPipe.getAudioUrl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem
class MusicViewModel(private val context: Context) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    var exoPlayer: ExoPlayer? = null
    var currentPlaylist: List<VideoItem> = emptyList()
    var currentIndex: Int = -1

    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val extractor = ServiceList.YouTube.getSearchExtractor("$query music")
                extractor.fetchPage()
                val items = extractor.initialPage.items.filterIsInstance<StreamInfoItem>()
                _uiState.value = _uiState.value.copy(
                    results = items.map {
                        VideoItem(
                            videoId = extractVideoId(it.url),
                            title = it.name,
                            thumbnailUrl = it.thumbnails.firstOrNull()?.url.toString()
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Search failed: ${e.message}")
            }
        }
    }

    fun playVideo(videoId: String, playlist: List<VideoItem> = uiState.value.results, index: Int) {
        currentPlaylist = playlist
        currentIndex = index

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val audioUrl = getAudioUrl(videoId)
                if (exoPlayer == null) {
                    exoPlayer = ExoPlayer.Builder(context).build()
                }

                exoPlayer?.apply {
                    stop()
                    clearMediaItems()
                    val mediaItem = MediaItem.fromUri(audioUrl)
                    setMediaItem(mediaItem)
                    prepare()
                    play()
                    addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(state: Int) {
                            if (state == Player.STATE_ENDED) {
                                playNext()
                            }
                            _uiState.value = _uiState.value.copy(
                                isPlaying = isPlaying,
                                currentVideoId = videoId,
                                isLoading = state == Player.STATE_BUFFERING
                            )
                        }
                    })
                }
                _uiState.value = _uiState.value.copy(
                    isPlaying = true,
                    currentVideoId = videoId,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Failed to load audio: ${e.message}"
                )
            }
        }
    }

    private fun stopCurrentPlayer() {
        exoPlayer?.release()
        exoPlayer = null
        _uiState.value = _uiState.value.copy(
            isPlaying = false,
            currentVideoId = null,
            isLoading = false
        )
    }

    fun pause() {
        exoPlayer?.pause()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun resume() {
        exoPlayer?.play()
        _uiState.value = _uiState.value.copy(isPlaying = true)
    }

    fun playNext() {
        if (currentIndex < currentPlaylist.size - 1) {
            playVideo(currentPlaylist[currentIndex + 1].videoId, currentPlaylist, currentIndex + 1)
        }
    }

    fun playPrevious() {
        if (currentIndex > 0) {
            playVideo(currentPlaylist[currentIndex - 1].videoId, currentPlaylist, currentIndex - 1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopCurrentPlayer()
    }

    fun extractVideoId(url: String): String {
        val regex = Regex("v=([a-zA-Z0-9_-]+)")
        return regex.find(url)?.groups?.get(1)?.value
            ?: throw IllegalArgumentException("Invalid YouTube URL: $url")
    }

    fun playVideo(videoUrl: String) {
        viewModelScope.launch {
            val audioUrl = getAudioUrl(videoUrl)
            playAudio(context, audioUrl)
        }
    }
}

data class UiState(
    val searchQuery: String = "",
    val results: List<VideoItem> = emptyList(),
    val isPlaying: Boolean = false,
    val currentVideoId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

data class VideoItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String? = null
)


// videoId ab URL hai

//fun playAudio(context: Context, audioUrl: String): ExoPlayer {
//    val player = ExoPlayer.Builder(context).build()
//    val mediaItem = MediaItem.fromUri(audioUrl)
//    player.setMediaItem(mediaItem)
//    player.prepare()
//    player.play()
//    return player
//}
//
//suspend fun getAudioUrl(videoUrl: String): String {
//    val streamInfo = StreamInfo.getInfo(videoUrl)
//    val audioStream = streamInfo.audioStreams.firstOrNull()
//    return audioStream?.url ?: throw Exception("No audio stream found")
//}


//
//class MusicViewModel(private val context: Context) : ViewModel() {
//    private val _uiState = MutableStateFlow(UiState())
//    val uiState: StateFlow<UiState> = _uiState
//
//    private var exoPlayer: ExoPlayer? = null // Single player
//    private var currentPlaylist: List<VideoItem> = emptyList() // For next/previous
//    private var currentIndex: Int = -1 // Current playing index
//
//    // ... updateSearch same rahega
//
//    fun playVideo(videoId: String, playlist: List<VideoItem> = uiState.value.results, index: Int) {
//        currentPlaylist = playlist
//        currentIndex = index
//
//        viewModelScope.launch {
//            stopCurrentPlayer() // Pehle wala stop
//            val audioUrl = getAudioUrl(videoId)
//            exoPlayer = ExoPlayer.Builder(context).build().apply {
//                val mediaItem = MediaItem.fromUri(audioUrl)
//                setMediaItem(mediaItem)
//                prepare()
//                play()
//                addListener(object : Player.Listener {
//                    override fun onPlaybackStateChanged(state: Int) {
//                        if (state == Player.STATE_ENDED) {
//                            playNext() // Song khatam ho to next play
//                        }
//                    }
//                })
//            }
//        }
//    }
//
//    private fun stopCurrentPlayer() {
//        exoPlayer?.release()
//        exoPlayer = null
//    }
//
//    fun pause() {
//        exoPlayer?.pause()
//    }
//
//    fun resume() {
//        exoPlayer?.play()
//    }
//
//    fun playNext() {
//        if (currentIndex < currentPlaylist.size - 1) {
//            playVideo(currentPlaylist[currentIndex + 1].videoId, currentPlaylist, currentIndex + 1)
//        }
//    }
//
//    fun playPrevious() {
//        if (currentIndex > 0) {
//            playVideo(currentPlaylist[currentIndex - 1].videoId, currentPlaylist, currentIndex - 1)
//        }
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        stopCurrentPlayer() // ViewModel clear hone pe release
//    }
//}
