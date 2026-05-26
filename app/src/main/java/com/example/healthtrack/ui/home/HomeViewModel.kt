package com.example.healthtrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.core.utils.HealthEvaluator
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.Recommendation
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.usecase.metrics.GetLatestHealthDataUseCase
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase

import kotlinx.coroutines.async
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

    private val _recommendations = MutableLiveData<List<Recommendation>>()
    val recommendations: LiveData<List<Recommendation>> get() = _recommendations

    private var dataLoaded = false

    fun loadHomeData() {
        if (dataLoaded) return
        dataLoaded = true

        viewModelScope.launch {
            try {
                val userDeferred = async { getUserDataUseCase() }
                val metricsDeferred = async { getLatestHealthDataUseCase() }

                val user = userDeferred.await()
                _userData.postValue(user)

                val metrics = metricsDeferred.await()
                _latestMetrics.postValue(metrics)
                updateRecommendations(metrics)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val metrics = getLatestHealthDataUseCase()
            _latestMetrics.postValue(metrics)
            updateRecommendations(metrics)
        }
    }
    private fun updateRecommendations(metrics: Map<MetricsField, Measurement?>) {
        val list = mutableListOf<Recommendation>()

        (metrics[MetricsField.IMC] as? Measurement.IMC)?.let {
            list.add(Recommendation("Control de IMC", "IMC: ${String.format("%.1f", it.value)}", HealthEvaluator.getIMCStatus(it.value)))
        }

        (metrics[MetricsField.GLUCOSE] as? Measurement.Glucose)?.let {
            list.add(Recommendation("Glucosa", "${it.value} mg/dL", HealthEvaluator.getGlucoseStatus(it.value)))
        }

        val systolic = metrics[MetricsField.SYSTOLIC_PRESSURE] as? Measurement.SystolicPressure
        val diastolic = metrics[MetricsField.DIASTOLIC_PRESSURE] as? Measurement.DiastolicPressure
        if (systolic != null && diastolic != null) {
            list.add(Recommendation("Presión Arterial", "${systolic.value}/${diastolic.value} mmHg", HealthEvaluator.getPressureStatus(systolic.value, diastolic.value)))
        }

        (metrics[MetricsField.FREQUENCY] as? Measurement.HeartRate)?.let {
            list.add(Recommendation("Ritmo Cardíaco", "${it.value.toInt()} BPM", HealthEvaluator.getHeartRateStatus(it.value)))
        }

        _recommendations.postValue(list)
    }
}
