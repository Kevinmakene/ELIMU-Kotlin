package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.domain.model.ProgressCreate
import com.kotlingdgocucb.elimu.domain.model.ProgressResponse

interface ProgressRepository {
    suspend fun getProgress(videoId: Int, menteeEmail: String): ProgressResponse?
    suspend fun addProgress(progress: ProgressCreate): ProgressResponse?
}
