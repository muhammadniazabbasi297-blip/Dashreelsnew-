package com.example.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

data class DramaSeries(
    val id: String,
    val title: String,
    val synopsis: String,
    @DrawableRes val posterRes: Int,
    val category: String,
    val tags: List<String>,
    val rating: Double,
    val viewsCountFormatted: String,
    val totalEpisodes: Int,
    val releaseYear: Int = 2025,
    val isTrending: Boolean = false,
    val isVipFreeUnlocked: Boolean = true,
    val director: String = "Alexander Vance",
    val cast: List<String> = listOf("Victoria Sterling", "Lucas Thorne")
)

data class Episode(
    val id: String,
    val dramaId: String,
    val episodeNumber: Int,
    val title: String,
    val durationSeconds: Int,
    val videoUrl: String,
    val subtitleText: String = "",
    val isVipFree: Boolean = true
)

@Entity(tableName = "watch_progress")
data class WatchProgressEntity(
    @PrimaryKey val dramaId: String,
    val episodeNumber: Int,
    val progressSeconds: Int,
    val totalSeconds: Int,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val dramaId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val dramaId: String,
    val episodeNumber: Int,
    val authorName: String,
    val authorAvatar: String,
    val commentText: String,
    val likesCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

data class VipStatus(
    val isPremiumFreeUnlocked: Boolean = true,
    val planName: String = "DashReels VIP Lifetime Free",
    val coinsBalance: Int = 10000,
    val dailyCheckinClaimed: Boolean = true,
    val ultraHdUnlocked: Boolean = true,
    val noAdsEnabled: Boolean = true,
    val downloadUnlocked: Boolean = true
)
