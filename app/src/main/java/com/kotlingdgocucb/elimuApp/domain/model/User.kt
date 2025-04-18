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
    val level: String,
    val mentor_name: String,
    @SerialName("created_at") val createdAt: String,
    val mentor_email : String = "",


    val mentor_profileUrl: String = "",
    val mentor_experience: String = "",
    val mentor_description: String = "",
    val mentor_githubUrl: String = "",
    val mentor_linkedinUrl: String = "",
    val mentor_xUrl: String = "",
    val mentor_instagramUrl: String = "",

    )
