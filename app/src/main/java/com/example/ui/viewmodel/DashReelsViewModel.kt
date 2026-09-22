package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DashReelsDatabase
import com.example.data.local.DashReelsRepository
import com.example.data.model.CommentEntity
import com.example.data.model.DramaSeries
import com.example.data.model.Episode
import com.example.data.model.VipStatus
import com.example.data.model.WatchProgressEntity
import com.example.data.sample.SampleDramaData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    REELS,
    LIBRARY
}

data class PlayerUiState(
    val activeDrama: DramaSeries,
    val episodes: List<Episode>,
    val currentEpisodeIndex: Int = 0,
    val isPlaying: Boolean = true,
    val playbackSpeed: Float = 1.0f,
    val isSubtitleVisible: Boolean = true,
    val subtitleLanguage: String = "English",
    val isMuted: Boolean = false,
    val isHd4k: Boolean = true,
    val showEpisodeDrawer: Boolean = false,
    val showCommentsDrawer: Boolean = false,
    val showVipDialog: Boolean = false,
    val showShareToast: String? = null
)

class DashReelsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: DashReelsRepository

    init {
        val db = DashReelsDatabase.getDatabase(application)
        repository = DashReelsRepository(db.dao())
        // Seed initial comments if needed
        viewModelScope.launch {
            SampleDramaData.sampleDramas.forEach { drama ->
                SampleDramaData.initialComments.take(3).forEachIndexed { idx, comment ->
                    repository.addComment(
                        dramaId = drama.id,
                        episodeNumber = 1,
                        author = listOf("Sophia M.", "ReelFan_99", "DramaLover", "Elena_Fan")[idx % 4],
                        text = comment
                    )
                }
            }
        }
    }

    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    val vipStatus: StateFlow<VipStatus> = repository.vipStatus

    val bookmarkedIds: StateFlow<Set<String>> = repository.bookmarkedDramaIds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val watchProgressList: StateFlow<List<WatchProgressEntity>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allDramas: List<DramaSeries> = repository.getAllDramas()

    // Active Player State
    private val initialDrama = allDramas.first()
    private val _playerState = MutableStateFlow(
        PlayerUiState(
            activeDrama = initialDrama,
            episodes = repository.getEpisodes(initialDrama.id),
            currentEpisodeIndex = 0
        )
    )
    val playerState: StateFlow<PlayerUiState> = _playerState.asStateFlow()

    // User liked episodes map: "dramaId_epNum" -> Boolean
    private val _likedEpisodes = MutableStateFlow<Map<String, Boolean>>(
        mapOf("billionaire_bride_1" to true, "shadow_dragon_1" to true)
    )
    val likedEpisodes: StateFlow<Map<String, Boolean>> = _likedEpisodes.asStateFlow()

    private val _selectedDramaForDetail = MutableStateFlow<DramaSeries?>(null)
    val selectedDramaForDetail: StateFlow<DramaSeries?> = _selectedDramaForDetail.asStateFlow()

    fun setTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category
    }

    fun openDramaDetail(drama: DramaSeries) {
        _selectedDramaForDetail.value = drama
    }

    fun closeDramaDetail() {
        _selectedDramaForDetail.value = null
    }

    fun playDrama(drama: DramaSeries, episodeIndex: Int = 0) {
        val episodes = repository.getEpisodes(drama.id)
        val validIndex = episodeIndex.coerceIn(0, (episodes.size - 1).coerceAtLeast(0))
        _playerState.update { current ->
            current.copy(
                activeDrama = drama,
                episodes = episodes,
                currentEpisodeIndex = validIndex,
                isPlaying = true,
                showEpisodeDrawer = false,
                showCommentsDrawer = false
            )
        }
        _currentTab.value = AppTab.REELS
        _selectedDramaForDetail.value = null

        // Save progress to Room
        viewModelScope.launch {
            repository.updateWatchProgress(
                dramaId = drama.id,
                episodeNumber = validIndex + 1,
                progressSec = 0,
                totalSec = episodes.getOrNull(validIndex)?.durationSeconds ?: 90
            )
        }
    }

    fun selectEpisode(index: Int) {
        val episodes = _playerState.value.episodes
        if (index in episodes.indices) {
            _playerState.update {
                it.copy(
                    currentEpisodeIndex = index,
                    isPlaying = true,
                    showEpisodeDrawer = false
                )
            }
            viewModelScope.launch {
                repository.updateWatchProgress(
                    dramaId = _playerState.value.activeDrama.id,
                    episodeNumber = index + 1,
                    progressSec = 0,
                    totalSec = episodes[index].durationSeconds
                )
            }
        }
    }

    fun playNextEpisode() {
        val curIndex = _playerState.value.currentEpisodeIndex
        val maxIndex = _playerState.value.episodes.size - 1
        if (curIndex < maxIndex) {
            selectEpisode(curIndex + 1)
        }
    }

    fun playPreviousEpisode() {
        val curIndex = _playerState.value.currentEpisodeIndex
        if (curIndex > 0) {
            selectEpisode(curIndex - 1)
        }
    }

    fun togglePlayPause() {
        _playerState.update { it.copy(isPlaying = !it.isPlaying) }
    }

    fun setPlaybackSpeed(speed: Float) {
        _playerState.update { it.copy(playbackSpeed = speed) }
    }

    fun toggleSubtitle() {
        _playerState.update { it.copy(isSubtitleVisible = !it.isSubtitleVisible) }
    }

    fun setSubtitleLanguage(lang: String) {
        _playerState.update { it.copy(subtitleLanguage = lang) }
    }

    fun toggleMute() {
        _playerState.update { it.copy(isMuted = !it.isMuted) }
    }

    fun toggleEpisodeDrawer() {
        _playerState.update { it.copy(showEpisodeDrawer = !it.showEpisodeDrawer) }
    }

    fun toggleCommentsDrawer() {
        _playerState.update { it.copy(showCommentsDrawer = !it.showCommentsDrawer) }
    }

    fun showVipDialog(show: Boolean) {
        _playerState.update { it.copy(showVipDialog = show) }
    }

    fun toggleLikeCurrent() {
        val dramaId = _playerState.value.activeDrama.id
        val epNum = _playerState.value.currentEpisodeIndex + 1
        val key = "${dramaId}_$epNum"
        _likedEpisodes.update { current ->
            val isLiked = current[key] == true
            current + (key to !isLiked)
        }
    }

    fun toggleBookmark(dramaId: String) {
        viewModelScope.launch {
            val isBookmarked = bookmarkedIds.value.contains(dramaId)
            repository.setBookmark(dramaId, !isBookmarked)
        }
    }

    fun getCommentsForCurrentEpisode(): StateFlow<List<CommentEntity>> {
        val dramaId = _playerState.value.activeDrama.id
        val ep = _playerState.value.currentEpisodeIndex + 1
        return repository.getComments(dramaId, ep)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun postComment(text: String) {
        if (text.isBlank()) return
        val dramaId = _playerState.value.activeDrama.id
        val ep = _playerState.value.currentEpisodeIndex + 1
        viewModelScope.launch {
            repository.addComment(dramaId, ep, "VIP Rebel ✨", text.trim())
        }
    }

    fun likeComment(commentId: Long) {
        viewModelScope.launch {
            repository.likeComment(commentId)
        }
    }

    fun claimDailyBonus(): Int {
        return repository.claimDailyBonus()
    }
}
