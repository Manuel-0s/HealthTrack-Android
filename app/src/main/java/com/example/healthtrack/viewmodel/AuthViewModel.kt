package com.example.healthtrack.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.model.AuthResult
import com.example.healthtrack.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()
    private val _authStatus = MutableLiveData<AuthResult>()
    val authStatus: LiveData<AuthResult> get() = _authStatus

    fun login(email: String, password: String) {
        _authStatus.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _authStatus.value = result
        }
    }

    fun register(email: String, password: String, fullName: String) {
        _authStatus.value = AuthResult.Loading
        viewModelScope.launch {
            val result = repository.register(email, password, fullName)
            _authStatus.value = result
        }
    }
}