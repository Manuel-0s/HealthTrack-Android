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
        val cards = listOf(
            Recommendation("Control de IMC", "IMC: 24.5", "¡En tu peso ideal! Sigue así."),
            Recommendation("Monitoreo de Glucosa", "Glucosa: 135 mg/dL", "Niveles algo elevados. Camina 15 min."),
            Recommendation("Presión Arterial", "120/80 mmHg", "Presión normal. ¡Excelente!"),
            Recommendation("Ritmo Cardíaco", "72 BPM", "Frecuencia ideal en reposo.")
        )

        cardAdapter = CardAdapter(cards)

        binding.viewPagerCarousel.apply {
            adapter = cardAdapter
            offscreenPageLimit = 1
            setPageTransformer { page, position ->
                page.translationX = position * -32.dpToPx()
            }
        }

        setupDots(cards.size)
    }

    private fun setupDots(count: Int) {
        val dots = mutableListOf<ImageView>()

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

        dots[0].setImageResource(R.drawable.dot_active)

        binding.viewPagerCarousel.registerOnPageChangeCallback(
            object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    dots.forEachIndexed { index, dot ->
                        dot.setImageResource(
                            if (index == position) R.drawable.dot_active
                            else R.drawable.dot_inactive
                        )
                    }
                }
            }
        )
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
    }

    private fun updateMetricsUI(metrics: Map<MetricsField, Measurement?>) {
        val glucose = metrics[MetricsField.GLUCOSE] as? Measurement.Glucose
        binding.tvGlucoseValue.text = glucose?.let { "${it.value} mg/dL" } ?: "-- mg/dL"

        val systolic = metrics[MetricsField.SYSTOLIC_PRESSURE] as? Measurement.SystolicPressure
        val diastolic = metrics[MetricsField.DIASTOLIC_PRESSURE] as? Measurement.DiastolicPressure
        binding.tvPressureValue.text = if (systolic != null && diastolic != null) {
            "${systolic.value}/${diastolic.value}"
        } else "--/--"

        val heartRate = metrics[MetricsField.FREQUENCY] as? Measurement.HeartRate
        binding.tvHeartRateValue.text = heartRate?.let { "${it.value.toInt()} BPM" } ?: "-- BPM"

        val imc = metrics[MetricsField.IMC] as? Measurement.IMC
        binding.tvWeightValue.text = imc?.let { String.format("%.1f", it.value) } ?: "--"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}