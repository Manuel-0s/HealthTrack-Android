package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.UserModel

interface UserRepository {
    suspend fun getUserData(): UserModel?
    suspend fun updateStats(uid: String, updates: Map<String, Any>)
    suspend fun updateAuthEmail(newEmail: String)
    fun logout()
}
