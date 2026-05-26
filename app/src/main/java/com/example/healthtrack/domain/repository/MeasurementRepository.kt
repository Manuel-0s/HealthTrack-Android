package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import kotlinx.coroutines.flow.Flow

interface MeasurementRepository {
    suspend fun saveMeasurement(measurement: Measurement): SaveMetricResult
    suspend fun getLatestMeasurement(userId: String, metricType: MetricsField): Measurement?
    suspend fun getRecentMeasurements(userId: String, limit: Long): List<Measurement>
}
