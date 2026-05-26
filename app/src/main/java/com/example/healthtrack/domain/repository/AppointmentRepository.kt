package com.example.healthtrack.domain.repository

import com.example.healthtrack.domain.model.AppointmentModel
import kotlinx.coroutines.flow.Flow

interface AppointmentRepository {
    fun getAppointmentsForPatient(pacienteId: String): Flow<List<AppointmentModel>>
}
