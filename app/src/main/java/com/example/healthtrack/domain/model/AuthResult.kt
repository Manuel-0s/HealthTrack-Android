package com.example.healthtrack.domain.model

import com.google.firebase.auth.FirebaseUser

sealed class AuthResult {
    data object Loading : AuthResult()
    data class Success(val user: FirebaseUser?) : AuthResult()

    data class Error(
        val message: String, 
        val field: AuthField = AuthField.NONE
    ) : AuthResult()
}
