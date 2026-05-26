package com.example.healthtrack.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.healthtrack.data.repository.UserRepositoryImpl
import com.example.healthtrack.domain.model.UserModel
import com.example.healthtrack.domain.usecase.user.GetUserDataUseCase
import com.example.healthtrack.domain.usecase.user.LogoutUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val repository = UserRepositoryImpl()
    private val getUserDataUseCase = GetUserDataUseCase(repository)
    private val logoutUseCase = LogoutUseCase(repository)

    private val _userData = MutableLiveData<UserModel?>()
    val userData: LiveData<UserModel?> get() = _userData

    private val _loggedOut = MutableLiveData<Boolean>()
    val loggedOut: LiveData<Boolean> get() = _loggedOut

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    fun loadUserData() {
        if (_userData.value != null) return
        
        viewModelScope.launch {
            _isLoading.value = true
            val user = getUserDataUseCase()
            _userData.value = user
            _isLoading.value = false
        }
    }

    fun updateUserName(nombre: String) {
        updateField(mapOf("nombre" to nombre)) { it.copy(nombre = nombre) }
    }

    fun updateUserEmail(correo: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.updateAuthEmail(correo)
                
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                repository.updateStats(uid, mapOf("correo" to correo))
                
                _userData.value?.let {
                    _userData.value = it.copy(correo = correo)
                }
                _message.value = "Se ha enviado un correo de verificación a la nueva dirección"
            } catch (e: Exception) {
                val errorMsg = when (e) {
                    is com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException -> 
                        "Por seguridad, debes cerrar sesión e iniciarla de nuevo para cambiar tu correo."
                    else -> "Error al actualizar correo: ${e.message}"
                }
                _message.value = errorMsg
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateBirthDate(fecha: String) {
        updateField(mapOf("fechaNacimiento" to fecha)) { it.copy(fechaNacimiento = fecha) }
    }

    fun updateHeight(estatura: Int) {
        val heightDouble = estatura.toDouble()
        updateField(mapOf("height" to heightDouble)) { it.copy(height = heightDouble) }
    }

    fun updateWeight(peso: Double) {
        updateField(mapOf("weight" to peso)) { it.copy(weight = peso) }
    }

    private fun updateField(updates: Map<String, Any>, updateLocal: (UserModel) -> UserModel) {
        viewModelScope.launch {
            val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            _isLoading.value = true
            try {
                repository.updateStats(uid, updates)
                _userData.value?.let {
                    _userData.value = updateLocal(it)
                }
                _message.value = "Datos actualizados correctamente"
            } catch (e: Exception) {
                _message.value = "Error al actualizar: ${e.message}"
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut() {
        logoutUseCase()
        _loggedOut.value = true
    }

    fun logout() {
        signOut()
    }
}
