package com.example.healthtrack.domain.model

import com.google.firebase.Timestamp

sealed class Measurement {
    abstract val userId: String
    abstract val recordedAt: Timestamp
    abstract val metricType: String

    data class HeartRate(
        override val userId: String = "",
        override val recordedAt: Timestamp = Timestamp.now(),
        override val metricType: String = "",
        val value: Double = 0.0
    ) : Measurement()

    data class Glucose(
        override val userId: String = "",
        override val recordedAt: Timestamp = Timestamp.now(),
        override val metricType: String = "",
        val value: Double = 0.0
    ) : Measurement()

    data class IMC(
        override val userId: String = "",
        override val recordedAt: Timestamp = Timestamp.now(),
        override val metricType: String = "",
        val value: Double = 0.0
    ) : Measurement()

    data class SystolicPressure(
        override val userId: String = "",
        override val recordedAt: Timestamp = Timestamp.now(),
        override val metricType: String = "",
        val value: Int = 0
    ) : Measurement()

    data class DiastolicPressure(
        override val userId: String = "",
        override val recordedAt: Timestamp = Timestamp.now(),
        override val metricType: String = "",
        val value: Int = 0
    ) : Measurement()
}
