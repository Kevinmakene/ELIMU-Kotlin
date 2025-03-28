package com.kotlingdgocucb.elimu.domain.usecase

import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.repository.ReviewRepository

class GetReviewsUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(videoId: Int): List<Review> {
        return reviewRepository.getReviews(videoId)
    }
}
