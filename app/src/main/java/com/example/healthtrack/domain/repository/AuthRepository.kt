package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult
    suspend fun register(email: String, password: String, fullName: String, username: String): AuthResult
    fun logout()
    fun getCurrentUserUid(): String?
}
