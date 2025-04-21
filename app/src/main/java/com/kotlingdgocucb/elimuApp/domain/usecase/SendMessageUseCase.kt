package com.kotlingdgocucb.elimuApp.domain.usecase

import com.kotlingdgocucb.elimuApp.data.repository.MessageRepository
import com.kotlingdgocucb.elimuApp.domain.model.Message

class SendMessageUseCase(
   private val repository: MessageRepository
){
     suspend operator fun invoke(chatId: String,message: String,senderId: String,receiverId: String): String?{
       return repository.sendMessage(chatId = chatId, message = message, senderId =senderId, receiverId = receiverId)
    }
}