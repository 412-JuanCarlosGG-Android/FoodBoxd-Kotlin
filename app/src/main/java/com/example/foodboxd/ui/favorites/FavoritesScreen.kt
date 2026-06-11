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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.UiState
import com.example.foodboxd.ui.components.OnResume
import com.example.foodboxd.ui.components.RestaurantImage
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

@Composable
fun FavoritesScreen(
    modifier: Modifier = Modifier,
    viewModel: FavoritesViewModel = viewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresca al volver a la pestaña (p. ej. tras marcar un favorito en el detalle).
    OnResume { viewModel.fetchFavorites(silent = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        val count = (uiState as? UiState.Success)?.data?.size ?: 0
        FavoritesHeader(count = count)

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YellowPrimary)
                }
            }
            is UiState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchFavorites() },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Reintentar", color = Neutral950, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is UiState.Success -> {
                if (state.data.isEmpty()) {
                    FavoritesEmptyState()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
                    ) {
                        items(state.data, key = { it.id }) { restaurant ->
                            FavoriteRestaurantCard(
                                restaurant = restaurant,
                                onClick = { onRestaurantClick(restaurant.id) },
                                onRemove = { viewModel.removeFavorite(restaurant.id) }
                            )
                        }
                    }
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
    restaurant: Restaurant,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
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
            ) {
                RestaurantImage(
                    url = restaurant.imageUrl,
                    contentDescription = restaurant.name,
                    modifier = Modifier.fillMaxSize()
                )
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
                    Text(text = restaurant.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 12.sp)
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
