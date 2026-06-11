package com.example.foodboxd.data.remote

import com.example.foodboxd.data.remote.dto.AuthResponse
import com.example.foodboxd.data.remote.dto.CreateReviewRequest
import com.example.foodboxd.data.remote.dto.LoginRequest
import com.example.foodboxd.data.remote.dto.RegisterRequest
import com.example.foodboxd.data.remote.dto.RestaurantDto
import com.example.foodboxd.data.remote.dto.RestaurantPageDto
import com.example.foodboxd.data.remote.dto.ReviewDto
import com.example.foodboxd.data.remote.dto.ToggleFavoriteRequest
import com.example.foodboxd.data.remote.dto.ToggleFavoriteResponse
import com.example.foodboxd.data.remote.dto.UpdateProfileRequest
import com.example.foodboxd.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Contrato Retrofit que mapea uno a uno los endpoints del backend de FoodBoxd.
 * La URL base (`http://10.0.2.2:3000/api/`) se configura en [ApiClient].
 */
interface ApiService {

    // ----- Restaurantes (público) -----

    @GET("restaurants")
    suspend fun getRestaurants(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("category") category: String? = null
    ): RestaurantPageDto

    @GET("restaurants/featured")
    suspend fun getFeatured(): List<RestaurantDto>

    @GET("restaurants/recommended")
    suspend fun getRecommended(): List<RestaurantDto>

    @GET("restaurants/promotions")
    suspend fun getPromotions(): List<RestaurantDto>

    @GET("restaurants/ranking")
    suspend fun getRanking(): List<RestaurantDto>

    @GET("restaurants/search")
    suspend fun searchRestaurants(@Query("q") query: String): List<RestaurantDto>

    @GET("restaurants/categories")
    suspend fun getCategories(): List<String>

    @GET("restaurants/{id}")
    suspend fun getRestaurantById(@Path("id") id: String): RestaurantDto

    // ----- Reseñas -----

    @GET("reviews/restaurant/{restaurantId}")
    suspend fun getReviewsByRestaurant(@Path("restaurantId") restaurantId: String): List<ReviewDto>

    @GET("reviews/user/{userId}")
    suspend fun getReviewsByUser(@Path("userId") userId: String): List<ReviewDto>

    @POST("reviews/restaurant/{restaurantId}")
    suspend fun createReview(
        @Path("restaurantId") restaurantId: String,
        @Body body: CreateReviewRequest
    ): ReviewDto

    // ----- Usuarios / autenticación -----

    @POST("users/register")
    suspend fun register(@Body body: RegisterRequest): AuthResponse

    @POST("users/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @GET("users/profile/{id}")
    suspend fun getProfile(@Path("id") id: String): UserDto

    @PUT("users/profile/{id}")
    suspend fun updateProfile(
        @Path("id") id: String,
        @Body body: UpdateProfileRequest
    ): UserDto

    @GET("users/profile/{id}/favorites")
    suspend fun getFavorites(@Path("id") id: String): List<RestaurantDto>

    @PUT("users/profile/{id}/favorite")
    suspend fun toggleFavorite(
        @Path("id") id: String,
        @Body body: ToggleFavoriteRequest
    ): ToggleFavoriteResponse
}
