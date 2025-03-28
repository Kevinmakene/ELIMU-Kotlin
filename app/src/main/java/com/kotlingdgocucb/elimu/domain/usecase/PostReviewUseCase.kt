package com.kotlingdgocucb.elimu.domain.usecase


import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.repository.ReviewRepository
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate

class PostReviewUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(review: ReviewCreate): Review {
        return reviewRepository.postReview(review)
    }
}
