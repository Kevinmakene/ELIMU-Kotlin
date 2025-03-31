package com.kotlingdgocucb.elimu.domain.usecase

import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ProgressEntity
import com.kotlingdgocucb.elimu.data.repository.ProgressRepository
import com.kotlingdgocucb.elimu.domain.model.ProgressCreate
import com.kotlingdgocucb.elimu.domain.model.ProgressResponse

// ProgressUseCase.kt
class ProgressUseCase(private val repository: ProgressRepository) {

    suspend fun trackProgress(videoId: Int, menteeEmail: String): ProgressResponse? {
        val progress = ProgressCreate(videoId = videoId, menteeEmail = menteeEmail)
        return repository.addProgress(progress)
    }

    suspend fun retrieveProgress(videoId: Int, menteeEmail: String): ProgressResponse? {
        return repository.getProgress(videoId, menteeEmail)
    }
}