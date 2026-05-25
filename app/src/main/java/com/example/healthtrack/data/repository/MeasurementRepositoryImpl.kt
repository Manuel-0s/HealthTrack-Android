package com.example.healthtrack.data.repository

import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import com.example.healthtrack.domain.repository.MeasurementRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.tasks.await

class MeasurementRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : MeasurementRepository {

    companion object {
        private const val COLLECTION = "mediciones"
    }

    override suspend fun saveMeasurement(measurement: Measurement): SaveMetricResult {
        return try {
            firestore.collection(COLLECTION)
                .add(measurement)
                .await()

            SaveMetricResult.Success
        } catch (e: Exception) {
            SaveMetricResult.Error(e.message ?: "Error al guardar la medición en Firestore")
        }
    }

    override fun getLatestMeasurementFlow(userId: String, metricType: MetricsField): Flow<Measurement?> = callbackFlow {
        val listener = firestore.collection(COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("metricType", metricType.label)
            .orderBy("recordedAt", Query.Direction.DESCENDING)
            .limit(1)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("Firestore_Debug", "Error en listener de ${metricType.label}: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }
                
                val measurement = if (snapshot != null && !snapshot.isEmpty) {
                    mapDocumentToMeasurement(snapshot.documents[0], metricType)
                } else null
                
                trySend(measurement)
            }
        awaitClose { listener.remove() }
    }.onStart { emit(null) }

    private fun mapDocumentToMeasurement(document: DocumentSnapshot, metricType: MetricsField): Measurement? {
        return when (metricType) {
            MetricsField.FREQUENCY -> document.toObject(Measurement.HeartRate::class.java)
            MetricsField.GLUCOSE -> document.toObject(Measurement.Glucose::class.java)
            MetricsField.IMC -> document.toObject(Measurement.IMC::class.java)
            MetricsField.SYSTOLIC_PRESSURE -> document.toObject(Measurement.SystolicPressure::class.java)
            MetricsField.DIASTOLIC_PRESSURE -> document.toObject(Measurement.DiastolicPressure::class.java)
            else -> null
        }
    }
}
