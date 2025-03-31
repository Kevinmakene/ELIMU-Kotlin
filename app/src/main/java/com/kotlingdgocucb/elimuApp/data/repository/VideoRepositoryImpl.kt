package com.kotlingdgocucb.elimuApp.data.repository

import android.util.Log
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.ElimuDao
import com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity.Video
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

class VideoRepositoryImpl(
    private val client: HttpClient,
    private val elimuDao: ElimuDao
) : VideoRepository {

    // Base URL sans barre oblique finale
    private val baseUrl = "https://elimugdgocucb.onrender.com/"

    override suspend fun getAllVideos(): List<Video> {
        // 1. Récupération initiale des vidéos locales
        val localVideos = elimuDao.getAllVideos()
        Log.d("VideoRepository", "Nombre de vidéos locales : ${localVideos.size}")

        return try {
            // 2. Appel réseau pour récupérer les vidéos depuis l'endpoint
            val response: HttpResponse = client.get("${baseUrl}videos/videos/")
            Log.d("VideoRepository", "Réponse réseau pour getAllVideos : ${response.status}")
            val networkVideos: List<Video> = response.body()
            Log.d("VideoRepository", "Nombre de vidéos récupérées depuis le réseau : ${networkVideos.size}")

            // 3. Mise à jour de la base de données locale en utilisant la stratégie REPLACE
            elimuDao.insertVideos(networkVideos)
            Log.d("VideoRepository", "Mise à jour locale effectuée avec succès.")

            // 4. Récupération et retour des vidéos mises à jour depuis la base locale
            val updatedVideos = elimuDao.getAllVideos()
            Log.d("VideoRepository", "Nombre de vidéos locales après mise à jour : ${updatedVideos.size}")
            updatedVideos
        } catch (e: Exception) {
            Log.e("VideoRepository", "Erreur lors de la récupération des vidéos depuis le réseau : ${e.message}", e)
            // En cas d'erreur, retourner les vidéos locales
            localVideos
        }
    }

    override suspend fun getVideoById(id: Int): Video? {
        // 1. Récupération initiale depuis la base locale
        val localVideo = elimuDao.getVideoById(id)
        if (localVideo != null) {
            Log.d("VideoRepository", "Vidéo locale trouvée pour id $id : ${localVideo.title}")
        } else {
            Log.d("VideoRepository", "Aucune vidéo locale trouvée pour l'id $id")
        }

        return try {
            // 2. Appel réseau pour récupérer la vidéo depuis l'endpoint
            val response: HttpResponse = client.get("${baseUrl}videos/$id")
            Log.d("VideoRepository", "Réponse réseau pour getVideoById (id=$id) : ${response.status}")
            val networkVideo: Video = response.body()
            Log.d("VideoRepository", "Vidéo récupérée depuis le réseau : ${networkVideo.title}")

            // 3. Mise à jour locale avec la vidéo récupérée (stratégie REPLACE)
            elimuDao.insertVideo(networkVideo)
            networkVideo
        } catch (e: Exception) {
            Log.e("VideoRepository", "Erreur lors de la récupération de la vidéo avec l'id $id depuis le réseau : ${e.message}", e)
            // En cas d'erreur, retourner la vidéo locale (si elle existe)
            localVideo
        }
    }
}
