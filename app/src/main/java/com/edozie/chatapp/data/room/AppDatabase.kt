package com.edozie.chatapp.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ChatThreadEntity::class, MessageEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatThreadDao(): ChatThreadDao
    abstract fun messageDao(): MessageDao
}