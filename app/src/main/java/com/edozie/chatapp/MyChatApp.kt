package com.edozie.chatapp

import android.app.Application
import androidx.room.Room
import com.edozie.chatapp.data.room.AppDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MyChatApp : Application() {
    lateinit var db: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "chat_db"
        ).build()
    }
}