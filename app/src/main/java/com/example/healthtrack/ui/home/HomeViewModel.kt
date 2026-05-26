package com.example.healthtrack.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.network.GeminiManager
import com.example.healthtrack.data.repository.AuthRepositoryImpl
import com.example.healthtrack.data.repository.MeasurementRepositoryImpl
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.Recommendation
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.usecase.metrics.GetLatestHealthDataUseCase
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private val userRepository = UserRepositoryImpl()
    private val measurementRepository = MeasurementRepositoryImpl()
    private val authRepository = AuthRepositoryImpl()
    private val geminiManager = GeminiManager()

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
                val user = getUserDataUseCase()
                _userData.postValue(user)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        viewModelScope.launch {
            val metrics = getLatestHealthDataUseCase()
            _latestMetrics.postValue(metrics)
            updateRecommendations(metrics)
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
        viewModelScope.launch {
            val imc = (metrics[MetricsField.IMC] as? Measurement.IMC)?.value?.toString() ?: "--"
            val glucosa = (metrics[MetricsField.GLUCOSE] as? Measurement.Glucose)?.value?.toString() ?: "--"
            val systolic = (metrics[MetricsField.SYSTOLIC_PRESSURE] as? Measurement.SystolicPressure)?.value?.toString() ?: "--"
            val diastolic = (metrics[MetricsField.DIASTOLIC_PRESSURE] as? Measurement.DiastolicPressure)?.value?.toString() ?: "--"
            val presion = "$systolic/$diastolic"
            val ritmo = (metrics[MetricsField.FREQUENCY] as? Measurement.HeartRate)?.value?.toString() ?: "--"

            //val recs = geminiManager.obtenerRecomendaciones(imc, glucosa, presion, ritmo)
            val recs = listOf(
                Recommendation(
                    title = "Control de IMC",
                    value = "IMC: 24.5",
                    advice = "¡Excelente! Te encuentras en tu peso ideal. Sigue manteniendo tus hábitos alimenticios actuales."
                ),
                Recommendation(
                    title = "Monitoreo de Glucosa",
                    value = "Glucosa: 135 mg/dL",
                    advice = "Tus niveles están algo elevados después de comer. Intenta caminar 15 minutos para estabilizarlos."
                )
            )

            _recommendations.postValue(recs)
        }
    }
}
