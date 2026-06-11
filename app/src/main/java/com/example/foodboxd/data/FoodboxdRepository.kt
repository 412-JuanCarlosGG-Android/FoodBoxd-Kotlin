package com.example.foodboxd.data

import com.example.foodboxd.data.remote.ApiService
import com.example.foodboxd.data.remote.dto.CreateReviewRequest
import com.example.foodboxd.data.remote.dto.LoginRequest
import com.example.foodboxd.data.remote.dto.RegisterRequest
import com.example.foodboxd.data.remote.dto.ToggleFavoriteRequest
import com.example.foodboxd.data.remote.dto.UpdateProfileRequest
import com.example.foodboxd.data.remote.dto.UserDto
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review

/**
 * Única fuente de verdad para la UI. Traduce los DTO de [ApiService] al modelo
 * de dominio y centraliza la lógica de sesión (login/registro/logout).
 *
 * Las funciones `suspend` lanzan excepción ante fallos de red/HTTP; los
 * ViewModels las envuelven en `try/catch` para producir su `UiState.Error`.
 */
class FoodboxdRepository(
    private val api: ApiService,
    val session: SessionManager
) {

    // ----- Restaurantes -----

    suspend fun getFeatured(): List<Restaurant> {
        val promoIds = promoIdsOrEmpty()
        return api.getFeatured().map { it.toDomain(isPromo = it.id in promoIds) }
    }

    suspend fun getRanking(): List<Restaurant> {
        val promoIds = promoIdsOrEmpty()
        return api.getRanking().mapIndexed { index, dto ->
            dto.toDomain(rank = index + 1, isPromo = dto.id in promoIds)
        }
    }

    suspend fun search(query: String): List<Restaurant> {
        val promoIds = promoIdsOrEmpty()
        val dtos = if (query.isBlank()) {
            api.getRestaurants().data
        } else {
            api.searchRestaurants(query)
        }
        return dtos.map { it.toDomain(isPromo = it.id in promoIds) }
    }

    suspend fun getCategories(): List<String> = api.getCategories()

    suspend fun getRestaurant(id: String): Restaurant {
        val promoIds = promoIdsOrEmpty()
        return api.getRestaurantById(id).toDomain(isPromo = id in promoIds)
    }

    /** Devuelve el contenido de la pantalla de inicio en una sola operación. */
    suspend fun getHomeContent(): HomeData {
        val promoIds = promoIdsOrEmpty()
        val featured = api.getFeatured().map { it.toDomain(isPromo = it.id in promoIds) }

        // "Últimas reseñas" = reseñas más recientes de los restaurantes destacados.
        val latest = featured.flatMap { restaurant ->
            runCatching {
                api.getReviewsByRestaurant(restaurant.id)
                    .map { it.toDomain(restaurantName = restaurant.name) }
            }.getOrDefault(emptyList())
        }.take(10)

        return HomeData(featured = featured, latestReviews = latest)
    }

    // ----- Reseñas -----

    suspend fun getReviews(restaurantId: String): List<Review> =
        api.getReviewsByRestaurant(restaurantId).map { it.toDomain() }

    suspend fun getReviewsByUser(userId: String): List<Review> =
        api.getReviewsByUser(userId).map { dto ->
            // Enriquecemos cada reseña con el nombre del restaurante para mostrarlo en el perfil.
            val restaurantName = runCatching { api.getRestaurantById(dto.restaurantId).name }.getOrNull()
            dto.toDomain(restaurantName = restaurantName)
        }

    suspend fun createReview(restaurantId: String, rating: Int, comment: String): Review =
        api.createReview(restaurantId, CreateReviewRequest(rating, comment)).toDomain()

    // ----- Autenticación -----

    suspend fun login(email: String, password: String): UserDto {
        val response = api.login(LoginRequest(email, password))
        session.saveSession(response.token, response.user)
        return response.user
    }

    suspend fun register(name: String, email: String, password: String): UserDto {
        val response = api.register(RegisterRequest(name, email, password))
        session.saveSession(response.token, response.user)
        return response.user
    }

    fun logout() = session.clear()

    // ----- Perfil / favoritos (requieren sesión) -----

    suspend fun getProfile(): UserDto =
        api.getProfile(requireUserId())

    suspend fun updateProfile(name: String, bio: String): UserDto =
        api.updateProfile(requireUserId(), UpdateProfileRequest(name, bio))

    suspend fun getFavorites(): List<Restaurant> =
        api.getFavorites(requireUserId()).map { it.toDomain() }

    suspend fun setFavorite(restaurantId: String, isFavorite: Boolean): Int =
        api.toggleFavorite(requireUserId(), ToggleFavoriteRequest(restaurantId, isFavorite)).favoriteCount

    // ----- Helpers internos -----

    private fun requireUserId(): String =
        session.userId ?: throw IllegalStateException("No hay una sesión activa")

    private suspend fun promoIdsOrEmpty(): Set<String> =
        runCatching { api.getPromotions().map { it.id }.toSet() }.getOrDefault(emptySet())
}

data class HomeData(
    val featured: List<Restaurant>,
    val latestReviews: List<Review>
)
