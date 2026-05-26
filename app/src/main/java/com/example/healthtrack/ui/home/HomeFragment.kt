package com.example.healthtrack.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.healthtrack.R
import com.example.healthtrack.databinding.FragmentHomeBinding
import com.example.healthtrack.domain.model.Measurement
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.Recommendation

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var cardAdapter: CardAdapter
    private var dots = mutableListOf<ImageView>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupCarousel()
        viewModel.loadHomeData()
        setupObservers()
    }

    private fun setupCarousel() {
        cardAdapter = CardAdapter(emptyList())

        binding.viewPagerCarousel.apply {
            adapter = cardAdapter
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                page.translationX = position * -32.dpToPx()
            }
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    updateDots(position)
                }
            })
        }
    }

    private fun setupDots(count: Int) {
        binding.dotsContainer.removeAllViews()
        dots.clear()

        repeat(count) {
            val dot = ImageView(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(8.dpToPx(), 8.dpToPx()).apply {
                    setMargins(4.dpToPx(), 0, 4.dpToPx(), 0)
                }
                setImageResource(R.drawable.dot_inactive)
            }
            binding.dotsContainer.addView(dot)
            dots.add(dot)
        }

        if (dots.isNotEmpty()) {
            dots[0].setImageResource(R.drawable.dot_active)
        }
    }

    private fun updateDots(position: Int) {
        dots.forEachIndexed { index, dot ->
            dot.setImageResource(
                if (index == position) R.drawable.dot_active
                else R.drawable.dot_inactive
            )
        }
    }

    private fun Int.dpToPx(): Int =
        (this * resources.displayMetrics.density).toInt()

    private fun setupObservers() {
        viewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let { binding.tvWelcome.text = "Hola, ${it.nombre}" }
        }

        viewModel.latestMetrics.observe(viewLifecycleOwner) { metrics ->
            updateMetricsUI(metrics)
        }

        viewModel.recommendations.observe(viewLifecycleOwner) { recommendations ->
            cardAdapter.updateData(recommendations)
            setupDots(recommendations.size)
        }
    }

    private fun updateMetricsUI(metrics: Map<MetricsField, Measurement?>) {
        val glucose = metrics[MetricsField.GLUCOSE] as? Measurement.Glucose
        binding.tvGlucoseValue.text = glucose?.let { "${it.value} mg/dL" } ?: "-- mg/dL"
        binding.tvGlucoseStatus.text = glucose?.let { getStatusForMetric(MetricsField.GLUCOSE, it.value) } ?: ""

        val systolic = metrics[MetricsField.SYSTOLIC_PRESSURE] as? Measurement.SystolicPressure
        val diastolic = metrics[MetricsField.DIASTOLIC_PRESSURE] as? Measurement.DiastolicPressure
        binding.tvPressureValue.text = if (systolic != null && diastolic != null) {
            "${systolic.value}/${diastolic.value}"
        } else "--/--"
        binding.tvPressureStatus.text = if (systolic != null && diastolic != null) {
            getPressureStatus(systolic.value, diastolic.value)
        } else ""

        val heartRate = metrics[MetricsField.FREQUENCY] as? Measurement.HeartRate
        binding.tvHeartRateValue.text = heartRate?.let { "${it.value.toInt()} BPM" } ?: "-- BPM"
        binding.tvHeartRateStatus.text = heartRate?.let { getStatusForMetric(MetricsField.FREQUENCY, it.value) } ?: ""

        val imc = metrics[MetricsField.IMC] as? Measurement.IMC
        binding.tvWeightValue.text = imc?.let { String.format("%.1f", it.value) } ?: "--"
        binding.tvWeightStatus.text = imc?.let { getStatusForMetric(MetricsField.IMC, it.value) } ?: ""
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
        else -> ""
    }

    private fun getPressureStatus(systolic: Int, diastolic: Int): String = when {
        systolic < 120 && diastolic < 80 -> "Normal"
        systolic < 130 && diastolic < 80 -> "Elevada"
        systolic < 140 || diastolic < 90 -> "Hipertensión N1"
        else -> "Hipertensión N2"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}