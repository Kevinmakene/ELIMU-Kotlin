package com.kotlingdgocucb.elimuApp.data.datasource.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.converter.Converters
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Mentor
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.ProgressEntity
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.ReviewCreate

@Database(
    entities = [Mentor::class, Video::class, Review::class, ReviewCreate::class,ProgressEntity::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class ElimuDatabase : RoomDatabase() {
    abstract fun elimuDao(): ElimuDao
}
