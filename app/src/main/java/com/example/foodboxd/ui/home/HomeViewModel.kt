package com.example.foodboxd.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.delay
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

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<HomeContent>>(UiState.Loading)
    val uiState: StateFlow<UiState<HomeContent>> = _uiState

    init {
        fetchHome()
    }

    fun fetchHome() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading

            delay(1500)

            try {
                val featured = listOf(
                    Restaurant(id = "2", rank = 2, name = "La Trattoria Italiana", category = "Italiana", rating = 4.8, reviewCount = 245, deliveryTime = "25-35 min", location = "Calle Principal 123", priceRange = "$$", hasPromo = true, promoDescription = "20% descuento", description = "Pasta fresca.", imageUrl = ""),
                    Restaurant(id = "1", rank = 1, name = "Sushi Master", category = "Japonesa", rating = 4.9, reviewCount = 189, deliveryTime = "30-45 min", location = "Avenida del Mar 456", priceRange = "$$$", hasPromo = false, description = "El mejor sushi.", imageUrl = ""),
                    Restaurant(id = "3", rank = 3, name = "El Asador Criollo", category = "Carnes", rating = 4.7, reviewCount = 312, deliveryTime = "40-55 min", location = "Boulevard Gourmet 789", priceRange = "$$$", hasPromo = true, promoDescription = "2x1", description = "Cortes finos.", imageUrl = "")
                )

                val latestReviews = listOf(
                    Review(id = "r1", authorName = "María González", authorInitials = "MG", date = "8 jun", rating = 5, comment = "Excelente comida y servicio. El sushi estaba fresquísimo, sin duda volveré pronto.", restaurantName = "Sushi Master"),
                    Review(id = "r2", authorName = "Carlos Medina", authorInitials = "CM", date = "7 jun", rating = 4, comment = "Muy buena experiencia. Los precios son justos y las porciones generosas.", restaurantName = "La Trattoria Italiana"),
                    Review(id = "r3", authorName = "Lucía Fernández", authorInitials = "LF", date = "6 jun", rating = 5, comment = "Los cortes de carne son espectaculares. El ambiente acogedor y la atención de primera.", restaurantName = "El Asador Criollo")
                )

                _uiState.value = UiState.Success(HomeContent(featured, latestReviews))

            } catch (e: Exception) {
                _uiState.value = UiState.Error("No se pudo cargar el inicio. Revisa tu conexión.")
            }
        }
    }
}
