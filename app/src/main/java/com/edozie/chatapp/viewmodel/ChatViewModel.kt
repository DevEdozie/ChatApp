package com.edozie.chatapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edozie.chatapp.data.repo.ChatRepository
import com.edozie.chatapp.data.room.MessageEntity
import com.edozie.chatapp.remote.repository.AuthRepository
import com.edozie.chatapp.util.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repo: ChatRepository,
    private val auth: AuthRepository,
    private val network: NetworkObserver
) : ViewModel() {

    lateinit var chatId: String
    lateinit var otherEmail: String
    private lateinit var otherUserId: String

    val messages = MutableStateFlow<List<MessageEntity>>(emptyList())
    val typing = MutableStateFlow(false)

    fun start(chatId: String, otherEmail: String, otherUid: String) {
        this.chatId = chatId
        this.otherEmail = otherEmail
        this.otherUserId = otherUid

        repo.observeMessages(chatId)
            .onEach { messages.value = it }
            .launchIn(viewModelScope)

        // observe typing of the other user
        repo.observeTyping(chatId, otherUserId)
            .onEach { typing.value = it }
            .launchIn(viewModelScope)
    }

    fun send(text: String) {
        if (!network.isOnline()) return
        viewModelScope.launch { repo.sendMessage(chatId, text) }
    }

    fun onTyping(isTyping: Boolean) {
        repo.setTyping(chatId, isTyping)
    }
}