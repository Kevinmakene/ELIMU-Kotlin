package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.MessageRepository

class MarkMessageAsReadUseCase (
   private val repository: MessageRepository
){
    suspend operator fun invoke(chatId: String,messageId: String){
        repository.markMessageAsRead(chatId = chatId, messageId = messageId)
    }
}