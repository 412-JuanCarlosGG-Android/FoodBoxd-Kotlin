package com.example.foodboxd.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs que reflejan exactamente las respuestas JSON del backend de FoodBoxd
 * (Node + Express + MongoDB). Los nombres con `@SerializedName` mapean los
 * campos de Mongo (por ejemplo `_id`) a propiedades idiomáticas de Kotlin.
 */

data class RestaurantDto(
    @SerializedName("_id") val id: String,
    val name: String,
    val imageUrl: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val category: String = "",
    val description: String = "",
    val menuItems: List<MenuItemDto> = emptyList()
)

data class MenuItemDto(
    val name: String = "",
    val price: Double = 0.0,
    val description: String = "",
    val imageUrl: String = ""
)

/** Respuesta paginada de GET /api/restaurants */
data class RestaurantPageDto(
    val data: List<RestaurantDto> = emptyList(),
    val total: Int = 0,
    val page: Int = 1,
    val totalPages: Int = 1
)

data class ReviewDto(
    @SerializedName("_id") val id: String = "",
    val restaurantId: String = "",
    val userId: String = "",
    val userName: String = "",
    val userAvatarUrl: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val createdAt: String? = null
)

// ----- Autenticación / usuario -----

data class RegisterRequest(val name: String, val email: String, val password: String)

data class LoginRequest(val email: String, val password: String)

data class AuthResponse(
    val user: UserDto,
    val token: String
)

data class UserDto(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val reviewCount: Int = 0,
    val favoriteCount: Int = 0
)

data class CreateReviewRequest(val rating: Int, val comment: String)

data class UpdateProfileRequest(val name: String, val bio: String)

data class ToggleFavoriteRequest(val restaurantId: String, val isFavorite: Boolean)

data class ToggleFavoriteResponse(val success: Boolean = false, val favoriteCount: Int = 0)
