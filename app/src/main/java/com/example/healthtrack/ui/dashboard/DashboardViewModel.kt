package com.example.healthtrack.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import com.example.healthtrack.domain.usecase.metrics.SaveMeasurementUseCase
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase
import com.example.healthtrack.domain.usecase.user.LogoutUseCase
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = MeasurementRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()
    private val userRepository = UserRepositoryImpl()
    private val saveMeasurementUseCase = SaveMeasurementUseCase(repository, authRepository)
    private val getUserDataUseCase = GetUserDataUseCase(userRepository)
    private val logoutUseCase = LogoutUseCase(userRepository)

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> get() = _loggedOut

    private val _saveStatus = MutableLiveData<SaveMetricResult?>()
    val saveStatus: LiveData<SaveMetricResult?> get() = _saveStatus

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

    fun onSaveRequested(type : MetricsField, val1 : String, val2 : String? = null) {
        _saveStatus.value = SaveMetricResult.Loading
        viewModelScope.launch {
            val result = saveMeasurementUseCase(type, val1, val2)

            _saveStatus.value = result
        }
    }

    fun resetStatus() {
        _saveStatus.value = null
    }
}
