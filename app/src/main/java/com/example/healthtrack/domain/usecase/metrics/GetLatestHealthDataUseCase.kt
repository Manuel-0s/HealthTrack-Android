package com.example.healthtrack.domain.usecase.metrics

import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.repository.AuthRepository
import com.example.healthtrack.domain.repository.MeasurementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

class GetLatestHealthDataUseCase(
    private val repository: MeasurementRepository,
    private val authRepository: AuthRepository
) {
    private val mainMetrics = listOf(
        MetricsField.FREQUENCY,
        MetricsField.GLUCOSE,
        MetricsField.IMC,
        MetricsField.SYSTOLIC_PRESSURE,
        MetricsField.DIASTOLIC_PRESSURE
    )

    operator fun invoke(): Flow<Map<MetricsField, Measurement?>> {
        val userId = authRepository.getCurrentUserUid() ?: return flowOf(emptyMap())

        val flows = mainMetrics.map { field ->
            repository.getLatestMeasurementFlow(userId, field)
        }

        return combine(flows) { measurements ->
            mainMetrics.zip(measurements).toMap()
        }
    }
}
