package com.example.healthtrack.data.repository

import com.example.healthtrack.data.model.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }
    suspend fun register(email: String, password: String, fullName: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                val userMap = hashMapOf(
                    "uid" to user.uid,
                    "email" to email,
                    "fullName" to fullName,
                    "password" to password,
                    "role" to "PACIENTE",
                )

                firestore.collection("usuarios")
                    .document(user.uid)
                    .set(userMap)
                    .await()
                AuthResult.Success(user)
            } else {
                AuthResult.Error("No se pudo crear el usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al registrarse")
        }
    }
}