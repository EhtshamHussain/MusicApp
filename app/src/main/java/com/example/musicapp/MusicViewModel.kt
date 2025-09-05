package com.example.musicapp


import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp.ExoPlayer.playAudio
import com.example.musicapp.Model.Playlist
import com.example.musicapp.Model.VideoItem
import com.example.musicapp.NewPipe.getAudioUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfoItem
import org.schabi.newpipe.extractor.timeago.patterns.pl
import java.util.UUID

class MusicViewModel(private val context: Context) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private var isSeeking = false // New flag to track seeking

    //navBar Tab
    val selecteTab = mutableStateOf(0)


    //Firebse
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val currentUserId: String? get() = auth.currentUser?.uid


    //    var progress  by   mutableStateOf(0f)
    private val _progress = MutableStateFlow(0f)
    val progress: StateFlow<Float> = _progress
    var exoPlayer: ExoPlayer? = null

    init {
        if (isLoggedIn()) {
            loadListsFromFirestore()
        }

        viewModelScope.launch {
            while (true) {
                exoPlayer?.let {
                    val pos = it.currentPosition.toFloat()
                    val dur = it.duration.takeIf { d -> d > 0 }?.toFloat() ?: 1f
                    _progress.value = pos / dur
                } ?: run {
                    _progress.value = 0f
                }
                delay(100)
            }
        }
    }


    private fun loadListsFromFirestore() {
        if (!isLoggedIn()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userDoc = firestore.collection("users").document(currentUserId!!).get().await()
                if (userDoc.exists()) {
                    val recent = (userDoc.get("recentlyPlayed") as? List<Map<String, Any?>>
                        ?: emptyList()).map { mapToVideoItem(it) }
                    val favs = (userDoc.get("favorites") as? List<Map<String, Any?>>
                        ?: emptyList()).map { mapToVideoItem(it) }
//                    val playlists = (userDoc.get("playList") as? List<Map<String, Any?>>
//                        ?: emptyList()).map { mapToVideoItem(it) }

                    val disLiked = (userDoc.get("disLiked") as? List<Map<String, Any?>>
                        ?: emptyList()).map { mapToVideoItem(it) }

                    val playlists = (userDoc.get("playlists") as? List<Map<String, Any?>>
                        ?: emptyList()).map { mapToPlaylist(it) }.sortedBy { it.name.lowercase() }

                    _uiState.value = _uiState.value.copy(
                        recentlyPlayed = recent,
                        favorites = favs,
                        disLiked = disLiked,
                        playlists = playlists  // add this
                    )

                    _uiState.value = _uiState.value.copy(
                        recentlyPlayed = recent,
                        favorites = favs,
                        playlists = playlists,
                        disLiked = disLiked
                    )

                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Failed to load lists: ${e.message}")
            }
        }

    }

    private fun mapToPlaylist(map: Map<String, Any?>): Playlist {
        return Playlist(
            id = map["id"] as? String ?: UUID.randomUUID().toString(),
            name = map["name"] as? String ?: "",
            description = map["description"] as? String ?: "",
            videos = (map["videos"] as? List<Map<String, Any?>>
                ?: emptyList()).map { mapToVideoItem(it) }
        )
    }

    private fun playlistToMap(playlist: Playlist): Map<String, Any?> {
        return mapOf(
            "id" to playlist.id,
            "name" to playlist.name,
            "description" to playlist.description,
            "videos" to playlist.videos.map { videoItemToMap(it) }
        )
    }

    private fun mapToVideoItem(map: Map<String, Any?>): VideoItem {
        return VideoItem(
            videoId = map["videoId"] as? String ?: "",
            title = map["title"] as? String ?: "",
            thumbnailUrl = map["thumbnailUrl"] as? String
        )
    }


    private fun videoItemToMap(item: VideoItem): Map<String, Any?> {
        return mapOf(
            "videoId" to item.videoId, "title" to item.title, "thumbnailUrl" to item.thumbnailUrl
        )

    }

    private fun saveListToFirestore(fieldName: String, list: List<VideoItem>) {
        if (!isLoggedIn()) return

        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(currentUserId!!)
                val data = mapOf(fieldName to list.map { videoItemToMap(it) })
//                val data = mapOf(fieldName to list)
                userDoc.set(data, SetOptions.merge()).await()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = "Failed to save $fieldName: ${e.message}")
            }
        }
    }


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
                    }, isLoading = false

                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = "Search failed: ${e.message}")
            }
        }
    }

    fun playVideo(
        videoId: String,
        playlist: List<VideoItem> = uiState.value.results,
        index: Int,
        showLoading: Boolean = true
    ) {
        viewModelScope.launch {
            if (showLoading) {
                _uiState.value = _uiState.value.copy(
                    isLoading = true, currentPlaylist = playlist, currentIndex = index
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    currentPlaylist = playlist, currentIndex = index
                )
            }
//            _progress.value = 0f
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
                                isLoading = if (showLoading && !isSeeking && state == Player.STATE_BUFFERING) true else _uiState.value.isLoading
                            )
                        }
                    })
                }
                _uiState.value = _uiState.value.copy(
                    isPlaying = true, currentVideoId = videoId, isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false, error = "Failed to load audio: ${e.message}"
                )
            }
        }
    }

    fun seekTo(position: Long) {
        isSeeking = true // Set flag before seeking
        exoPlayer?.seekTo(position)
//        exoPlayer?.duration?.let { duration ->
//            if (duration > 0) _progress.value = position.toFloat() / duration.toFloat()
//        }
        _uiState.value = _uiState.value.copy(isPlaying = exoPlayer?.isPlaying ?: false)
        viewModelScope.launch {
            // Reset isSeeking after a short delay to account for buffering
            delay(500) // Adjust delay if needed
            isSeeking = false
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
        val currentPlaylist = _uiState.value.currentPlaylist
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
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            result(it.isSuccessful, "SignIn Successfully")
        }

    }


    fun logIn(email: String, password: String, result: (Boolean, String) -> Unit) {
        // Basic validation (optional but useful)
        if (email.isBlank() || password.length < 6) {
            result(false, "Enter valid email & min 6-char password")
            return
        }
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            result(it.isSuccessful, "LogIn Successfully")
        }
    }

    fun isLoggedIn(): Boolean = auth.currentUser != null

    fun currentUserUid(): String? = auth.currentUser?.uid

    fun logOut() = auth.signOut()


    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    @SuppressLint("SuspiciousIndentation")
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addToRecentlyPlayed(video: VideoItem) {
        if (!isLoggedIn()) return
        val currentList = _uiState.value.recentlyPlayed.toMutableList()
        if (!currentList.any { it.videoId == video.videoId }) {
            currentList.add(0, video)

            if (currentList.size > 50) currentList.removeLast()
        }
        _uiState.value = _uiState.value.copy(recentlyPlayed = currentList)

        saveListToFirestore("recentlyPlayed", currentList)

    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addToDisLiked(video: VideoItem) {
        if (!isLoggedIn()) return
        val currentList = _uiState.value.disLiked.toMutableList()
        val favList = _uiState.value.favorites.toMutableList()
        if (favList.any { it.videoId == video.videoId }) {
            favList.removeAll { it.videoId == video.videoId }
        }
        if (currentList.any { it.videoId == video.videoId }) {
            currentList.removeAll { it.videoId == video.videoId }
        } else {
            currentList.add(0, video)
        }
        _uiState.value = _uiState.value.copy(disLiked = currentList, favorites = favList)
        saveListToFirestore("disLiked", currentList)
        saveListToFirestore("favorites", currentList)
    }


    fun addToFavourites(video: VideoItem) {
        if (!isLoggedIn()) return
        val currentList = _uiState.value.favorites.toMutableList()

        val dislike = _uiState.value.disLiked.toMutableList()
        if (dislike.any { it.videoId == video.videoId }) {
            dislike.removeAll { it.videoId == video.videoId }
        }
        if (currentList.any { it.videoId == video.videoId }) {
            currentList.removeAll { it.videoId == video.videoId }
        } else {
            currentList.add(0, video)
        }
        _uiState.value = _uiState.value.copy(favorites = currentList, disLiked = dislike)
        saveListToFirestore("favorites", currentList)

    }


//    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
//    fun addToPlayList(video: VideoItem) {
//        if (!isLoggedIn()) return
//        val currentList = _uiState.value.playList.toMutableList()
//        if (!currentList.any { it.videoId == video.videoId }) {
//            currentList.add(0, video)
//
//            if (currentList.size > 50) currentList.removeLast()
//        }
//        _uiState.value = _uiState.value.copy(playList = currentList)
//        saveListToFirestore("playList", currentList)
//
//    }

    fun createPlaylist(
        name: String,
        description: String,
        videoToAdd: VideoItem? = null
    ): Playlist? {
        if (!isLoggedIn() || name.isBlank() || description.isBlank()) return null// validate not empty

        val currentPlaylists = _uiState.value.playlists.toMutableList()
        if (currentPlaylists.any { it.name == name }) return null // avoid duplicate names

        val newVideos = if (videoToAdd != null) listOf(videoToAdd) else emptyList()
        val newPlaylist = Playlist(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            videos = newVideos
        )
        currentPlaylists.add(newPlaylist)
        currentPlaylists.sortBy { it.name.lowercase() }  // sort alphabetically

        _uiState.value = _uiState.value.copy(playlists = currentPlaylists)

        savePlaylistsToFirestore(currentPlaylists)
        return newPlaylist
    }

    private fun savePlaylistsToFirestore(playlists: List<Playlist>) {
        if (!isLoggedIn()) return
        viewModelScope.launch {
            try {
                val userDoc = firestore.collection("users").document(currentUserId!!)
                val data = mapOf("playlists" to playlists.map { playlistToMap(it) })
                userDoc.set(data, SetOptions.merge()).await()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = "Failed to save playlists: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    fun addToSpecificPlaylist(playlistId: String, video: VideoItem) {
        if (!isLoggedIn()) return

        val currentPlaylists = _uiState.value.playlists.toMutableList()
        val playlistIndex = currentPlaylists.indexOfFirst { it.id == playlistId }
        if (playlistIndex == -1) return

        val updatedVideos = currentPlaylists[playlistIndex].videos.toMutableList()
        if (!updatedVideos.any { it.videoId == video.videoId }) {
            updatedVideos.add(0, video)  // add at top
            if (updatedVideos.size > 50) updatedVideos.removeLast()  // limit to 50 for performance
        }

        val updatedPlaylist = currentPlaylists[playlistIndex].copy(videos = updatedVideos)
        currentPlaylists[playlistIndex] = updatedPlaylist

        _uiState.value = _uiState.value.copy(playlists = currentPlaylists)

        savePlaylistsToFirestore(currentPlaylists)
    }
    fun removeFromSpecificPlaylist(playlistId: String, video: VideoItem) {
        val updatedPlaylists = _uiState.value.playlists.map { playlist ->
            if (playlist.id == playlistId) {
                playlist.copy(videos = playlist.videos - video) // 👈 video remove
            } else playlist
        }

        _uiState.value = _uiState.value.copy(
            playlists = updatedPlaylists
        )
        savePlaylistsToFirestore(updatedPlaylists)
    }

    fun playNext(video: VideoItem) {
        val currentPlaylist = _uiState.value.currentPlaylist.toMutableList()
        val currentIndex = _uiState.value.currentIndex

        currentPlaylist.removeAll { it.videoId == video.videoId }

        // Insert just after current index
        val insertIndex = if (currentIndex >= 0 && currentIndex < currentPlaylist.size) {
            currentIndex + 1
        } else {
            currentPlaylist.size
        }
        currentPlaylist.add(insertIndex, video)

        _uiState.value = _uiState.value.copy(currentPlaylist = currentPlaylist)
    }


}


data class UiState(
    var searchQuery: String = "",
    var results: List<VideoItem> = emptyList(),
    val isPlaying: Boolean = false,
    val currentVideoId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,

    // New lists for your features
    val recentlyPlayed: List<VideoItem> = emptyList(),
    val favorites: List<VideoItem> = emptyList(),
//    val playList: List<VideoItem> = emptyList(),
    val playlists: List<Playlist> = emptyList(),

    val disLiked: List<VideoItem> = emptyList(),


    val currentPlaylist: List<VideoItem> = emptyList(),
    val currentIndex: Int = -1,
)



