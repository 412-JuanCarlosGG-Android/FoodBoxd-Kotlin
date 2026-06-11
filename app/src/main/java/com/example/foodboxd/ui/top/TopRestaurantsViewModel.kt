package com.example.foodboxd.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopRestaurantsViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Restaurant>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Restaurant>>> = _uiState

    init {
        fetchTopRestaurants()
    }

    fun fetchTopRestaurants(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent || _uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            try {
                _uiState.value = UiState.Success(repository.getRanking())
            } catch (e: Exception) {
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error("No se pudieron cargar los restaurantes. Revisa tu conexión.")
                }
            }
        }
    }
}
