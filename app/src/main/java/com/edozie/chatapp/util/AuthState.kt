package com.edozie.chatapp.util

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Idle: AuthState()
    object Loading: AuthState()
    data class Authenticated(val user: FirebaseUser): AuthState()
    data class Error(val message: String): AuthState()
    object Unauthenticated: AuthState()
}
