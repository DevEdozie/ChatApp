package com.edozie.chatapp.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepository {
    override suspend fun signUp(email: String, pass: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            val task = auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener {
                    cont.resume(Result.failure(it))
                }
            cont.invokeOnCancellation {
                task.exception?.let { exception -> cont.resumeWithException(exception) }
            }
        }


    override suspend fun login(email: String, pass: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            val task = auth.signInWithEmailAndPassword(email, pass)
                .addOnSuccessListener {
                    cont.resume(Result.success(it.user!!))
                }
                .addOnFailureListener {
                    cont.resume(Result.failure(it))
                }
            cont.invokeOnCancellation {
                task.exception?.let { exception -> cont.resumeWithException(exception) }
            }

        }

    override fun logout() {
        auth.signOut()
    }

    override val currentUser: FirebaseUser?
        get() = auth.currentUser
}