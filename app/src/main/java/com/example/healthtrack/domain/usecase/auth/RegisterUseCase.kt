package com.example.healthtrack.domain.usecase.auth

import com.example.healthtrack.domain.model.AuthField
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.domain.repository.AuthRepository

class RegisterUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String, confirmPassword: String, fullName: String, username: String): AuthResult {
        return when {
            email.isBlank() -> 
                AuthResult.Error("El correo es obligatorio", AuthField.EMAIL)
            password.isBlank() -> 
                AuthResult.Error("La contraseña es obligatoria", AuthField.PASSWORD)
            password != confirmPassword -> 
                AuthResult.Error("Las contraseñas no coinciden", AuthField.CONFIRM_PASSWORD)
            password.length < 8 ->
                AuthResult.Error("La contraseña debe tener al menos 8 caracteres", AuthField.PASSWORD)
            fullName.isBlank() -> 
                AuthResult.Error("El nombre es obligatorio", AuthField.FULL_NAME)
            username.isBlank() ->
                AuthResult.Error("El nombre de usuario es obligatorio", AuthField.USERNAME)
            else -> repository.register(email, password, fullName, username)
        }
    }
}
