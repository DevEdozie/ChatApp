package com.edozie.chatapp.di

import com.edozie.chatapp.data.repo.ChatRepository
import com.edozie.chatapp.data.repo.FirestoreChatRepository
import com.edozie.chatapp.remote.repository.AuthRepository
import com.edozie.chatapp.remote.repository.FirebaseAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepoModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepo(repo: FirebaseAuthRepository): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepo(repo: FirestoreChatRepository): ChatRepository
}
