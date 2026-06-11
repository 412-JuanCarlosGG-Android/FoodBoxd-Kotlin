package com.example.foodboxd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.example.foodboxd.ui.theme.YellowPrimary

/**
 * Carga la foto remota de un restaurante con Coil. Mientras carga (o si la
 * URL está vacía / falla) muestra un marcador de posición gris con un ícono,
 * conservando el aspecto visual de las tarjetas originales del frontend.
 */
@Composable
fun RestaurantImage(
    url: String?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
) {
    if (url.isNullOrBlank()) {
        Placeholder(modifier)
        return
    }
    SubcomposeAsyncImage(
        model = url,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier,
        loading = { Placeholder(Modifier.fillMaxSize()) },
        error = { Placeholder(Modifier.fillMaxSize()) },
    )
}

@Composable
private fun Placeholder(modifier: Modifier) {
    Box(
        modifier = modifier.background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = YellowPrimary.copy(alpha = 0.6f),
        )
    }
}
