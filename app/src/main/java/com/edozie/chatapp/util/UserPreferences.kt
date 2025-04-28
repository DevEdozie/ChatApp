package com.edozie.chatapp.util

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


private val Context.datastore by preferencesDataStore("user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        private val KEY_EMAIL = stringPreferencesKey("email")
        private val KEY_PHOTOURL = stringPreferencesKey("photo_url")
    }

    suspend fun saveUserProfile(
        email: String?,
        photoUrl: String?
    ) {
        context.datastore.edit { prefs ->
            prefs[KEY_EMAIL] = email ?: ""
            prefs[KEY_PHOTOURL] = photoUrl ?: ""
        }
    }

    val userEmail: Flow<String> = context.datastore.data.map { it[KEY_EMAIL] ?: "" }
    val userPhotoUrl: Flow<String> = context.datastore.data.map { it[KEY_PHOTOURL] ?: "" }

    suspend fun clear() {
        context.datastore.edit { it.clear() }
    }

}