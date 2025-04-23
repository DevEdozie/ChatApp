package com.edozie.chatapp.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "chat_threads")
data class ChatThreadEntity(
    @PrimaryKey val id: String,
    val otherUserId: String,
    val otherUserEmail: String,
    val lastMessage: String?,
    val timesStamp: Long
)
