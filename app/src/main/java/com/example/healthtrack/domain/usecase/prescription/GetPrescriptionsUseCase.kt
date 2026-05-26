package com.example.healthtrack.domain.usecase.prescription

import com.example.healthtrack.domain.model.PrescriptionModel
import com.example.healthtrack.domain.repository.PrescriptionRepository
import kotlinx.coroutines.flow.Flow

class GetPrescriptionsUseCase(
    private val repository: PrescriptionRepository
) {
    operator fun invoke(pacienteId: String): Flow<List<PrescriptionModel>> {
        return repository.getPrescriptionsForPatient(pacienteId)
    }
}
