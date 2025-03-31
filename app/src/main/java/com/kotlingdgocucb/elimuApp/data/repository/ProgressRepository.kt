package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.domain.model.ProgressCreate
import com.kotlingdgocucb.elimuApp.domain.model.ProgressResponse

interface ProgressRepository {
    suspend fun getProgress(videoId: Int, menteeEmail: String): ProgressResponse?
    suspend fun addProgress(progress: ProgressCreate): ProgressResponse?
}
