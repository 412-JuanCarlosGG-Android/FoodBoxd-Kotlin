package com.example.foodboxd.ui.top

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodboxd.model.UiState
import com.example.foodboxd.ui.components.OnResume
import com.example.foodboxd.ui.components.RestaurantImage
import com.example.foodboxd.ui.theme.FoodboxdTheme
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

@Composable
fun TopRestaurantsScreen(
    modifier: Modifier = Modifier,
    viewModel: TopRestaurantsViewModel = viewModel(),
    onRestaurantClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    // Refresca el ranking al volver (refleja nuevas reseñas/calificaciones).
    OnResume { viewModel.fetchTopRestaurants(silent = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopRestaurantsHeader()

        when (val state = uiState) {
            is UiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = YellowPrimary)
                }
            }
            is UiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchTopRestaurants() },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Reintentar", color = Neutral950, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is UiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
                ) {
                    items(state.data) { restaurant ->
                        TopRestaurantCard(
                            rank = restaurant.rank,
                            name = restaurant.name,
                            category = restaurant.category,
                            rating = restaurant.rating.toString(),
                            reviews = "(${restaurant.reviewCount})",
                            time = restaurant.deliveryTime,
                            location = restaurant.location,
                            price = restaurant.priceRange,
                            hasPromo = restaurant.hasPromo,
                            imageUrl = restaurant.imageUrl,
                            onClick = { onRestaurantClick(restaurant.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopRestaurantsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral950)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Trofeo",
                tint = YellowPrimary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Top Restaurantes",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Los mejor calificados por nuestra comunidad",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun TopRestaurantCard(
    rank: Int,
    name: String,
    category: String,
    rating: String,
    reviews: String,
    time: String,
    location: String,
    price: String,
    hasPromo: Boolean,
    imageUrl: String = "",
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box {
                RestaurantImage(
                    url = imageUrl,
                    contentDescription = name,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(12.dp))
                )

                val badgeColor = when (rank) {
                    1 -> Color(0xFFFFD700)
                    2 -> Color(0xFFC0C0C0)
                    3 -> Color(0xFFCD7F32)
                    else -> Neutral950
                }

                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(24.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = rank.toString(), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = price, color = Color.Gray, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF5F5F5))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(text = category, fontSize = 12.sp, color = Color.DarkGray)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = rating, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text(text = " $reviews", color = Color.Gray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "•  🕒 $time", color = Color.Gray, fontSize = 12.sp)

                    Spacer(modifier = Modifier.weight(1f))

                    if (hasPromo) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(YellowPrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = "Promo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB8860B))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = location, color = Color.Gray, fontSize = 12.sp, maxLines = 1)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TopRestaurantsScreenPreview() {
    FoodboxdTheme {
        TopRestaurantsScreen()
    }
}