package com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "review_create")
data class ReviewCreate(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerialName("video_id") val videoId: Int,
    @SerialName("mentee_email") val menteeEmail: String,
    val stars: Int,
    val comment: String?
)

@Serializable
@Entity(tableName = "review")
data class Review(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @SerialName("video_id") val videoId: Int,
    @SerialName("mentee_email") val menteeEmail: String,
    val stars: Int,
    val comment: String?,
    @SerialName("created_at") val createdAt: String
)
