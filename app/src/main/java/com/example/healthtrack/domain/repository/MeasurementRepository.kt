package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun saveMeasurement(measurement: Measurement): SaveMetricResult
    fun getLatestMeasurementFlow(userId: String, metricType: MetricsField): Flow<Measurement?>
}
