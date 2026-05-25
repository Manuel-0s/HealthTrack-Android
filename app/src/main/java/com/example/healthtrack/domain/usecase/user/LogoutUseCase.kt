package com.example.healthtrack.domain.usecase.user

import com.example.healthtrack.domain.repository.UserRepository

class LogoutUseCase(private val repository: UserRepository) {
    operator fun invoke() {
        repository.logout()
    }
}
