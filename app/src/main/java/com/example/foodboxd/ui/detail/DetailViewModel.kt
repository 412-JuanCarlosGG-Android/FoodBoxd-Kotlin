package com.example.foodboxd.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val restaurant: Restaurant? = null,
    val reviews: List<Review> = emptyList(),
    val isFavorite: Boolean = false,
    val isSubmitting: Boolean = false,
    val submitMessage: String? = null
)

/**
 * Detalle de un restaurante: carga sus datos + reseñas, permite marcar/desmarcar
 * favorito y publicar una nueva reseña (todo contra el backend).
 *
 * Recibe el `restaurantId` por constructor, por lo que se instancia con la
 * factory [provideFactory] desde la pantalla.
 */
class DetailViewModel(
    private val restaurantId: String,
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val restaurant = repository.getRestaurant(restaurantId)
                val reviews = repository.getReviews(restaurantId)
                val isFavorite = runCatching {
                    repository.getFavorites().any { it.id == restaurantId }
                }.getOrDefault(false)

                _uiState.value = DetailUiState(
                    isLoading = false,
                    restaurant = restaurant,
                    reviews = reviews,
                    isFavorite = isFavorite
                )
            } catch (e: Exception) {
                _uiState.value = DetailUiState(
                    isLoading = false,
                    error = "No se pudo cargar el restaurante. Revisa tu conexión."
                )
            }
        }
    }

    fun toggleFavorite() {
        val newValue = !_uiState.value.isFavorite
        _uiState.value = _uiState.value.copy(isFavorite = newValue) // optimista
        viewModelScope.launch {
            runCatching { repository.setFavorite(restaurantId, newValue) }
                .onFailure {
                    // Revertimos si el backend rechaza el cambio.
                    _uiState.value = _uiState.value.copy(isFavorite = !newValue)
                }
        }
    }

    fun submitReview(rating: Int, comment: String) {
        if (rating == 0 || comment.isBlank()) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSubmitting = true, submitMessage = null)
            try {
                repository.createReview(restaurantId, rating, comment)
                // Recargamos reseñas y el restaurante (su rating/contador se recalcula en el backend).
                val reviews = repository.getReviews(restaurantId)
                val restaurant = runCatching { repository.getRestaurant(restaurantId) }
                    .getOrDefault(_uiState.value.restaurant)
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    reviews = reviews,
                    restaurant = restaurant,
                    submitMessage = "¡Gracias por tu reseña!"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSubmitting = false,
                    submitMessage = "No se pudo publicar tu reseña. Inténtalo de nuevo."
                )
            }
        }
    }

    fun consumeSubmitMessage() {
        _uiState.value = _uiState.value.copy(submitMessage = null)
    }

    companion object {
        fun provideFactory(restaurantId: String): ViewModelProvider.Factory = viewModelFactory {
            initializer { DetailViewModel(restaurantId) }
        }
    }
}
