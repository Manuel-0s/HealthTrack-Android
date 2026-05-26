package com.example.healthtrack.data.repository

import android.util.Log
import com.example.healthtrack.domain.model.PrescriptionModel
import com.example.healthtrack.domain.repository.PrescriptionRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class PrescriptionRepositoryImpl(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) : PrescriptionRepository {

    override fun getPrescriptionsForPatient(pacienteId: String): Flow<List<PrescriptionModel>> = callbackFlow {
        val subscription = firestore.collection("prescripciones")
            .whereEqualTo("pacienteId", pacienteId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("PrescriptionRepo", "Firestore error: ${error.message}")
                    close(error)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val prescriptions = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(PrescriptionModel::class.java)?.copy(id = doc.id)
                    }.sortedByDescending { it.fechaPrescripcion }
                    trySend(prescriptions)
                }
            }

        awaitClose { subscription.remove() }
    }
}
