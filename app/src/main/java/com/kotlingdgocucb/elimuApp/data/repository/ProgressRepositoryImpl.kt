package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDao

import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.ProgressEntity
import com.kotlingdgocucb.elimuApp.domain.model.ProgressCreate
import com.kotlingdgocucb.elimuApp.domain.model.ProgressResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class ProgressRepositoryImpl(
    private val client: HttpClient,
    private val elimuDao: ElimuDao
) : ProgressRepository {

    // Base URL de l'API FastAPI
    private val baseUrl = "https://elimugdgocucb.onrender.com/progress"

    /**
     * Récupérer la progression d'une vidéo pour un mentee donné.
     */
    override suspend fun getProgress(videoId: Int, menteeEmail: String): ProgressResponse? {
        // Récupération locale rapide
        val localProgress = elimuDao.getProgress(videoId, menteeEmail)
        println("getProgress: Progression locale pour videoId $videoId et mentee $menteeEmail : $localProgress")

        try {
            // Récupération des données depuis l'API
            val response: HttpResponse = client.get("$baseUrl/$videoId") {
                parameter("menteeEmail", menteeEmail)
                contentType(ContentType.Application.Json)
            }

            println("getProgress: Réponse reçue avec le statut: ${response.status}")
            val networkProgress: ProgressResponse = response.body()
            println("getProgress: Progression récupérée depuis le serveur: $networkProgress")

            // Mise à jour de la base locale avec les données du serveur
            elimuDao.insertProgress(
                ProgressEntity(
                    id = networkProgress.id,
                    videoId = networkProgress.videoId,
                    menteeEmail = networkProgress.menteeEmail,
                    watched = networkProgress.watched
                )
            )

            return networkProgress
        } catch (e: Exception) {
            println("getProgress: Erreur lors de la récupération depuis le serveur: ${e.message}")
        }

        // Retourner la progression locale si la requête échoue
        return localProgress?.let {
            ProgressResponse(it.id, it.videoId, it.menteeEmail, it.watched)
        }
    }

    /**
     * Enregistrer la progression d'une vidéo et synchroniser avec le serveur.
     */
    override suspend fun addProgress(progress: ProgressCreate): ProgressResponse? {
        println("addProgress: Envoi de la progression: $progress à l'URL: $baseUrl/")

        try {
            val response: HttpResponse = client.post("$baseUrl/") {
                contentType(ContentType.Application.Json)
                setBody(progress)
            }

            println("addProgress: Réponse reçue avec le statut: ${response.status}")
            if (!response.status.isSuccess()) {
                val errorBody = response.bodyAsText()
                println("addProgress: Erreur de validation: $errorBody")
                return null
            }

            val responseText = response.bodyAsText()
            println("addProgress: Corps de la réponse: $responseText")

            // Si le corps est vide, construire un objet par défaut
            val postedProgress: ProgressResponse = if (responseText.trim() == "{}") {
                ProgressResponse(
                    id = 0, // valeur par défaut ou générée côté client si nécessaire
                    videoId = progress.videoId,
                    menteeEmail = progress.menteeEmail,
                    watched = 0 // valeur par défaut
                )
            } else {
                response.body() // désérialisation normale si le JSON contient des données
            }

            println("addProgress: Progression envoyée et parsée: $postedProgress")

            // Mise à jour de la base locale avec la progression envoyée
            elimuDao.insertProgress(
                ProgressEntity(
                    id = postedProgress.id,
                    videoId = postedProgress.videoId,
                    menteeEmail = postedProgress.menteeEmail,
                    watched = postedProgress.watched
                )
            )

            return postedProgress
        } catch (e: Exception) {
            println("addProgress: Erreur lors de l'envoi de la progression: ${e.message}")
            return null
        }
    }

}
