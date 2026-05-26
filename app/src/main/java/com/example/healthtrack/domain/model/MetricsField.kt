package com.example.healthtrack.domain.model

enum class MetricsField(val label: String) {
    FREQUENCY("Frecuencia cardiaca"),
    GLUCOSE("Glucosa"),
    BLOOD_PRESSURE("Presion Arterial"),
    SYSTOLIC_PRESSURE("Presion sistolica"),
    DIASTOLIC_PRESSURE("Presion diastolica"),

    IMC("IMC"),
    WEIGHT("Peso"),
    HEIGHT("Estatura"),
    NONE("")
}
