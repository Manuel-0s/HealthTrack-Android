package com.example.healthtrack.ui.metrics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import kotlinx.coroutines.launch

class MetricsViewModel : ViewModel() {
    private val measurementRepository = MeasurementRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()

    private val _metricsHistory = MutableLiveData<List<Measurement>>()
    val metricsHistory: LiveData<List<Measurement>> get() = _metricsHistory

    private val _selectedMetric = MutableLiveData<MetricsField>(MetricsField.GLUCOSE)
    val selectedMetric: LiveData<MetricsField> get() = _selectedMetric

    fun selectMetric(metric: MetricsField) {
        _selectedMetric.value = metric
        loadMetricsHistory()
    }

    fun loadMetricsHistory() {
        val userId = authRepository.getCurrentUserUid() ?: return
        val currentMetric = _selectedMetric.value ?: MetricsField.GLUCOSE

        viewModelScope.launch {
            val allRecent = measurementRepository.getRecentMeasurements(userId, 100)
            val filtered = allRecent.filter {
                when(currentMetric) {
                    MetricsField.GLUCOSE -> it is Measurement.Glucose
                    MetricsField.FREQUENCY -> it is Measurement.HeartRate
                    MetricsField.IMC -> it is Measurement.IMC
                    MetricsField.SYSTOLIC_PRESSURE -> it is Measurement.SystolicPressure || it is Measurement.DiastolicPressure
                    else -> false
                }
            }
            _metricsHistory.postValue(filtered)
        }
    }
}
