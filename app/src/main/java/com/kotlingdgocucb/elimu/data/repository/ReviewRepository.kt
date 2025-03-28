package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate

interface ReviewRepository {
    suspend fun getReviews(videoId: Int): List<Review>
    suspend fun getAverageRating(videoId: Int): Float
    suspend fun postReview(review: ReviewCreate): Review
}
