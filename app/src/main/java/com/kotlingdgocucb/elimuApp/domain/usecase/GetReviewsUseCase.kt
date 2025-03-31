package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimuApp.data.repository.ReviewRepository

class GetReviewsUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(videoId: Int): List<Review> {
        return reviewRepository.getReviews(videoId)
    }
}
