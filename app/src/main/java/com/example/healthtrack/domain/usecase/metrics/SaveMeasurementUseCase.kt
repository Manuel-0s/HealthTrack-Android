package com.example.healthtrack.domain.usecase.metrics

import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import com.example.healthtrack.domain.repository.AuthRepository
import com.example.healthtrack.domain.repository.MeasurementRepository
import com.google.firebase.Timestamp
import java.util.Date
import java.util.Locale

class SaveMeasurementUseCase(
    private val repository: MeasurementRepository,
    private val authRepository: AuthRepository,
) {

    suspend operator fun invoke(type: MetricsField, val1: String, val2: String?): SaveMetricResult {
        val userId = authRepository.getCurrentUserUid()
            ?: return SaveMetricResult.Error("No se pudo obtener el ID del usuario")
            
        val n1 = val1.toDoubleOrNull() ?: return SaveMetricResult.Error("El valor principal no es un número válido")
        
        val errorResult = validateBusinessRules(type, n1, val2)
        if (errorResult != null) return errorResult
        
        val timestamp = Timestamp.now()
        
        return when (type) {
            MetricsField.BLOOD_PRESSURE -> {
                val diastolicValue = val2?.toIntOrNull() ?: 0
                val systolic = Measurement.SystolicPressure(userId, timestamp, MetricsField.SYSTOLIC_PRESSURE.label, n1.toInt())
                val diastolic = Measurement.DiastolicPressure(userId, timestamp, MetricsField.DIASTOLIC_PRESSURE.label, diastolicValue)
                
                val resSystolic = repository.saveMeasurement(systolic)
                if (resSystolic is SaveMetricResult.Error) return resSystolic
                
                repository.saveMeasurement(diastolic)
            }
            MetricsField.IMC -> {
                val heightValue = val2?.toDoubleOrNull() ?: 1.0
                val imcValue = n1 / (heightValue * heightValue)
                
                val imcMeasurement = Measurement.IMC(userId, timestamp, type.label, imcValue)
                repository.saveMeasurement(imcMeasurement)
            }
            MetricsField.GLUCOSE -> {
                val glucose = Measurement.Glucose(userId, timestamp, type.label, n1)
                repository.saveMeasurement(glucose)
            }
            MetricsField.FREQUENCY -> {
                val heartRate = Measurement.HeartRate(userId, timestamp, type.label, n1)
                repository.saveMeasurement(heartRate)
            }
            else -> SaveMetricResult.Error("Tipo de métrica no soportado")
        }
    }

    private fun validateBusinessRules(type: MetricsField, n1: Double, val2: String?): SaveMetricResult.Error? {
        return when (type) {
            MetricsField.BLOOD_PRESSURE -> {
                val n2 = val2?.toIntOrNull()
                if (n1 < 50 || n1 > 250) SaveMetricResult.Error("La presión sistólica debe estar entre 50 y 250 mmHg", MetricsField.SYSTOLIC_PRESSURE)
                else if (n2 == null) SaveMetricResult.Error("Se requiere el valor de la presión diastólica", MetricsField.DIASTOLIC_PRESSURE)
                else if (n2 < 30 || n2 > 150) SaveMetricResult.Error("La presión diastólica debe estar entre 30 y 150 mmHg", MetricsField.DIASTOLIC_PRESSURE)
                else null
            }
            MetricsField.IMC -> {
                val n2 = val2?.toDoubleOrNull()
                if (n1 < 2.0 || n1 > 500.0) SaveMetricResult.Error("El peso debe estar entre 2 y 500 kg", MetricsField.WEIGHT)
                else if (n2 == null) SaveMetricResult.Error("Se requiere la estatura", MetricsField.HEIGHT)
                else if (n2 < 0.5 || n2 > 2.5) SaveMetricResult.Error("La estatura debe estar entre 0.5 y 2.5 metros", MetricsField.HEIGHT)
                else null
            }
            MetricsField.GLUCOSE -> {
                if (n1 < 20.0 || n1 > 600.0) SaveMetricResult.Error("El nivel de glucosa debe estar entre 20 y 600 mg/dL", MetricsField.GLUCOSE)
                else null
            }
            MetricsField.FREQUENCY -> {
                if (n1 < 30.0 || n1 > 250.0) SaveMetricResult.Error("La frecuencia cardíaca debe estar entre 30 y 250 BPM", MetricsField.FREQUENCY)
                else null
            }
            else -> null
        }
    }
}
