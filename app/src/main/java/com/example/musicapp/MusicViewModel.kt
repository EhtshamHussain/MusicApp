package com.example.musicapp


import android.content.Context
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.ExoPlayer.playAudio
import com.example.musicapp.NewPipe.getAudioUrl
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem

class MusicViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())

    val uiState: StateFlow<UiState> = _uiState


    /**
     * Firebase Authentication
     */

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    var exoPlayer: ExoPlayer? = null



    fun updateSearch(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query, isLoading = true)
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
                    },
                    isLoading = false

                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Search failed: ${e.message}")
            }
        }
    }

    fun playVideo(videoId: String, playlist: List<VideoItem> = uiState.value.results, index: Int) {
//        currentPlaylist = playlist
//        currentIndex = index

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true,
                currentPlaylist = playlist,
                currentIndex = index
                )
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
            isLoading = false,
            results = emptyList(),
            currentPlaylist = emptyList(),  // Optional: reset
            currentIndex = -1
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
        val currentIndex = _uiState.value.currentIndex
        val  currentPlaylist = _uiState.value.currentPlaylist
        if (currentIndex < currentPlaylist.size - 1) {
            playVideo(currentPlaylist[currentIndex + 1].videoId, currentPlaylist, currentIndex + 1)
        }
    }

    fun playPrevious() {
        val currentIndex = _uiState.value.currentIndex
        val currentPlaylist = _uiState.value.currentPlaylist
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

    fun signUp(email: String, password: String, result: (Boolean, String) -> Unit) {
        // Basic validation (optional but useful)
        if (email.isBlank() || password.length < 6) {
            result(false, "Enter valid email & min 6-char password")
            return
        }
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result(it.isSuccessful, "SignIn Successfully")
            }

    }


    fun logIn(email: String, password: String, result: (Boolean, String) -> Unit) {
        // Basic validation (optional but useful)
        if (email.isBlank() || password.length < 6) {
            result(false, "Enter valid email & min 6-char password")
            return
        }
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                result(it.isSuccessful, "LogIn Successfully")
            }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUserUid(): String? = auth.currentUser?.uid

    fun logOut() = auth.signOut()


    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun addToRecentlyPlayed(video: VideoItem) {
        val currentList = _uiState.value.recentlyPlayed.toMutableList()
        if (!currentList.any { it.videoId == video.videoId }){
            currentList.add(0,video)
        }
        _uiState.value = _uiState.value.copy(recentlyPlayed = currentList)

    }


    fun addToFavourites(video: VideoItem) {
        val currentList = _uiState.value.favorites.toMutableList()
        if (currentList.any { it.videoId == video.videoId }){
            currentList.removeAll{it.videoId == video.videoId}
        }else{
            currentList.add(0,video)
        }
        _uiState.value = _uiState.value.copy(favorites = currentList)

    }



    fun addToPlayList(video: VideoItem) {
        val currentList = _uiState.value.playList.toMutableList()
        if (!currentList.any { it.videoId == video.videoId }){
            currentList.add(0,video)
        }
        _uiState.value = _uiState.value.copy(playList = currentList)

    }


}

data class UiState(
    val searchQuery: String = "",
    val results: List<VideoItem> = emptyList(),
    val isPlaying: Boolean = false,
    val currentVideoId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    // New lists for your features
    val recentlyPlayed: List<VideoItem> = emptyList(),
    val favorites: List<VideoItem> = emptyList(),
    val playList: List<VideoItem> = emptyList(),

    val currentPlaylist: List<VideoItem> = emptyList(),
    val currentIndex: Int = -1
)

data class VideoItem(
    val videoId: String,
    val title: String,
    val thumbnailUrl: String? = null,
)


