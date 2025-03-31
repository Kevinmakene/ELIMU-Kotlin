package com.kotlingdgocucb.elimuApp.data.datasource.local.room.entity

import androidx.room.Entity

@Entity(
    tableName = "mentor",
    primaryKeys = ["id", "email"]
)
data class Mentor(
    val id: Int = 0,
    val tack: String = "",
    val name: String = "",
    val profileUrl: String = "",
    val experience: String = "",
    val description: String = "",
    val githubUrl: String = "",
    val linkedinUrl: String = "",
    val xUrl: String = "",
    val instagramUrl: String = "",
    val email: String = ""
)
