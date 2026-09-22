package com.example.data.local

import com.example.data.model.BookmarkEntity
import com.example.data.model.CommentEntity
import com.example.data.model.DramaSeries
import com.example.data.model.Episode
import com.example.data.model.VipStatus
import com.example.data.model.WatchProgressEntity
import com.example.data.sample.SampleDramaData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class DashReelsRepository(private val dao: DashReelsDao) {

    private val _vipStatus = MutableStateFlow(
        VipStatus(
            isPremiumFreeUnlocked = true,
            planName = "DashReels VIP Lifetime Free",
            coinsBalance = 15000,
            dailyCheckinClaimed = false,
            ultraHdUnlocked = true,
            noAdsEnabled = true,
            downloadUnlocked = true
        )
    )
    val vipStatus = _vipStatus.asStateFlow()

    fun getAllDramas(): List<DramaSeries> = SampleDramaData.sampleDramas

    fun getDramaById(id: String): DramaSeries? {
        return SampleDramaData.sampleDramas.find { it.id == id }
    }

    fun getEpisodes(dramaId: String): List<Episode> {
        return SampleDramaData.getEpisodesForDrama(dramaId)
    }

    // Room Bookmarks
    val bookmarkedDramaIds: Flow<Set<String>> = dao.getAllBookmarks().map { list ->
        list.map { it.dramaId }.toSet()
    }

    suspend fun toggleBookmark(dramaId: String) {
        val all = dao.getAllBookmarks()
        // Check if exists
        dao.isBookmarked(dramaId).collect { isBookmarked ->
            if (isBookmarked) {
                dao.removeBookmark(dramaId)
            } else {
                dao.addBookmark(BookmarkEntity(dramaId = dramaId))
            }
        }
    }

    suspend fun setBookmark(dramaId: String, bookmarked: Boolean) {
        if (bookmarked) {
            dao.addBookmark(BookmarkEntity(dramaId = dramaId))
        } else {
            dao.removeBookmark(dramaId)
        }
    }

    // Room Watch Progress
    val allProgress: Flow<List<WatchProgressEntity>> = dao.getAllProgress()

    suspend fun updateWatchProgress(dramaId: String, episodeNumber: Int, progressSec: Int, totalSec: Int) {
        dao.saveProgress(
            WatchProgressEntity(
                dramaId = dramaId,
                episodeNumber = episodeNumber,
                progressSeconds = progressSec,
                totalSeconds = totalSec,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    // Room Comments
    fun getComments(dramaId: String, episodeNumber: Int): Flow<List<CommentEntity>> {
        return dao.getComments(dramaId, episodeNumber)
    }

    suspend fun addComment(dramaId: String, episodeNumber: Int, author: String, text: String) {
        dao.insertComment(
            CommentEntity(
                dramaId = dramaId,
                episodeNumber = episodeNumber,
                authorName = author,
                authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=100&auto=format&fit=crop&q=80",
                commentText = text,
                likesCount = 0,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun likeComment(commentId: Long) {
        dao.likeComment(commentId)
    }

    fun claimDailyBonus(): Int {
        val bonus = 1000
        _vipStatus.update { current ->
            current.copy(
                coinsBalance = current.coinsBalance + bonus,
                dailyCheckinClaimed = true
            )
        }
        return bonus
    }
}
