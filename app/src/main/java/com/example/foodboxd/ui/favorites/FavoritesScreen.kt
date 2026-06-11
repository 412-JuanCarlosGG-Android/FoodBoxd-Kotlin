package com.example.foodboxd.ui.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodboxd.ui.theme.FoodboxdTheme
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

private data class FavoriteRestaurant(
    val name: String,
    val category: String,
    val rating: String,
    val priceRange: String,
    val deliveryTime: String,
    val location: String,
    val hasPromo: Boolean = false
)

private val initialFavorites = listOf(
    FavoriteRestaurant("La Trattoria", "Italiana", "4.8", "$$", "20-30 min", "Centro Histórico"),
    FavoriteRestaurant("Sushi Zen", "Japonesa", "4.7", "$$$", "25-40 min", "Polanco"),
    FavoriteRestaurant("El Asador", "Argentina", "4.9", "$$$", "20-30 min", "Lomas", hasPromo = true),
    FavoriteRestaurant("Green Garden", "Vegana", "4.6", "$$", "20-35 min", "Condesa"),
)

@Composable
fun FavoritesScreen(modifier: Modifier = Modifier) {
    val favorites = remember { mutableStateListOf(*initialFavorites.toTypedArray()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        FavoritesHeader(count = favorites.size)

        if (favorites.isEmpty()) {
            FavoritesEmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                items(favorites, key = { it.name }) { restaurant ->
                    FavoriteRestaurantCard(
                        restaurant = restaurant,
                        onRemove = { favorites.remove(restaurant) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesHeader(count: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral950)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = YellowPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Favoritos",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (count > 0) "$count restaurante${if (count != 1) "s" else ""} guardado${if (count != 1) "s" else ""}"
                   else "Tus restaurantes favoritos",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun FavoritesEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "🤍", fontSize = 56.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Aún no tienes favoritos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Marca restaurantes con ❤ para guardarlos aquí y encontrarlos fácilmente.",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun FavoriteRestaurantCard(
    restaurant: FavoriteRestaurant,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            ) {
                if (restaurant.hasPromo) {
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(YellowPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "Promo", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Neutral950)
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = restaurant.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = restaurant.priceRange, color = Color.Gray, fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Quitar de favoritos",
                            tint = YellowPrimary,
                            modifier = Modifier
                                .size(20.dp)
                                .clickable { onRemove() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = restaurant.category, fontSize = 12.sp, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = YellowPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = restaurant.rating, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "• 🕒 ${restaurant.deliveryTime}", color = Color.Gray, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = restaurant.location, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesScreenPreview() {
    FoodboxdTheme {
        FavoritesScreen()
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesEmptyPreview() {
    FoodboxdTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            FavoritesHeader(count = 0)
            FavoritesEmptyState()
        }
    }
}
