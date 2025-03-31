package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.ReviewRepository

class GetAverageRatingUseCase(private val reviewRepository: ReviewRepository) {
    // L'opérateur 'invoke' permet d'appeler l'objet comme une fonction
    suspend operator fun invoke(videoId: Int): Float {
        return reviewRepository.getAverageRating(videoId)
    }
}
