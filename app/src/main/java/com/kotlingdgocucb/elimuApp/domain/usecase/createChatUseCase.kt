package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.MessageRepository
import com.kotlingdgocucb.elimuApp.domain.model.Message

class CreateChatUseCase(
    private val repository: MessageRepository
) {
    suspend operator fun invoke(chatId: String,message: Message){
        repository.createChat(chatId = chatId, initialMessage = message )
    }
}