package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.PrescriptionModel
import kotlinx.coroutines.flow.Flow

interface PrescriptionRepository {
    fun getPrescriptionsForPatient(pacienteId: String): Flow<List<PrescriptionModel>>
}
