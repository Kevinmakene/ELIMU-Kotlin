package com.kotlingdgocucb.elimuApp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int = 0, // Valeur par défaut,
    val name: String,
    val email: String,
    @SerialName("is_logged_in") val isLoggedIn: Boolean,
    val profile_picture_uri: String,
    val track: String,
    val mentor: String,
    @SerialName("created_at") val createdAt: String,
    val mentor_email : String = ""
)
