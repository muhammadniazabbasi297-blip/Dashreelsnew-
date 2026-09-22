package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.BookmarkEntity
import com.example.data.model.CommentEntity
import com.example.data.model.WatchProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DashReelsDao {

    // Watch Progress
    @Query("SELECT * FROM watch_progress ORDER BY updatedAt DESC")
    fun getAllProgress(): Flow<List<WatchProgressEntity>>

    @Query("SELECT * FROM watch_progress WHERE dramaId = :dramaId LIMIT 1")
    fun getProgressForDrama(dramaId: String): Flow<WatchProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: WatchProgressEntity)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY addedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE dramaId = :dramaId)")
    fun isBookmarked(dramaId: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE dramaId = :dramaId")
    suspend fun removeBookmark(dramaId: String)

    // Comments
    @Query("SELECT * FROM comments WHERE dramaId = :dramaId AND episodeNumber = :episodeNumber ORDER BY createdAt DESC")
    fun getComments(dramaId: String, episodeNumber: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)

    @Query("UPDATE comments SET likesCount = likesCount + 1 WHERE id = :commentId")
    suspend fun likeComment(commentId: Long)
}
