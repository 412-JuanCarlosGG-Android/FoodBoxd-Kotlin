package com.example.foodboxd.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Restaurant>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Restaurant>>> = _uiState

    init {
        fetchFavorites()
    }

    /**
     * @param silent si es `true` y ya hay datos cargados, no muestra el spinner
     * (refresco en segundo plano al volver a la pantalla).
     */
    fun fetchFavorites(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent || _uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            try {
                _uiState.value = UiState.Success(repository.getFavorites())
            } catch (e: Exception) {
                // En refresco silencioso conservamos los datos previos si falla.
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error("No se pudieron cargar tus favoritos. Revisa tu conexión.")
                }
            }
        }
    }

    fun removeFavorite(restaurantId: String) {
        val current = (_uiState.value as? UiState.Success)?.data ?: return
        // Actualización optimista: quitamos de la lista y confirmamos contra el backend.
        _uiState.value = UiState.Success(current.filterNot { it.id == restaurantId })
        viewModelScope.launch {
            runCatching { repository.setFavorite(restaurantId, isFavorite = false) }
                .onFailure { fetchFavorites() }
        }
    }
}
