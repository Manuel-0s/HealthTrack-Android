package com.example.healthtrack.domain.usecase.appointment

import com.example.healthtrack.domain.model.AppointmentModel
import com.example.healthtrack.domain.repository.AppointmentRepository
import kotlinx.coroutines.flow.Flow

class GetAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    operator fun invoke(pacienteId: String): Flow<List<AppointmentModel>> {
        return repository.getAppointmentsForPatient(pacienteId)
    }
}
