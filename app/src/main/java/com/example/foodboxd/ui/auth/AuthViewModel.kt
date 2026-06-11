package com.example.foodboxd.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

/** Estado de las pantallas de autenticación (login y registro). */
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)

/**
 * ViewModel compartido por Login y Registro. Habla con el backend
 * (`/api/users/login` y `/api/users/register`) y guarda la sesión al tener éxito.
 */
class AuthViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa correo y contraseña")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                repository.login(email.trim(), password)
                _uiState.value = AuthUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = messageFor(e, "Credenciales inválidas"))
            }
        }
    }

    fun register(name: String, email: String, password: String) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState(error = "Completa todos los campos")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState(error = "La contraseña debe tener al menos 6 caracteres")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                repository.register(name.trim(), email.trim(), password)
                _uiState.value = AuthUiState(success = true)
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = messageFor(e, "No se pudo crear la cuenta"))
            }
        }
    }

    fun consumeError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    private fun messageFor(e: Exception, fallback: String): String = when (e) {
        is HttpException -> when (e.code()) {
            400, 401 -> fallback
            else -> "Error del servidor (${e.code()})"
        }
        else -> "No hay conexión con el servidor. ¿Está corriendo el backend?"
    }
}
