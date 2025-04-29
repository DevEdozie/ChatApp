package com.edozie.chatapp.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.util.Base64
import androidx.annotation.RequiresApi
import java.io.ByteArrayOutputStream
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.edozie.chatapp.remote.repository.AuthRepository
import com.edozie.chatapp.util.AuthState
import com.edozie.chatapp.util.UserPreferences
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import androidx.core.graphics.scale

@HiltViewModel
class AuthViewModel @Inject constructor(
    application: Application,
    private val repo: AuthRepository,
    private val firestore: FirebaseFirestore,
    private val userPrefs: UserPreferences,
) : AndroidViewModel(application) {

    // Backing properties
    private val _email = MutableStateFlow("")
    private val _password = MutableStateFlow("")
    private val _confirm_password = MutableStateFlow("")

    // Expose read-only StateFlows
    val email = _email.asStateFlow()
    val password = _password.asStateFlow()
    val confirm_password = _confirm_password.asStateFlow()

    // Current FirebaseUser
//    val currentUser = getCurrentUser()

    // Cached profile via DataStore
    val userEmail: Flow<String> = userPrefs.userEmail
    val userPhoto: Flow<String> = userPrefs.userPhotoUrl

    init {
        // On startup, load from Firestore into DataStore
        viewModelScope.launch {
            loadUserProfile()
        }
    }

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
                loadUserProfile()
            }
            .onFailure { _state.value = AuthState.Error(it.message ?: "Login failed") }
    }

    fun logout() {
        viewModelScope.launch {
            repo.logout()
            _state.value = AuthState.Unauthenticated
            // Save login status to SharedPreferences
            prefs.edit() { putBoolean("is_logged_in", false) }
            userPrefs.clear()
        }
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

    fun getCurrentUser(): FirebaseUser? {
        return repo.currentUser
    }

    // Load and cache profile from Firestore
    private suspend fun loadUserProfile() {
        val uid = getCurrentUser()?.uid ?: return
        val doc = firestore.collection("users")
            .document(uid)
            .get()
            .await()
        val base64 = doc.getString("photoBase64") ?: ""
        val email = getCurrentUser()?.email
        // Save into DataStore
        userPrefs.saveUserProfile(
            email = email,
            photoUrl = base64,     // store the Base64 string
        )
    }


    @RequiresApi(Build.VERSION_CODES.P)
    fun updateProfilePicture(uri: Uri) = viewModelScope.launch {
        val uid = getCurrentUser()?.uid ?: return@launch

        // TEST v-1
        // 1) Load and compress file into bytes
//        val input = getApplication<Application>().contentResolver.openInputStream(uri)
//        val raw = input!!.readBytes()
//        val base64 = Base64.encodeToString(raw, Base64.NO_WRAP)

        // TEST v-2
        // Pseudocode: scale the bitmap down to e.g. 200×200 px
        val source = ImageDecoder.createSource(getApplication<Application>().contentResolver, uri)
        val original = ImageDecoder.decodeBitmap(source)
        val thumbnail = original.scale(200, 200)
        val baos = ByteArrayOutputStream()
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 70, baos)
        val bytes = baos.toByteArray()
        val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)


        // 2) Save into Firestore document
        firestore.collection("users")
            .document(uid)
            .update("photoBase64", base64)
            .await()

        // 3) Cache locally
        userPrefs.saveUserProfile(
            email = userEmail.firstOrNull(),
            photoUrl = base64,     // now holds your Base64 blob
        )
    }


}





