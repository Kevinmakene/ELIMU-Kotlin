package com.kotlingdgocucb.elimuApp.domain.usecase


import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimuApp.data.repository.ReviewRepository
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.ReviewCreate

class PostReviewUseCase(private val reviewRepository: ReviewRepository) {
    suspend operator fun invoke(review: ReviewCreate): Review {
        return reviewRepository.postReview(review)
    }
}
