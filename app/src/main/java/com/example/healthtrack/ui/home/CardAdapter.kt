package com.example.healthtrack.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.healthtrack.R
import com.example.healthtrack.databinding.ItemCardBinding
import com.example.healthtrack.domain.model.Recommendation

class CardAdapter(private var items: List<Recommendation>) :
    RecyclerView.Adapter<CardAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCardBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.itemView.context

        holder.binding.tvCardTitle.text = item.title
        holder.binding.tvCardValue.text = item.value
        holder.binding.tvCardStatus.text = item.advice

        val (bgColor, titleColor, iconRes, iconColor) = when (position) {
            0 -> listOf(R.color.primary_green_light, R.color.primary_green, R.drawable.ic_bar_chart, R.color.primary_green)
            1 -> listOf(R.color.lavender_light, R.color.lavender, R.drawable.ic_glucose, R.color.lavender)
            2 -> listOf(R.color.red_light, R.color.red, R.drawable.ic_blood_pressure, R.color.red)
            else -> listOf(R.color.orange_light, R.color.orange, R.drawable.ic_monitor_heart, R.color.orange)
        }

        holder.binding.cardBackground.setBackgroundColor(ContextCompat.getColor(context, bgColor))
        holder.binding.tvCardTitle.setTextColor(ContextCompat.getColor(context, titleColor))
        holder.binding.ivCardIcon.setImageResource(iconRes)
        holder.binding.ivCardIcon.setColorFilter(ContextCompat.getColor(context, iconColor))
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<Recommendation>) {
        items = newItems
        notifyDataSetChanged()
    }
}