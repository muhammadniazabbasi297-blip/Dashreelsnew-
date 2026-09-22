package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.BookmarkEntity
import com.example.data.model.CommentEntity
import com.example.data.model.WatchProgressEntity

@Database(
    entities = [
        WatchProgressEntity::class,
        BookmarkEntity::class,
        CommentEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DashReelsDatabase : RoomDatabase() {
    abstract fun dao(): DashReelsDao

    companion object {
        @Volatile
        private var INSTANCE: DashReelsDatabase? = null

        fun getDatabase(context: Context): DashReelsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DashReelsDatabase::class.java,
                    "dashreels_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
