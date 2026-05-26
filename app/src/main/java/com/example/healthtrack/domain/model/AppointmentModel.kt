package com.example.healthtrack.domain.model

import com.google.firebase.Timestamp

data class AppointmentModel(
    val id: String = "",
    val duracionMinutos: Long = 0,
    val estado: String = "",
    val fechaCita: Timestamp? = null,
    val medicoId: String = "",
    val motivo: String = "",
    val notas: String = "",
    val pacienteId: String = ""
)
