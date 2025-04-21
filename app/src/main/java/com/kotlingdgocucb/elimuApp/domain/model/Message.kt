package com.kotlingdgocucb.elimuApp.domain.model

import com.google.firebase.Timestamp

data class Message(
    var id: String = "",
    var timestamp: Timestamp? = null,
    var isRead: Boolean = false,
    var message: String = "",
    var receiverId: String = "",
    var senderId: String = ""
)