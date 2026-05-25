package com.example.healthtrack.data.repository

import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : AuthRepository {

    companion object {
        private const val USERS_COLLECTION = "usuarios"
    }

    override suspend fun login(email: String, password: String): AuthResult {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            AuthResult.Success(result.user)
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al iniciar sesión")
        }
    }

    override suspend fun register(email: String, password: String, fullName: String, username: String): AuthResult {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user

            if (firebaseUser != null) {
                val newUser = UserModel(
                    alertEmail = null,
                    correo = email,
                    doctorAsignadoId = null,
                    fechaNacimiento = "",
                    height = 0.0,
                    imc = 0.0,
                    maxGlucose = 0,
                    maxHeartRate = 0,
                    maxPressureDiastolica = 0,
                    maxPressureSistolica = 0,
                    nombre = fullName,
                    password = password,
                    role = "PACIENTE",
                    usuario = username,
                    weight = 0.0
                )

                firestore.collection(USERS_COLLECTION)
                    .document(firebaseUser.uid)
                    .set(newUser)
                    .await()

                AuthResult.Success(firebaseUser)
            } else {
                AuthResult.Error("No se pudo crear el usuario")
            }
        } catch (e: Exception) {
            AuthResult.Error(e.localizedMessage ?: "Error al registrarse")
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUserUid(): String? {
        return firebaseAuth.currentUser?.uid
    }
}
