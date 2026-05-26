package com.example.healthtrack.ui.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.healthtrack.databinding.FragmentAppointmentBinding
import kotlinx.coroutines.launch

class AppointmentFragment : Fragment() {
    private var _binding: FragmentAppointmentBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AppointmentViewModel by viewModels()
    private val adapter = AppointmentAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppointmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupObservers()
    }

    private fun setupRecyclerView() {
        binding.rvAppointments.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAppointments.adapter = adapter
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.appointments.collect { list ->
                adapter.submitList(list)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
