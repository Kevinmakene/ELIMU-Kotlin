package com.kotlingdgocucb.elimu.data.datasource.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

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
