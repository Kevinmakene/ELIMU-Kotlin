package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video

interface VideoRepository {
    suspend fun getAllVideos(): List<Video>
    suspend fun getVideoById(id: Int): Video?
}

