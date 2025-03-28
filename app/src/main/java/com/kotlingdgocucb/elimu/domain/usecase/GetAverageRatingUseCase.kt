package com.kotlingdgocucb.elimu.domain.usecase

import com.kotlingdgocucb.elimu.data.repository.ReviewRepository

class GetAverageRatingUseCase(private val reviewRepository: ReviewRepository) {
    // L'opérateur 'invoke' permet d'appeler l'objet comme une fonction
    suspend operator fun invoke(videoId: Int): Float {
        return reviewRepository.getAverageRating(videoId)
    }
}
