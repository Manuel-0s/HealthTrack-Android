package com.example.healthtrack.domain.model

sealed class SaveMetricResult {
    data object Loading : SaveMetricResult()
    data object Success : SaveMetricResult()
    data class Error(
        val message: String,
        val field: MetricsField = MetricsField.NONE
    ) : SaveMetricResult()
}
