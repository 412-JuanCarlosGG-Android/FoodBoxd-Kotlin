package com.example.foodboxd.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Contenido que se muestra en la pantalla de inicio: restaurantes destacados
 * y las reseñas más recientes publicadas por la comunidad.
 */
data class HomeContent(
    val featured: List<Restaurant>,
    val latestReviews: List<Review>
)

class HomeViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeContent>> = _uiState

    init {
        fetchHome()
    }

    fun fetchHome(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent || _uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            try {
                val home = repository.getHomeContent()
                _uiState.value = UiState.Success(
                    HomeContent(featured = home.featured, latestReviews = home.latestReviews)
                )
            } catch (e: Exception) {
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error("No se pudo cargar el inicio. Revisa tu conexión.")
                }
            }
        }
    }
}
