package com.kotlingdgocucb.elimuApp.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kotlingdgocucb.elimuApp.domain.model.Message
import com.kotlingdgocucb.elimuApp.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await


data class Chat(
    var id: String = "", // "georges@gdgocucb.com-jacques@gdgocucb.com"
    var messages: List<Message> = emptyList()
)

class MessageRepositoryImpl(
   private val firestore :FirebaseFirestore
):MessageRepository {
    private val chatsCollection = firestore.collection("chats")


    override suspend fun getChatMessages(chatId: String, user: User): StateFlow<List<Message>> {
        val messagesFlow = MutableStateFlow<List<Message>>(emptyList())
        val chatRef = firestore.collection("chats").document(chatId)
        try {
            val chatSnapshot = chatRef.get().await()
            if (chatSnapshot.exists()) {
                // The chat exists, retrieve the messages
                val messagesCollection = chatRef.collection("messages")
                val messagesSnapshot = messagesCollection.orderBy("timestamp", Query.Direction.ASCENDING).get().await()
                val messagesList = messagesSnapshot.documents.mapNotNull { it.toObject(Message::class.java) }
                messagesFlow.value = messagesList // Update the flow with the retrieved messages
            } else {
                // The chat does not exist, create a new chat
                val initialChatData = mapOf(
                    "participants" to listOf(user.email, user.mentor_email),
                    "createdAt" to System.currentTimeMillis()
                )
                chatRef.set(initialChatData).await()
                // Optionally, add an initial message if needed
            }
        } catch (e: Exception) {
            // Handle errors
            e.printStackTrace()
        }
        return messagesFlow.asStateFlow()
    }

    override suspend fun sendMessage(chatId: String, message: String, senderId: String, receiverId: String): String? {
        val newMessage = Message(
            message = message,
            senderId = senderId,
            receiverId = receiverId,
            timestamp = Timestamp.now(),
            isRead = false // Par défaut, le message n'est pas lu
        )

        return try {
            val documentReference = chatsCollection.document(chatId)
                .collection("messages")
                .add(newMessage)
                .await()
            documentReference.id // Retourner l'ID du message créé
        } catch (e: Exception) {
            println("Error sending message: ${e.message}")
            null // Retourner null en cas d'erreur
        }
    }

    // Fonction pour marquer un message comme lu
    override suspend fun markMessageAsRead(chatId: String, messageId: String) {
        try {
            chatsCollection.document(chatId)
                .collection("messages")
                .document(messageId)
                .update("isRead", true)
                .await()
        } catch (e: Exception) {
            println("Error marking message as read: ${e.message}")
        }
    }

    // Fonction pour créer un chat (si nécessaire)
    override suspend fun createChat(chatId: String, initialMessage: Message) {
        try {
            // Vérifier si le chat existe déjà

            val chatDocument = chatsCollection.document(chatId).get().await()
            if (!chatDocument.exists()) {
                // Créer le document du chat
                chatsCollection.document(chatId).set(mapOf("id" to chatId)).await()
                // Ajouter le premier message
                val messageId = sendMessage(chatId, initialMessage.message, initialMessage.senderId, initialMessage.receiverId)
                if (messageId != null) {
                    // Le message a été envoyé avec succès
                    println("Chat created successfully with initial message.")
                } else {
                    println("Chat created, but failed to send initial message.")
                }
            } else {
                println("Chat already exists.")
            }
        } catch (e: Exception) {
            println("Error creating chat: ${e.message}")
        }
    }

}

