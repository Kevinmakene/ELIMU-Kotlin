package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.MessageRepository
import com.kotlingdgocucb.elimuApp.domain.model.Message
import com.kotlingdgocucb.elimuApp.domain.model.User
import kotlinx.coroutines.flow.StateFlow

class GetChatMessageUseCase (
    private val repository: MessageRepository
){
    suspend operator fun invoke (chatId: String, user: User):StateFlow<List<Message>> {
        return repository.getChatMessages(chatId = chatId, user = user)
    }
}