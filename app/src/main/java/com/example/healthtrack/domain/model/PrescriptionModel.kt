package com.example.healthtrack.domain.model

import com.google.firebase.Timestamp

data class PrescriptionModel(
    val id: String = "",
    val medicamento: String = "",
    val dosis: String = "",
    val frecuencia: String = "",
    val duracion: String = "",
    val indicaciones: String = "",
    val fechaPrescripcion: Timestamp? = null,
    val medicoId: String = "",
    val pacienteId: String = ""
)
