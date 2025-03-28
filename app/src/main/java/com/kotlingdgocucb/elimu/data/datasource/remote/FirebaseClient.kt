package com.kotlingdgocucb.elimu.data.datasource.remote

import com.kotlingdgocucb.elimu.data.datasource.local.room.entity.Mentor
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class FirebaseClient {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    // Remplace "your-project-id" par l'identifiant de ton projet Firebase.
    private val baseUrl = "https://elimu-be04d-default-rtdb.firebaseio.com/data/~2F"

    suspend fun getMentorsByCategory(): Map<String, List<Mentor>> {
        return client.get(baseUrl).body()
    }
}

