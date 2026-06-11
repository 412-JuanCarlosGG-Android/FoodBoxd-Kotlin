package com.example.foodboxd.data

import com.example.foodboxd.data.remote.dto.MenuItemDto
import com.example.foodboxd.data.remote.dto.RestaurantDto
import com.example.foodboxd.data.remote.dto.ReviewDto
import com.example.foodboxd.model.MenuItem
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Conversión de los DTO de la API al modelo de dominio que consume la UI.
 *
 * El backend de FoodBoxd modela una app de RESEÑAS (no de delivery), por lo que
 * no envía campos como `priceRange`, `location` o `deliveryTime` que el diseño
 * original del frontend sí muestra. Para no perder ese diseño, esos campos
 * puramente visuales se derivan de forma DETERMINISTA a partir de los datos
 * reales (precio del menú, id, etc.), de modo que cada restaurante se vea
 * siempre igual entre pantallas.
 */

private val neighborhoods = listOf(
    "Centro Histórico", "Polanco", "Roma Norte", "Condesa",
    "Coyoacán", "Del Valle", "Nápoles", "Santa Fe", "Narvarte", "Juárez"
)

fun RestaurantDto.toDomain(rank: Int = 0, isPromo: Boolean = false): Restaurant {
    val hash = id.hashCode() and 0x7fffffff
    return Restaurant(
        id = id,
        rank = rank,
        name = name,
        category = category,
        rating = rating,
        reviewCount = reviewCount,
        deliveryTime = derivedDeliveryTime(hash),
        location = neighborhoods[hash % neighborhoods.size],
        priceRange = derivedPriceRange(menuItems),
        hasPromo = isPromo,
        promoDescription = if (isPromo) "Promoción activa" else null,
        description = description,
        imageUrl = imageUrl,
        menuItems = menuItems.map { it.toDomain() }
    )
}

fun MenuItemDto.toDomain() = MenuItem(
    name = name,
    price = price,
    description = description,
    imageUrl = imageUrl
)

fun ReviewDto.toDomain(restaurantName: String? = null) = Review(
    id = id,
    authorName = userName,
    authorInitials = initialsOf(userName),
    date = formatReviewDate(createdAt),
    rating = rating,
    comment = comment,
    restaurantName = restaurantName
)

// ----- Helpers de derivación / formato -----

private fun derivedPriceRange(menu: List<MenuItemDto>): String {
    val avg = menu.map { it.price }.filter { it > 0 }.average()
    return when {
        avg.isNaN() -> "$$"
        avg < 8.0 -> "$"
        avg < 16.0 -> "$$"
        else -> "$$$"
    }
}

private fun derivedDeliveryTime(hash: Int): String {
    val start = 15 + (hash % 20)
    return "$start-${start + 15} min"
}

private fun initialsOf(name: String): String {
    val parts = name.trim().split(" ").filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(2).uppercase(Locale.getDefault())
        else -> "${parts[0].first()}${parts[1].first()}".uppercase(Locale.getDefault())
    }
}

private val isoFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("es", "ES"))

private fun formatReviewDate(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return try {
        OffsetDateTime.parse(iso).format(isoFormatter)
    } catch (e: Exception) {
        ""
    }
}
