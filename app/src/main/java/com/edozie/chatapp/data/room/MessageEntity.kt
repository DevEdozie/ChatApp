package com.edozie.chatapp.data.room

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "messages",
    foreignKeys = [ForeignKey(
        entity = ChatThreadEntity::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = CASCADE
    )],
    indices = [Index("chatId")]
)
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val senderId: String,
    val text: String,
    val timestamp: Long
)
