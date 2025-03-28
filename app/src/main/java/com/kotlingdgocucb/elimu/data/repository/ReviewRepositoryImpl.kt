package com.kotlingdgocucb.elimu.data.repository

import com.kotlingdgocucb.elimu.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Review
import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.ReviewCreate
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ReviewRepositoryImpl(
    private val client: HttpClient,
    private val elimuDao: ElimuDao
) : ReviewRepository {

    // Base URL sans barre oblique finale
    private val baseUrl = "https://elimugdgocucb.onrender.com"

    override suspend fun getReviews(videoId: Int): List<Review> {
        // 1. Récupération initiale des reviews locales pour affichage rapide
        val localReviews = elimuDao.getReviewsByVideoId(videoId)
        println("getReviews: Reviews locales pour videoId $videoId: $localReviews")

        // 2. Récupération des reviews depuis le serveur
        try {
            val response: HttpResponse = client.get("$baseUrl/reviews/$videoId")
            println("getReviews: Réponse reçue avec le status: ${response.status}")
            val networkReviews: List<Review> = response.body()
            println("getReviews: Reviews récupérées depuis le réseau: $networkReviews")

            // 3. Mise à jour de la base locale (stratégie IGNORE pour éviter les doublons)
            elimuDao.insertReviews(networkReviews)
        } catch (e: Exception) {
            println("getReviews: Erreur lors de la récupération depuis le réseau: ${e.message}")
        }

        // 4. Retour de la liste mise à jour (localement synchronisée)
        val updatedReviews = elimuDao.getReviewsByVideoId(videoId)
        println("getReviews: Reviews locales mises à jour: $updatedReviews")
        return updatedReviews
    }

    override suspend fun getAverageRating(videoId: Int): Float {
        println("getAverageRating: Demande de la note moyenne pour videoId $videoId à l'URL: $baseUrl/videos/$videoId/rating")
        try {
            val response: HttpResponse = client.get("$baseUrl/videos/$videoId/rating")
            println("getAverageRating: Réponse reçue avec le status: ${response.status}")
            val avgRating: Float = response.body()
            println("getAverageRating: Note moyenne parsée: $avgRating")
            return avgRating
        } catch (e: Exception) {
            println("getAverageRating: Erreur lors de la récupération de la note moyenne: ${e.message}")
            throw e
        }
    }

    override suspend fun postReview(review: ReviewCreate): Review {
        println("postReview: Envoi de la review: $review à l'URL: $baseUrl/reviews/")
        try {
            val response: HttpResponse = client.post("$baseUrl/reviews/") {
                contentType(ContentType.Application.Json)
                setBody(review)
            }
            println("postReview: Réponse reçue avec le status: ${response.status}")
            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                println("postReview: Erreur de validation: $errorBody")
            }
            val postedReview: Review = response.body()
            println("postReview: Review envoyée et parsée: $postedReview")

            // Mise à jour de la base locale avec la review postée
            elimuDao.insertReview(postedReview)
            return postedReview
        } catch (e: Exception) {
            println("postReview: Erreur lors de l'envoi de la review: ${e.message}")
            throw e
        }
    }
}
