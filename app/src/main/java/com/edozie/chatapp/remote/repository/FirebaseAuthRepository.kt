package com.edozie.chatapp.remote.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class FirebaseAuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

//    override suspend fun signUp(email: String, pass: String): Result<FirebaseUser> {
//        return try {
//            // 1) Create the user in Firebase Auth
//            val authResult = auth
//                .createUserWithEmailAndPassword(email, pass)
//                .await()                              // <-- suspends until complete
//            val user = authResult.user!!
//
//            // 2) Write to Firestore under "users/{uid}"
//            firestore.collection("users")
//                .document(user.uid)
//                .set(mapOf("email" to user.email))
//                .await()                              // <-- suspends until complete
//
//            // 3) Return success
//            Result.success(user)
//
//        } catch (e: Exception) {
//            // any error (Auth or Firestore) winds up here
//            Result.failure(e)
//        }
//    }


    override suspend fun signUp(email: String, pass: String): Result<FirebaseUser> =
        suspendCancellableCoroutine { cont ->
            val task = auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user!!
                    // 1) Write to Firestore “users/{uid}”
                    firestore.collection("users")
                        .document(user.uid)
                        .set(mapOf("email" to user.email))
                        .addOnSuccessListener {
                            // resume only after Firestore write succeeds
                            cont.resume(Result.success(user))
                        }
                        .addOnFailureListener { firestoreEx ->
                            // if Firestore write fails, treat as overall failure
                            cont.resume(Result.failure(firestoreEx))
                        }
                }
                .addOnFailureListener { authEx ->
                    cont.resume(Result.failure(authEx))
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