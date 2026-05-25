package com.example.healthtrack.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase
import com.example.healthtrack.domain.usecase.user.LogoutUseCase
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserRepositoryImpl()
    private val getUserDataUseCase = GetUserDataUseCase(repository)
    private val logoutUseCase = LogoutUseCase(repository)

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> get() = _loggedOut

    fun loadUserData() {
        viewModelScope.launch {
            val user = getUserDataUseCase()
            _userData.value = user
        }
    }

    fun logout() {
        logoutUseCase()
        _loggedOut.value = true
    }
}
