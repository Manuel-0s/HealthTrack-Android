package com.example.healthtrack.ui.metrics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtrack.R
import com.example.healthtrack.databinding.ItemHistoryRecordBinding
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import java.text.SimpleDateFormat
import java.util.Locale

class MetricsHistoryAdapter(private var items: List<Measurement>) :
    RecyclerView.Adapter<MetricsHistoryAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemHistoryRecordBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryRecordBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context
        val sdf = SimpleDateFormat("EEE, hh:mm a", Locale.getDefault())

        val dateText = sdf.format(item.recordedAt.toDate())
        holder.binding.tvRecordDate.text = dateText

        when (item) {
            is Measurement.Glucose -> {
                holder.binding.tvMetricType.text = "Glucosa"
                holder.binding.tvMetricValue.text = "${item.value} mg/dL"
                holder.binding.viewTypeIndicator.backgroundTintList = ContextCompat.getColorStateList(context, R.color.lavender)
                setStatus(holder, getGlucoseStatus(item.value))
            }
            is Measurement.HeartRate -> {
                holder.binding.tvMetricType.text = "Frecuencia"
                holder.binding.tvMetricValue.text = "${item.value.toInt()} BPM"
                holder.binding.viewTypeIndicator.backgroundTintList = ContextCompat.getColorStateList(context, R.color.primary_green)
                setStatus(holder, getHeartRateStatus(item.value))
            }
            is Measurement.IMC -> {
                holder.binding.tvMetricType.text = "IMC"
                holder.binding.tvMetricValue.text = String.format("%.1f", item.value)
                holder.binding.viewTypeIndicator.backgroundTintList = ContextCompat.getColorStateList(context, R.color.orange)
                setStatus(holder, getIMCStatus(item.value))
            }
            is Measurement.SystolicPressure -> {
                holder.binding.tvMetricType.text = "Presión (S)"
                holder.binding.tvMetricValue.text = "${item.value} mmHg"
                holder.binding.viewTypeIndicator.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                setStatus(holder, "Registro") // Simplificado
            }
            is Measurement.DiastolicPressure -> {
                holder.binding.tvMetricType.text = "Presión (D)"
                holder.binding.tvMetricValue.text = "${item.value} mmHg"
                holder.binding.viewTypeIndicator.backgroundTintList = ContextCompat.getColorStateList(context, R.color.red)
                setStatus(holder, "Registro")
            }
        }
    }

    private fun setStatus(holder: ViewHolder, status: String) {
        holder.binding.tvRecordStatus.text = status
        val colorRes = when (status) {
            "Óptimo" -> R.color.status_optimal
            "Normal" -> R.color.status_normal
            "Prediabetes", "Sobrepeso", "Elevada" -> R.color.status_elevated
            "Baja", "Bajo", "Bajo peso" -> R.color.lavender
            else -> R.color.status_high
        }
        val color = ContextCompat.getColor(holder.itemView.context, colorRes)
        holder.binding.tvRecordStatus.setTextColor(color)
        holder.binding.cardStatus.setCardBackgroundColor(color.withAlpha(0.15f))
        holder.binding.viewTypeIndicator.backgroundTintList = android.content.res.ColorStateList.valueOf(color)
    }

    private fun Int.withAlpha(alpha: Float): Int {
        val a = (alpha * 255).toInt()
        return (this and 0x00FFFFFF) or (a shl 24)
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Measurement>) {
        items = newItems
        notifyDataSetChanged()
    }

    private fun getGlucoseStatus(value: Double): String = when {
        value < 70 -> "Baja"
        value < 100 -> "Normal"
        value < 126 -> "Prediabetes"
        else -> "Alta"
    }

    private fun getHeartRateStatus(value: Double): String = when {
        value < 60 -> "Bajo"
        value <= 100 -> "Normal"
        else -> "Alto"
    }

    private fun getIMCStatus(value: Double): String = when {
        value < 18.5 -> "Bajo peso"
        value < 25.0 -> "Normal"
        value < 30.0 -> "Sobrepeso"
        else -> "Obesidad"
    }
}
