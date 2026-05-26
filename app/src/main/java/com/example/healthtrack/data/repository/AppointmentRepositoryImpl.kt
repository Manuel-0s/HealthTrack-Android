package com.example.healthtrack.data.repository

import com.example.healthtrack.domain.model.AppointmentModel
import com.example.healthtrack.domain.repository.AppointmentRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

import android.util.Log

class AppointmentRepositoryImpl(
    private val firestore: FirebaseFirestore
) : AppointmentRepository {

    override fun getAppointmentsForPatient(pacienteId: String): Flow<List<AppointmentModel>> = callbackFlow {
        val subscription = firestore.collection("citas_medicas")
            .whereEqualTo("pacienteId", pacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val appointments = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(AppointmentModel::class.java)?.copy(id = doc.id)
                    }.sortedByDescending { it.fechaCita }
                    trySend(appointments)
                }
            }

        awaitClose { subscription.remove() }
    }
}
