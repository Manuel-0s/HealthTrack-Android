package com.example.healthtrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.usecase.metrics.GetLatestHealthDataUseCase
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()
    private val measurementRepository = MeasurementRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()

    private val getUserDataUseCase = GetUserDataUseCase(userRepository)
    private val getLatestHealthDataUseCase = GetLatestHealthDataUseCase(measurementRepository, authRepository)

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    private val _latestMetrics = MutableLiveData<Map<MetricsField, Measurement?>>()
    val latestMetrics: LiveData<Map<MetricsField, Measurement?>> get() = _latestMetrics

    fun loadHomeData() {
        viewModelScope.launch {
            android.util.Log.d("Home_Debug", "Iniciando carga de datos en ViewModel")
            val user = getUserDataUseCase()
            android.util.Log.d("Home_Debug", "Usuario obtenido: ${user?.nombre}")
            _userData.value = user

            getLatestHealthDataUseCase().collectLatest { metrics ->
                android.util.Log.d("Home_Debug", "Flow emitió ${metrics.size} métricas")
                _latestMetrics.value = metrics
            }
        }
    }
}
