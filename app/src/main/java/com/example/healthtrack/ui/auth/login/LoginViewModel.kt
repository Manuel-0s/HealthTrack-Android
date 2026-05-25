package com.example.healthtrack.ui.auth.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.domain.model.AuthResult
import com.example.healthtrack.domain.usecase.auth.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepositoryImpl()
    private val loginUseCase = LoginUseCase(repository)

    private val _authStatus = MutableLiveData<AuthResult>()
    val authStatus: LiveData<AuthResult> get() = _authStatus

    fun login(email: String, password: String) {
        _authStatus.value = AuthResult.Loading
        viewModelScope.launch {
            val result = loginUseCase(email, password)
            _authStatus.value = result
        }
    }
}
