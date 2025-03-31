package com.kotlingdgocucb.elimu.domain.model



import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProgressCreate(
    @SerialName("video_id") val videoId: Int,
    @SerialName("mentee_email") val menteeEmail: String
)


@Serializable
data class ProgressResponse(
    val id: Int = 0,
    @SerialName("video_id") val videoId: Int = 0,
    @SerialName("mentee_email") val menteeEmail: String = "",
    val watched: Int = 0
)
