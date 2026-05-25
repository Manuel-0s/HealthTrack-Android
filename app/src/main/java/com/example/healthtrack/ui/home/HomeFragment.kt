package com.example.healthtrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.healthtrack.databinding.FragmentHomeBinding
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
        viewModel.loadHomeData()
    }

    private fun setupObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.tvWelcome.text = "Hola, ${it.nombre}"
            }
        }

        viewModel.latestMetrics.observe(viewLifecycleOwner) { metrics ->
            updateMetricsUI(metrics)
        }
    }

    private fun updateMetricsUI(metrics: Map<MetricsField, Measurement?>) {
        android.util.Log.d("Home_Debug", "Actualizando UI con ${metrics.size} métricas")

        val glucose = metrics[MetricsField.GLUCOSE] as? Measurement.Glucose
        binding.tvGlucoseValue.text = glucose?.let { "${it.value} mg/dL" } ?: "-- mg/dL"

        val systolic = metrics[MetricsField.SYSTOLIC_PRESSURE] as? Measurement.SystolicPressure
        val diastolic = metrics[MetricsField.DIASTOLIC_PRESSURE] as? Measurement.DiastolicPressure
        
        binding.tvPressureValue.text = if (systolic != null && diastolic != null) {
            "${systolic.value}/${diastolic.value}"
        } else {
            "--/--"
        }

        val heartRate = metrics[MetricsField.FREQUENCY] as? Measurement.HeartRate
        binding.tvHeartRateValue.text = heartRate?.let { "${it.value.toInt()} BPM" } ?: "-- BPM"

        val imc = metrics[MetricsField.IMC] as? Measurement.IMC
        val imcText = imc?.let { String.format("%.1f", it.value) } ?: "--"
        binding.tvIMCValue.text = "$imcText (IMC)"
        binding.tvWeightValue.text = imcText

        imc?.let {
            val status = when {
                it.value < 18.5 -> "Bajo peso"
                it.value < 25.0 -> "Normal"
                it.value < 30.0 -> "Sobrepeso"
                else -> "Obesidad"
            }
            binding.tvIMCStatus.text = "Tu estado es: $status"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
