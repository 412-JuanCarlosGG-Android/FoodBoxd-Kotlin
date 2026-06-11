package com.example.foodboxd.ui.search

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodboxd.ui.theme.FoodboxdTheme
import com.example.foodboxd.ui.theme.Neutral150
import com.example.foodboxd.ui.theme.Neutral200
import com.example.foodboxd.ui.theme.Neutral300
import com.example.foodboxd.ui.theme.Neutral600
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

private data class FakeRestaurant(
    val name: String,
    val category: String,
    val rating: String,
    val priceRange: String,
    val deliveryTime: String,
    val location: String
)

private val fakeRestaurants = listOf(
    FakeRestaurant("La Trattoria", "Italiana", "4.8", "$$", "20-30 min", "Centro Histórico"),
    FakeRestaurant("Sushi Zen", "Japonesa", "4.7", "$$$", "25-40 min", "Polanco"),
    FakeRestaurant("Burger Lab", "Americana", "4.5", "$$", "15-25 min", "Roma Norte"),
    FakeRestaurant("Taco Fiesta", "Mexicana", "4.3", "$", "10-20 min", "Coyoacán"),
    FakeRestaurant("Green Garden", "Vegana", "4.6", "$$", "20-35 min", "Condesa"),
    FakeRestaurant("Pasta Mia", "Italiana", "4.4", "$$", "25-35 min", "Napoles"),
    FakeRestaurant("Dragon Palace", "China", "4.2", "$", "30-45 min", "Doctores"),
    FakeRestaurant("El Asador", "Argentina", "4.9", "$$$", "20-30 min", "Lomas"),
)

private val categories = listOf("Todo", "Pizza", "Sushi", "Burgers", "Tacos", "Pasta", "Vegano", "Postres")
private val priceOptions = listOf("$", "$$", "$$$", "$$$$")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(modifier: Modifier = Modifier) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Todo") }
    var selectedPrice by remember { mutableStateOf<String?>(null) }
    var minRating by remember { mutableIntStateOf(0) }
    var showSheet by remember { mutableStateOf(false) }
    var sheetPrice by remember { mutableStateOf<String?>(null) }
    var sheetRating by remember { mutableIntStateOf(0) }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val results = fakeRestaurants.filter { restaurant ->
        (selectedCategory == "Todo" || restaurant.category.equals(selectedCategory, ignoreCase = true)) &&
        (query.isBlank() || restaurant.name.contains(query, ignoreCase = true))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SearchHeader(
            query = query,
            onQueryChange = { query = it },
            onClearQuery = { query = "" },
            onFilterClick = {
                sheetPrice = selectedPrice
                sheetRating = minRating
                showSheet = true
            }
        )

        CategoryChips(
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelect = { selectedCategory = it }
        )

        if (selectedPrice != null || minRating > 0) {
            ActiveFilterRow(
                selectedPrice = selectedPrice,
                minRating = minRating,
                onClearPrice = { selectedPrice = null },
                onClearRating = { minRating = 0 }
            )
        }

        if (results.isEmpty()) {
            SearchNoResults(query = query, onClear = { query = "" })
        } else {
            SearchResultsList(results = results, count = results.size)
        }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            FilterSheet(
                selectedPrice = sheetPrice,
                onPriceSelect = { sheetPrice = if (sheetPrice == it) null else it },
                minRating = sheetRating,
                onRatingSelect = { sheetRating = if (sheetRating == it) 0 else it },
                onClear = { sheetPrice = null; sheetRating = 0 },
                onApply = {
                    selectedPrice = sheetPrice
                    minRating = sheetRating
                    showSheet = false
                }
            )
        }
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onFilterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral950)
            .padding(horizontal = 24.dp, vertical = 32.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = YellowPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Buscar",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Encuentra tu próximo restaurante favorito",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = YellowPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(text = "Restaurantes, cocinas...", color = Color.Gray, fontSize = 14.sp)
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            textStyle = TextStyle(fontSize = 14.sp, color = Neutral950),
                            cursorBrush = SolidColor(YellowPrimary),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (query.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Limpiar",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable { onClearQuery() }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(YellowPrimary)
                    .clickable { onFilterClick() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Filtros",
                    color = Neutral950,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun CategoryChips(
    categories: List<String>,
    selectedCategory: String,
    onCategorySelect: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (isSelected) YellowPrimary else Neutral150)
                    .clickable { onCategorySelect(category) }
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = category,
                    fontSize = 13.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) Neutral950 else Neutral600
                )
            }
        }
    }
}

@Composable
private fun ActiveFilterRow(
    selectedPrice: String?,
    minRating: Int,
    onClearPrice: () -> Unit,
    onClearRating: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (selectedPrice != null) {
            FilterPill(text = "Precio: $selectedPrice", onDismiss = onClearPrice)
        }
        if (minRating > 0) {
            FilterPill(text = "$minRating+ ★", onDismiss = onClearRating)
        }
    }
}

@Composable
private fun FilterPill(text: String, onDismiss: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(YellowPrimary.copy(alpha = 0.15f))
            .border(1.dp, YellowPrimary.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = text, fontSize = 12.sp, color = Color(0xFFB8860B), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Quitar filtro",
                tint = Color(0xFFB8860B),
                modifier = Modifier
                    .size(14.dp)
                    .clickable { onDismiss() }
            )
        }
    }
}

@Composable
private fun SearchResultsList(results: List<FakeRestaurant>, count: Int) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Text(
                text = "$count restaurante${if (count != 1) "s" else ""} encontrado${if (count != 1) "s" else ""}",
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
        items(results) { restaurant ->
            RestaurantSearchCard(restaurant = restaurant)
        }
    }
}

@Composable
private fun RestaurantSearchCard(restaurant: FakeRestaurant) {
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
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = restaurant.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = restaurant.priceRange, color = Color.Gray, fontSize = 14.sp)
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

@Composable
private fun SearchNoResults(query: String, onClear: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(text = "🔍", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Sin resultados para",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\"$query\"",
                color = YellowPrimary,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Intenta con otro nombre o ajusta los filtros",
                color = Color.Gray,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onClear,
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Limpiar búsqueda", color = Neutral950, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun FilterSheet(
    selectedPrice: String?,
    onPriceSelect: (String) -> Unit,
    minRating: Int,
    onRatingSelect: (Int) -> Unit,
    onClear: () -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        Text(
            text = "Filtros",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Rango de precio", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            priceOptions.forEach { price ->
                val isSelected = price == selectedPrice
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) YellowPrimary else Neutral150)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) YellowPrimary else Neutral200,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onPriceSelect(price) }
                        .padding(horizontal = 18.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = price,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Neutral950 else Neutral600,
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Calificación mínima", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            (1..5).forEach { star ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "$star estrellas",
                    tint = if (star <= minRating) YellowPrimary else Neutral200,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onRatingSelect(star) }
                )
            }
        }
        if (minRating > 0) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "$minRating+ estrellas", color = Color.Gray, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onClear,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Neutral300)
            ) {
                Text("Limpiar", color = Neutral600, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onApply,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Aplicar", color = Neutral950, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SearchScreenPreview() {
    FoodboxdTheme {
        SearchScreen()
    }
}
