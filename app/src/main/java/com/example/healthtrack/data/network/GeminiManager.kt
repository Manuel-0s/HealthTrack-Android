package com.example.healthtrack.data.network

import com.example.healthtrack.domain.model.Recommendation
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import org.json.JSONArray


class GeminiManager {

    private val model = Firebase.ai(backend = GenerativeBackend.googleAI())
        .generativeModel("gemini-3.5-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
            },
            systemInstruction = content { text("Eres un asistente experto en salud y bienestar para la aplicación HealthTrack. " +
                    "Tu tarea es recibir biomarcadores del usuario y devolver exactamente DOS consejos o alertas personalizados. " +
                    "Debes responder ÚNICAMENTE con un arreglo JSON con la siguiente estructura: " +
                    "[ " +
                    "  { \"title\": \"Título corto\", \"value\": \"Métrica\", \"advice\": \"Consejo breve.\" } " +
                    "]") }
            )

    suspend fun obtenerRecomendaciones(imc: String, glucosa: String, presion: String, ritmo: String): List<Recommendation> {
        val prompt = "Métricas actuales -> IMC: $imc, Glucosa: $glucosa, Presión: $presion, Ritmo: $ritmo BPM."
        android.util.Log.d("Gemini_Debug", "Prompt enviado: $prompt")

        return try {
            val response = model.generateContent(prompt)
            val jsonText = response.text
            android.util.Log.d("Gemini_Debug", "Respuesta RAW de Gemini: $jsonText")

            if (jsonText == null) return emptyList()

            val jsonArray = JSONArray(jsonText)
            val list = mutableListOf<Recommendation>()

            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(Recommendation(
                    title = obj.getString("title"),
                    value = obj.getString("value"),
                    advice = obj.getString("advice")
                ))
            }
            android.util.Log.d("Gemini_Debug", "Lista parseada: ${list.size} elementos")
            list
        } catch (e: Exception) {
            android.util.Log.e("Gemini_Debug", "Error en GeminiManager", e)
            emptyList()
        }
    }
}
