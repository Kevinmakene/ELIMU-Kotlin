package com.kotlingdgocucb.elimu.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerialName("video_id") val videoId: Int,
    @SerialName("mentee_email") val menteeEmail: String,
    val watched: Int
)
