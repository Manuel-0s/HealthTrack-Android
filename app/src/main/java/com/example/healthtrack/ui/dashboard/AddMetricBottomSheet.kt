package com.example.healthtrack.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.healthtrack.R
import com.example.healthtrack.databinding.BottomsheetLayoutBinding
import com.example.healthtrack.domain.model.MetricsField
import com.example.healthtrack.domain.model.SaveMetricResult
import com.example.healthtrack.ui.home.HomeViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddMetricBottomSheet : BottomSheetDialogFragment() {

    private var _binding: BottomsheetLayoutBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: DashboardViewModel by viewModels()
    private val viewModelHome: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }

        binding.btnBack.setOnClickListener {
            showSelectionMenu()
            hideKeyboard()
        }

        binding.layoutBloodPressure.setOnClickListener {
            showForm(MetricsField.BLOOD_PRESSURE, getString(R.string.blood_pressure), "Sistólica (mmHg)", "Diastólica (mmHg)")
        }

        binding.layoutGlucose.setOnClickListener {
            showForm(MetricsField.GLUCOSE, getString(R.string.glucose), "Nivel (mg/dL)")
        }

        binding.layoutWeight.setOnClickListener {
            showForm(MetricsField.IMC, getString(R.string.body_data_bmi), "Peso (kg)", "Estatura (m)")
        }

        binding.layoutHeartRate.setOnClickListener {
            showForm(MetricsField.FREQUENCY, getString(R.string.heart_rate), "BPM")
        }
    }

    private fun setupObservers() {
        viewModel.saveStatus.observe(viewLifecycleOwner) { result ->
            when (result) {
                is SaveMetricResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSave.isEnabled = false
                }
                is SaveMetricResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true

                    Toast.makeText(requireContext(), getString(R.string.measurement_saved), Toast.LENGTH_SHORT).show()
                    viewModelHome.refresh()
                    showSelectionMenu()
                    viewModel.resetStatus()
                }
                is SaveMetricResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                    Toast.makeText(requireContext(), result.message, Toast.LENGTH_SHORT).show()
                    viewModel.resetStatus()
                }
                else -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnSave.isEnabled = true
                }
            }
        }
    }

    private fun showSelectionMenu() {
        binding.viewFlipper.displayedChild = 0
        binding.tvTitle.text = getString(R.string.register_measurement)
        binding.btnBack.visibility = View.GONE
    }

    private fun showForm(
        type: MetricsField,
        title: String,
        hint1: String,
        hint2: String? = null
    ) {
        binding.viewFlipper.displayedChild = 1
        binding.tvTitle.text = title
        binding.btnBack.visibility = View.VISIBLE

        binding.tilValue1.hint = hint1
        binding.etValue1.text?.clear()

        if (hint2 != null) {
            binding.tilValue2.visibility = View.VISIBLE
            binding.tilValue2.hint = hint2
            binding.etValue2.text?.clear()
        } else {
            binding.tilValue2.visibility = View.GONE
            binding.etValue2.text?.clear()
        }

        binding.btnSave.setOnClickListener {
            val input1 = binding.etValue1.text.toString()
            val input2 = binding.etValue2.text.toString()

            viewModel.onSaveRequested(type, input1, input2)

            hideKeyboard()
        }
    }

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "AddMetricBottomSheet"
        fun newInstance() = AddMetricBottomSheet()
    }
}
