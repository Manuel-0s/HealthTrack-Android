package com.example.healthtrack.ui.prescription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtrack.databinding.FragmentPrescriptionBinding
import kotlinx.coroutines.launch

class PrescriptionFragment : Fragment() {
    private var _binding: FragmentPrescriptionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PrescriptionViewModel by viewModels()
    private val adapter = PrescriptionAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.rvTreatments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTreatments.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.prescriptions.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}