package com.example.healthtrack.ui.auth.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.domain.usecase.auth.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {
    private val repository = AuthRepositoryImpl()
    private val registerUseCase = RegisterUseCase(repository)

    private val _authStatus = MutableLiveData<AuthResult>()
    val authStatus: LiveData<AuthResult> get() = _authStatus

    fun register(email: String, password: String, confirmPassword: String, fullName: String, username: String) {
        _authStatus.value = AuthResult.Loading
        viewModelScope.launch {
            val result = registerUseCase(email, password, confirmPassword, fullName, username)
            _authStatus.value = result
        }
    }
}
