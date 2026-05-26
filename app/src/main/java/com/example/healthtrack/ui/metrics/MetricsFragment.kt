package com.example.healthtrack.ui.metrics

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtrack.R
import com.example.healthtrack.databinding.FragmentMetricsBinding
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class MetricsFragment : Fragment() {

    private var _binding: FragmentMetricsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MetricsViewModel by viewModels()
    private lateinit var historyAdapter: MetricsHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMetricsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupChips()
        setupChart()
        setupObservers()
        viewModel.loadMetricsHistory()
    }

    private fun setupRecyclerView() {
        historyAdapter = MetricsHistoryAdapter(emptyList())
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = historyAdapter
        }
    }

    private fun setupChips() {
        binding.chipGroupMetrics.setOnCheckedStateChangeListener { group, checkedIds ->
            val metric = when (checkedIds.firstOrNull()) {
                R.id.chipGlucose -> MetricsField.GLUCOSE
                R.id.chipHeartRate -> MetricsField.FREQUENCY
                R.id.chipIMC -> MetricsField.IMC
                R.id.chipPressure -> MetricsField.SYSTOLIC_PRESSURE
                else -> MetricsField.GLUCOSE
            }
            viewModel.selectMetric(metric)
            updateChipColors(checkedIds.firstOrNull())
        }
    }

    private fun updateChipColors(checkedId: Int?) {
        listOf(binding.chipGlucose, binding.chipHeartRate, binding.chipIMC, binding.chipPressure).forEach { chip ->
            val isSelected = chip.id == checkedId
            if (isSelected) {
                chip.setChipBackgroundColorResource(R.color.primary_green)
                chip.setTextColor(Color.WHITE)
            } else {
                chip.setChipBackgroundColorResource(R.color.chip_inactive_bg)
                chip.setTextColor(Color.GRAY)
            }
        }
    }

    private fun setupChart() {
        binding.lineChart.apply {
            description.isEnabled = false
            setTouchEnabled(true)
            isDragEnabled = true
            setScaleEnabled(true)
            setPinchZoom(true)
            setDrawGridBackground(false)
            
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.GRAY
                setDrawGridLines(false)
            }
            
            axisLeft.apply {
                textColor = Color.GRAY
                setDrawGridLines(true)
                gridColor = Color.parseColor("#33FFFFFF")
            }
            
            axisRight.isEnabled = false
            legend.isEnabled = false
        }
    }

    private fun setupObservers() {
        viewModel.metricsHistory.observe(viewLifecycleOwner) { history ->
            historyAdapter.updateData(history)
            updateStats(history)
            updateChartData(history)
        }

        viewModel.selectedMetric.observe(viewLifecycleOwner) { metric ->
            binding.tvChartSubtitle.text = "${metric.label} — últimos 7 días"
        }
    }

    private fun updateStats(history: List<Measurement>) {
        if (history.isEmpty()) {
            binding.tvAvgValue.text = "--"
            binding.tvLastValue.text = "--"
            binding.tvMinValue.text = "--"
            binding.tvMaxValue.text = "--"
            return
        }

        val values = history.mapNotNull { 
            when(it) {
                is Measurement.Glucose -> it.value
                is Measurement.HeartRate -> it.value
                is Measurement.IMC -> it.value
                is Measurement.SystolicPressure -> it.value.toDouble()
                is Measurement.DiastolicPressure -> it.value.toDouble()
                else -> null
            }
        }

        if (values.isNotEmpty()) {
            val avg = values.average()
            val last = values.first()
            val min = values.minOrNull() ?: 0.0
            val max = values.maxOrNull() ?: 0.0

            val metric = viewModel.selectedMetric.value ?: MetricsField.GLUCOSE
            val unit = when(metric) {
                MetricsField.GLUCOSE -> " mg/dL"
                MetricsField.FREQUENCY -> " BPM"
                MetricsField.IMC -> ""
                else -> " mmHg"
            }

            binding.tvAvgValue.text = "${String.format("%.1f", avg)}$unit"
            binding.tvLastValue.text = "${last.toInt()}$unit"
            binding.tvMinValue.text = "${min.toInt()}$unit"
            binding.tvMaxValue.text = "${max.toInt()}$unit"

            val lastStatus = getStatusForMetric(metric, last)
            binding.tvLastStatus.text = lastStatus
            binding.tvLastStatus.setTextColor(ContextCompat.getColor(requireContext(), getStatusColor(lastStatus)))

            val avgStatus = getStatusForMetric(metric, avg)
            binding.tvAvgStatus.text = avgStatus
            binding.tvAvgStatus.setTextColor(ContextCompat.getColor(requireContext(), getStatusColor(avgStatus)))
            
            val minStatus = getStatusForMetric(metric, min)
            binding.tvMinStatus.text = minStatus
            binding.tvMinStatus.setTextColor(ContextCompat.getColor(requireContext(), getStatusColor(minStatus)))

            val maxStatus = getStatusForMetric(metric, max)
            binding.tvMaxStatus.text = maxStatus
            binding.tvMaxStatus.setTextColor(ContextCompat.getColor(requireContext(), getStatusColor(maxStatus)))
        }
    }

    private fun getStatusForMetric(field: MetricsField, value: Double): String = when (field) {
        MetricsField.GLUCOSE -> when {
            value < 70 -> "Baja"
            value < 100 -> "Normal"
            value < 126 -> "Prediabetes"
            else -> "Alta"
        }
        MetricsField.FREQUENCY -> when {
            value < 60 -> "Bajo"
            value <= 100 -> "Normal"
            else -> "Alto"
        }
        MetricsField.IMC -> when {
            value < 18.5 -> "Bajo peso"
            value < 25.0 -> "Normal"
            value < 30.0 -> "Sobrepeso"
            else -> "Obesidad"
        }
        else -> "Registro"
    }

    private fun getStatusColor(status: String): Int = when (status) {
        "Óptimo" -> R.color.status_optimal
        "Normal" -> R.color.status_normal
        "Prediabetes", "Sobrepeso", "Elevada" -> R.color.status_elevated
        "Baja", "Bajo", "Bajo peso" -> R.color.lavender
        else -> R.color.status_high
    }

    private fun updateChartData(history: List<Measurement>) {
        val entries = mutableListOf<Entry>()
        val labels = mutableListOf<String>()
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())

        val sortedHistory = history.sortedBy { it.recordedAt }
        
        sortedHistory.forEachIndexed { index, measurement ->
            val value = when(measurement) {
                is Measurement.Glucose -> measurement.value
                is Measurement.HeartRate -> measurement.value
                is Measurement.IMC -> measurement.value
                is Measurement.SystolicPressure -> measurement.value.toDouble()
                is Measurement.DiastolicPressure -> measurement.value.toDouble()
                else -> 0.0
            }
            entries.add(Entry(index.toFloat(), value.toFloat()))
            labels.add(sdf.format(measurement.recordedAt.toDate()))
        }

        val dataSet = LineDataSet(entries, "Historial").apply {
            val chartColor = ContextCompat.getColor(requireContext(), R.color.chart_line)
            color = chartColor
            setCircleColor(chartColor)
            lineWidth = 2.5f
            circleRadius = 5f
            setDrawCircleHole(true)
            circleHoleColor = ContextCompat.getColor(requireContext(), R.color.bg_dark)
            valueTextColor = Color.TRANSPARENT
            mode = LineDataSet.Mode.CUBIC_BEZIER
            setDrawFilled(true)
            
            fillDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.chart_gradient)
        }

        binding.lineChart.apply {
            data = LineData(dataSet)
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            invalidate()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
