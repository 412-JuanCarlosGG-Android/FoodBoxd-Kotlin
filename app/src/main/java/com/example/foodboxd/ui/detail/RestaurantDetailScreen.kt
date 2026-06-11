package com.example.foodboxd.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodboxd.model.MenuItem
import com.example.foodboxd.model.Restaurant
import com.example.foodboxd.model.Review
import com.example.foodboxd.ui.components.RestaurantImage
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary
import java.util.Locale

@Composable
fun RestaurantDetailScreen(
    restaurantId: String,
    modifier: Modifier = Modifier,
    onBack: () -> Unit = {},
    viewModel: DetailViewModel = viewModel(factory = DetailViewModel.provideFactory(restaurantId))
) {
    val state by viewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().background(Color.White)) {
        when {
            state.isLoading -> {
                CircularProgressIndicator(
                    color = YellowPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            state.error != null || state.restaurant == null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.error ?: "Restaurante no encontrado", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { viewModel.load() },
                        colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                    ) {
                        Text("Reintentar", color = Neutral950, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = onBack) { Text("Volver") }
                }
            }
            else -> {
                DetailContent(
                    restaurant = state.restaurant!!,
                    reviews = state.reviews,
                    isFavorite = state.isFavorite,
                    isSubmitting = state.isSubmitting,
                    submitMessage = state.submitMessage,
                    onBack = onBack,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onSubmitReview = viewModel::submitReview,
                    onConsumeMessage = viewModel::consumeSubmitMessage
                )
            }
        }
    }
}

@Composable
private fun DetailContent(
    restaurant: Restaurant,
    reviews: List<Review>,
    isFavorite: Boolean,
    isSubmitting: Boolean,
    submitMessage: String?,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSubmitReview: (Int, String) -> Unit,
    onConsumeMessage: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
        ) {
            RestaurantImage(
                url = restaurant.imageUrl,
                contentDescription = restaurant.name,
                modifier = Modifier.fillMaxSize()
            )

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(Color.White)
                    .size(40.dp)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
            }

            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = YellowPrimary
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = restaurant.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = restaurant.priceRange, color = Color.Gray, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = restaurant.category, fontSize = 14.sp, color = Color.DarkGray)
            }

            if (restaurant.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = restaurant.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = restaurant.rating.toString(), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = " (${restaurant.reviewCount} reseñas)", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "🕒 ${restaurant.deliveryTime}", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = restaurant.location, color = Color.Gray, fontSize = 14.sp)
            }

            if (restaurant.hasPromo) {
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(YellowPrimary)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎉", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Promoción activa", fontWeight = FontWeight.Bold, color = Color(0xFF4D3E00))
                        Text(
                            text = restaurant.promoDescription ?: "Aprovecha esta promoción",
                            color = Color(0xFF4D3E00),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (restaurant.menuItems.isNotEmpty()) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Menú",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                RestaurantMenuGallery(menu = restaurant.menuItems)
            }

            Spacer(modifier = Modifier.height(32.dp))

            ReviewInputArea(
                isSubmitting = isSubmitting,
                submitMessage = submitMessage,
                onSubmit = onSubmitReview,
                onConsumeMessage = onConsumeMessage
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Reseñas (${reviews.size})",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (reviews.isEmpty()) {
                Text(
                    text = "Aún no hay reseñas. ¡Sé el primero en opinar!",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            } else {
                reviews.forEach { review ->
                    ReviewListItem(
                        initials = review.authorInitials,
                        name = review.authorName,
                        date = review.date,
                        rating = review.rating,
                        comment = review.comment
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun RestaurantMenuGallery(menu: List<MenuItem>) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(menu) { item ->
            Column(modifier = Modifier.width(140.dp)) {
                RestaurantImage(
                    url = item.imageUrl,
                    contentDescription = item.name,
                    modifier = Modifier
                        .size(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = item.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 2)
                Text(
                    text = String.format(Locale.US, "$%.2f", item.price),
                    color = Color(0xFFB8860B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun ReviewInputArea(
    isSubmitting: Boolean,
    submitMessage: String?,
    onSubmit: (Int, String) -> Unit,
    onConsumeMessage: () -> Unit
) {
    var rating by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }

    if (submitMessage != null) {
        AlertDialog(
            onDismissRequest = onConsumeMessage,
            title = { Text(text = submitMessage) },
            confirmButton = {
                TextButton(
                    onClick = {
                        // Si fue exitoso (mensaje de gracias), limpiamos el formulario.
                        if (submitMessage.startsWith("¡Gracias")) {
                            rating = 0
                            comment = ""
                        }
                        onConsumeMessage()
                    }
                ) {
                    Text("Aceptar", color = Neutral950, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFFF9D6))
            .border(1.dp, YellowPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(text = "Deja tu reseña", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            Row {
                for (i in 1..5) {
                    Icon(
                        imageVector = if (i <= rating) Icons.Default.Star else Icons.Outlined.Star,
                        contentDescription = "Star $i",
                        tint = if (i <= rating) YellowPrimary else Color.Gray,
                        modifier = Modifier
                            .size(32.dp)
                            .clickable { rating = i }
                            .padding(end = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                placeholder = { Text("Escribe tu comentario sobre este restaurante...", color = Color.Gray) },
                enabled = !isSubmitting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color.White, RoundedCornerShape(8.dp)),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = YellowPrimary
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { onSubmit(rating, comment) },
                enabled = !isSubmitting && rating > 0 && comment.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                shape = RoundedCornerShape(8.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Neutral950, strokeWidth = 2.dp, modifier = Modifier.size(18.dp))
                } else {
                    Icon(imageVector = Icons.AutoMirrored.Filled.Send, contentDescription = "Enviar", tint = Neutral950, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Publicar reseña", color = Neutral950, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ReviewListItem(initials: String, name: String, date: String, rating: Int, comment: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(YellowPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = initials, fontWeight = FontWeight.Bold, color = Neutral950)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = name, fontWeight = FontWeight.Bold)
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < rating) YellowPrimary else Color.LightGray,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }
            Text(text = date, color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(text = comment, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}
