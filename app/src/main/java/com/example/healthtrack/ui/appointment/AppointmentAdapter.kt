package com.example.healthtrack.ui.appointment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtrack.databinding.ItemAppointmentRecordBinding
import com.example.healthtrack.domain.model.AppointmentModel
import java.text.SimpleDateFormat
import java.util.Locale

class AppointmentAdapter : ListAdapter<AppointmentModel, AppointmentAdapter.AppointmentViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val binding = ItemAppointmentRecordBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AppointmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AppointmentViewHolder(private val binding: ItemAppointmentRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        fun bind(appointment: AppointmentModel) {
            binding.tvAppointmentReason.text = appointment.motivo
            binding.tvAppointmentDateTime.text = appointment.fechaCita?.toDate()?.let { dateFormat.format(it) } ?: ""
            binding.tvAppointmentNotes.text = "Notas: ${appointment.notas}"
            binding.tvAppointmentStatusText.text = appointment.estado
            
            // Note: In a real app, you might want to change card colors based on status
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<AppointmentModel>() {
        override fun areItemsTheSame(oldItem: AppointmentModel, newItem: AppointmentModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: AppointmentModel, newItem: AppointmentModel): Boolean {
            return oldItem == newItem
        }
    }
}
