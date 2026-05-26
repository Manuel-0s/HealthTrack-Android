package com.example.healthtrack.ui.appointment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.domain.model.AppointmentModel
import com.example.healthtrack.domain.usecase.appointment.GetAppointmentsUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

import com.example.healthtrack.data.repository.AppointmentRepositoryImpl
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.flow.catch
import android.util.Log

class AppointmentViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val repository = AppointmentRepositoryImpl(FirebaseFirestore.getInstance())
    private val getAppointmentsUseCase = GetAppointmentsUseCase(repository)

    private val _appointments = MutableStateFlow<List<AppointmentModel>>(emptyList())
    val appointments: StateFlow<List<AppointmentModel>> = _appointments.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAppointments()
    }

    private fun loadAppointments() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getAppointmentsUseCase(userId)
                .catch { e ->
                    _isLoading.value = false
                }
                .collect { list ->
                    _appointments.value = list
                    _isLoading.value = false
                }
        }
    }
}
