package com.edozie.chatapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edozie.chatapp.remote.repository.AuthRepository
import com.edozie.chatapp.util.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val repo: AuthRepository
) : AndroidViewModel(application) {

    // Backing properties
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirm_password = MutableStateFlow("")

    // Expose read-only StateFlows
    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val confirm_password = _confirm_password.asStateFlow()

    private val prefs by lazy {
        getApplication<Application>().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    }

    private val _state = MutableStateFlow<AuthState>(AuthState.Idle)
    val state: StateFlow<AuthState> = _state

    fun signUp(email: String, pass: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        repo.signUp(email, pass)
            .onSuccess { _state.value = AuthState.Authenticated(it) }
            .onFailure { _state.value = AuthState.Error(it.message ?: "Signup failed") }
    }


    fun login(email: String, pass: String) = viewModelScope.launch {
        _state.value = AuthState.Loading
        repo.login(email, pass)
            .onSuccess {
                _state.value = AuthState.Authenticated(it)
                // Save login status to SharedPreferences
                prefs.edit() { putBoolean("is_logged_in", true) }
            }
            .onFailure { _state.value = AuthState.Error(it.message ?: "Login failed") }
    }

    fun logout() {
        repo.logout()
        _state.value = AuthState.Unauthenticated
        // Save login status to SharedPreferences
        prefs.edit() { putBoolean("is_logged_in", false) }
    }

    // Update functions

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirm_password.value = newConfirmPassword
    }


}





