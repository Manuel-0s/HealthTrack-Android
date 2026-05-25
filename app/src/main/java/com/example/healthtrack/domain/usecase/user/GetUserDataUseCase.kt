package com.example.healthtrack.domain.usecase.user

import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.repository.UserRepository

class GetUserDataUseCase(private val repository: UserRepository) {
    suspend operator fun invoke(): UserModel? {
        return repository.getUserData()
    }
}
