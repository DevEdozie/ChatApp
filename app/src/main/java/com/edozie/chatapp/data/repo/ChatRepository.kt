package com.edozie.chatapp.data.repo

import com.edozie.chatapp.data.room.ChatThreadEntity
import com.edozie.chatapp.data.room.MessageEntity
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeThreads(): Flow<List<ChatThreadEntity>>
    fun observeMessages(chatId: String): Flow<List<MessageEntity>>
    suspend fun createChatWith(email: String): String
    suspend fun sendMessage(chatId: String, text: String)
    fun setTyping(chatId: String, isTyping: Boolean)
    fun observeTyping(chatId: String, userId: String): Flow<Boolean>
}