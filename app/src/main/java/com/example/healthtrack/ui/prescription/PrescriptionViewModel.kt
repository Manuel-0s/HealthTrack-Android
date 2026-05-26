package com.example.healthtrack.ui.prescription

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.PrescriptionRepositoryImpl
import com.example.healthtrack.domain.model.PrescriptionModel
import com.example.healthtrack.domain.usecase.prescription.GetPrescriptionsUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class PrescriptionViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val repository = PrescriptionRepositoryImpl(FirebaseFirestore.getInstance())
    private val getPrescriptionsUseCase = GetPrescriptionsUseCase(repository)

    private val _prescriptions = MutableStateFlow<List<PrescriptionModel>>(emptyList())
    val prescriptions: StateFlow<List<PrescriptionModel>> = _prescriptions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadPrescriptions()
    }

    private fun loadPrescriptions() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _isLoading.value = true
            getPrescriptionsUseCase(userId)
                .catch { e ->
                    Log.e("PrescriptionViewModel", "Error loading prescriptions", e)
                    _isLoading.value = false
                }
                .collect { list ->
                    _prescriptions.value = list
                    _isLoading.value = false
                }
        }
    }
}
