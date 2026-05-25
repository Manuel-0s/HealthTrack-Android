package com.example.healthtrack.domain.usecase.auth

import com.example.healthtrack.domain.model.AuthField
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.domain.repository.AuthRepository

class LoginUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): AuthResult {
        return when {
            email.isBlank() -> AuthResult.Error("El correo es obligatorio", AuthField.EMAIL)
            password.isBlank() -> AuthResult.Error("La contraseña es obligatoria", AuthField.PASSWORD)
            else -> repository.login(email, password)
        }
    }
}
