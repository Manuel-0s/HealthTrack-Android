package com.example.healthtrack.domain.model

enum class MetricsField(val label: String) {
    FREQUENCY("Frecuencia Cardíaca"),
    GLUCOSE("Glucosa"),
    BLOOD_PRESSURE("Presión Arterial"),
    SYSTOLIC_PRESSURE("Presión"),
    DIASTOLIC_PRESSURE("Presión diastólica"),
    IMC("IMC"),
    WEIGHT("Peso"),
    HEIGHT("Estatura"),
    NONE("")
}
