package com.example.healthtrack.data.repository

import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : UserRepository {

    companion object {
        private const val USERS_COLLECTION = "usuarios"
        private var cachedUser: UserModel? = null
    }

    override suspend fun getUserData(): UserModel? {
        val uid = auth.currentUser?.uid ?: return null

        cachedUser?.let { return it }

        return try {
            val document = firestore.collection(USERS_COLLECTION).document(uid).get().await()
            val user = if (document.exists()) {
                document.toObject(UserModel::class.java)
            } else {
                null
            }
            cachedUser = user
            user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateStats(uid: String, updates: Map<String, Any>) {
        firestore.collection(USERS_COLLECTION).document(uid).update(updates).await()
        cachedUser = null
    }

    override suspend fun updateAuthEmail(newEmail: String) {
        auth.currentUser?.verifyBeforeUpdateEmail(newEmail)?.await()
        cachedUser = null
    }

    override fun logout() {
        auth.signOut()
        cachedUser = null
    }
}
