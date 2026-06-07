package com.example.foodboxd.ui.top

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TopRestaurantsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<Restaurant>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<Restaurant>>> = _uiState

    init {
        fetchTopRestaurants()
    }

    fun fetchTopRestaurants() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            delay(2000)

            try {
                val mockData = listOf(
                    Restaurant(id = "1", rank = 1, name = "Sushi Master", category = "Japonesa", rating = 4.9, reviewCount = 189, deliveryTime = "30-45 min", location = "Avenida del Mar 456", priceRange = "$$$", hasPromo = false, description = "El mejor sushi.", imageUrl = ""),
                    Restaurant(id = "2", rank = 2, name = "La Trattoria Italiana", category = "Italiana", rating = 4.8, reviewCount = 245, deliveryTime = "25-35 min", location = "Calle Principal 123", priceRange = "$$", hasPromo = true, promoDescription = "20% descuento", description = "Pasta fresca.", imageUrl = ""),
                    Restaurant(id = "3", rank = 3, name = "El Asador Criollo", category = "Carnes", rating = 4.7, reviewCount = 312, deliveryTime = "40-55 min", location = "Boulevard Gourmet 789", priceRange = "$$$", hasPromo = true, promoDescription = "2x1", description = "Cortes finos.", imageUrl = "")
                )

                _uiState.value = UiState.Success(mockData)

            } catch (e: Exception) {
                _uiState.value = UiState.Error("No se pudieron cargar los restaurantes. Revisa tu conexión.")
            }
        }
    }
}