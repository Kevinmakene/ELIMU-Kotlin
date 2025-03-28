package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Video

interface VideoRepository {
    suspend fun getAllVideos(): List<Video>
    suspend fun getVideoById(id: Int): Video?
}

