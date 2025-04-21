package com.kotlingdgocucb.elimuApp.data.repository

import com.kotlingdgocucb.elimuApp.domain.model.Message
import com.kotlingdgocucb.elimuApp.domain.model.User
import kotlinx.coroutines.flow.StateFlow

interface MessageRepository {
    suspend fun getChatMessages(chatId: String, user: User): StateFlow<List<Message>>

    suspend fun sendMessage(chatId: String, message: String, senderId: String, receiverId: String): String?

    suspend fun markMessageAsRead(chatId: String, messageId: String)


}