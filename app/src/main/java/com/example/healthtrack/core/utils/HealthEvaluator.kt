package com.example.healthtrack.core.utils

object HealthEvaluator {

    fun getGlucoseStatus(value: Double): String = when {
        value < 70 -> "Baja — Tus niveles de azúcar están por debajo del rango recomendado. Intenta consumir un carbohidrato de rápida acción."
        value < 100 -> "Normal — ¡Excelente! Tus niveles de glucosa se encuentran en el rango óptimo y saludable."
        value < 126 -> "Prediabetes — Tus niveles están ligeramente elevados. Es una buena oportunidad para ajustar tus hábitos diarios."
        else -> "Alta — Nivel de azúcar elevado. Recuerda monitorear tu alimentación y mantener una hidratación adecuada."
    }

    fun getPressureStatus(systolic: Int, diastolic: Int): String = when {
        systolic < 120 && diastolic < 80 -> "Normal — Tu presión arterial está en un rango óptimo. Tu corazón bombea sangre con total comodidad."
        systolic < 130 && diastolic < 80 -> "Elevada — Tu presión sistólica está un poco por encima de lo ideal. Intenta moderar el consumo de sal."
        systolic < 140 || diastolic < 90 -> "Hipertensión N1 — Tus niveles muestran una presión moderadamente alta."
        else -> "Hipertensión N2 — Presión arterial significativamente alta. Evita esfuerzos intensos y consulta las indicaciones de tu médico."
    }

    fun getHeartRateStatus(value: Double): String = when {
        value < 60 -> "Bajo (Bradicardia) — Tu ritmo cardíaco es menor al promedio en reposo. Común en atletas, pero vigila si sientes fatiga."
        value <= 100 -> "Normal — Tu corazón late a un ritmo constante, ideal y saludable para un estado de reposo."
        else -> "Alto (Taquicardia) — Tu frecuencia cardíaca está acelerada. Puede deberse a actividad reciente, estrés, cafeína o deshidratación."
    }

    fun getIMCStatus(value: Double): String = when {
        value < 18.5 -> "Bajo peso — Tu peso se encuentra por debajo del rango recomendado para tu estatura. Prioriza nutrientes de calidad."
        value < 25.0 -> "Normal — ¡Felicidades! Tienes una relación excelente entre tu peso y tu estatura. Mantén la consistencia."
        value < 30.0 -> "Sobrepeso — Tu peso está ligeramente por encima de lo ideal. Estás a tiempo de realizar pequeños ajustes en tu rutina."
        else -> "Obesidad — Rango de peso elevado que incrementa el esfuerzo de tu cuerpo. Enfócate en metas de salud graduales y seguras."
    }
}