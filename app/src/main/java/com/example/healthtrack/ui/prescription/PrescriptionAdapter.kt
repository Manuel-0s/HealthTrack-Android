package com.example.healthtrack.ui.prescription

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtrack.databinding.ItemTreatmentRecordBinding
import com.example.healthtrack.domain.model.PrescriptionModel

class PrescriptionAdapter : ListAdapter<PrescriptionModel, PrescriptionAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTreatmentRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemTreatmentRecordBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PrescriptionModel) {
            binding.tvMedicineName.text = item.medicamento
            binding.tvMedicineDose.text = item.dosis
            binding.tvMedicineFrequency.text = item.frecuencia
            binding.tvMedicineDuration.text = item.duracion
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<PrescriptionModel>() {
        override fun areItemsTheSame(oldItem: PrescriptionModel, newItem: PrescriptionModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: PrescriptionModel, newItem: PrescriptionModel): Boolean {
            return oldItem == newItem
        }
    }
}
