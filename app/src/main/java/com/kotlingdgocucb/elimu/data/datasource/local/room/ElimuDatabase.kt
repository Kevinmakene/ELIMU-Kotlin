package com.kotlingdgocucb.elimu.data.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate

@Database(
    entities = [Mentor::class, Video::class, Review::class, ReviewCreate::class],
    version = 1
)
abstract class ElimuDatabase : RoomDatabase() {
    abstract fun elimuDao(): ElimuDao
}
