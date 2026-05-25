package com.example.healthtrack.domain.model

data class UserModel(
    val alertEmail: String? = null,
    val correo: String = "",
    val doctorAsignadoId: String? = null,
    val fechaNacimiento: String = "",
    val height: Double = 0.0,
    val imc: Double = 0.0,
    val maxGlucose: Int = 0,
    val maxHeartRate: Int = 0,
    val maxPressureDiastolica: Int = 0,
    val maxPressureSistolica: Int = 0,
    val nombre: String = "",
    val password: String = "",
    val role: String = "PACIENTE",
    val usuario: String? = null,
    val weight: Double = 0.0
)
