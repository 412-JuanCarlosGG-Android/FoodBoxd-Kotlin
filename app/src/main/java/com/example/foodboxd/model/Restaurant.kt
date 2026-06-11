package com.example.foodboxd.model

data class Restaurant(
    val id: String,
    val rank: Int,
    val name: String,
    val category: String,
    val rating: Double,
    val reviewCount: Int,
    val deliveryTime: String,
    val location: String,
    val priceRange: String,
    val hasPromo: Boolean,
    val promoDescription: String? = null,
    val description: String,
    val imageUrl: String,
    val menuItems: List<MenuItem> = emptyList()
)

data class MenuItem(
    val name: String,
    val price: Double,
    val description: String,
    val imageUrl: String
)

data class Review(
    val id: String,
    val authorName: String,
    val authorInitials: String,
    val date: String,
    val rating: Int,
    val comment: String,
    val restaurantName: String? = null
)