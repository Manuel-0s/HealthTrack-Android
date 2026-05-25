package com.example.healthtrack.ui.dashboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import com.example.healthtrack.domain.usecase.metrics.SaveMeasurementUseCase
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {
    private val repository = MeasurementRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()
    private val saveMeasurementUseCase = SaveMeasurementUseCase(repository, authRepository)

    private val _saveStatus = MutableLiveData<SaveMetricResult?>()
    val saveStatus: LiveData<SaveMetricResult?> get() = _saveStatus

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
