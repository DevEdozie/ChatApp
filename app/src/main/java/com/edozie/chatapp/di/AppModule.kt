package com.edozie.chatapp.di

import android.content.Context
import androidx.room.Room
import com.edozie.chatapp.data.room.AppDatabase
import com.edozie.chatapp.util.NetworkObserver
import com.edozie.chatapp.util.UserPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore =
        FirebaseFirestore.getInstance()

//    @Provides
//    @Singleton
//    fun provideFirebaseStorage(): FirebaseStorage =
//        FirebaseStorage.getInstance()


    @Provides
    @Singleton
    fun provideNetworkObserver(
        @ApplicationContext context: Context
    ): NetworkObserver = NetworkObserver(context)


    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext ctx: Context): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "chat_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideUserPreferences(@ApplicationContext ctx: Context): UserPreferences =
        UserPreferences(ctx)


}
