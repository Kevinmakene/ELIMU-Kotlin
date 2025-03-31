package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class UserRepositoryImpl(private val client: HttpClient) : UserRepository {
    override suspend fun createUser(user: User): User {
        println("UserRepositoryImpl: Envoi de la requête pour créer l'utilisateur: $user")
        val response: HttpResponse = client.post("https://elimugdgocucb.onrender.com/users/") {
            contentType(ContentType.Application.Json)
            setBody(user)
        }
        println("UserRepositoryImpl: Statut de la réponse: ${response.status}")
        val responseBody = response.bodyAsText()
        println("UserRepositoryImpl: Corps de la réponse: $responseBody")

        if (!response.status.isSuccess()) {
            println("UserRepositoryImpl: Erreur détectée: ${response.status}")
            throw Exception("Erreur serveur: ${response.status} - $responseBody")
        }

        return try {
            val createdUser: User = response.body()
            println("UserRepositoryImpl: Utilisateur créé: $createdUser")
            createdUser
        } catch (e: Exception) {
            println("UserRepositoryImpl: Erreur lors de la désérialisation de la réponse: ${e.message}")
            throw e
        }
    }
}
