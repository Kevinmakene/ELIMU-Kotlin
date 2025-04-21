package com.kotlingdgocucb.elimuApp.ui.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kotlingdgocucb.elimuApp.domain.model.Message
import com.kotlingdgocucb.elimuApp.domain.model.User
import com.kotlingdgocucb.elimuApp.domain.usecase.CreateChatUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.GetChatMessageUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.MarkMessageAsReadUseCase
import com.kotlingdgocucb.elimuApp.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessageViewModel(
    private val firebase : Firebase,
    private val markMessageAsReadUseCase: MarkMessageAsReadUseCase,
    private val getChatMessageUseCase: GetChatMessageUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val createChatUseCase: CreateChatUseCase
): ViewModel() {
    private var chatId:  MutableState<String> = mutableStateOf("")
    private var _messageList: MutableStateFlow<List<Message>> = MutableStateFlow(emptyList())
    val message: StateFlow<List<Message>> = _messageList

    fun getChat(user: User) {
        viewModelScope.launch {
            val chatId = "${user.email}-${user.mentor_email}"
            try {
                getChatMessageUseCase(chatId = chatId, user = user).collect {
                    _messageList.value = it
                }
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(senderId: String, receiverId: String,content: String) {
        viewModelScope.launch {
            sendMessageUseCase(chatId.value, content, receiverId = receiverId, senderId =senderId )
        }
    }

    fun markMessageAsRead(messageId: String) {
        viewModelScope.launch {
           markMessageAsReadUseCase(chatId.value, messageId)
        }
    }
}
/*
    val messages: List<Message> = mutableListOf()
    fun onNewMessageTextChange(newText: String) {
        _newMessageText.value = newText
    }
    */