package com.example.foodboxd.ui.detail

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodboxd.ui.theme.FoodboxdTheme
import com.example.foodboxd.ui.theme.YellowPrimary

@Composable
fun RestaurantDetailScreen(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(Color.DarkGray)
        ) {
            IconButton(
                onClick = { },
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
                onClick = {  },
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(Color.White)
                    .size(40.dp)
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Favorito", tint = YellowPrimary)
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Nombre del Restaurante",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(text = "$$", color = Color.Gray, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF5F5F5))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "Categoría", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Descripción estandarizada del restaurante. Aquí irá el texto que describe el tipo de comida, el ambiente y las especialidades preparadas con los mejores ingredientes.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "4.8", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = " (XXX reseñas)", color = Color.Gray, fontSize = 14.sp)

                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "XX-XX min", color = Color.Gray, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = YellowPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Dirección completa estandarizada", color = Color.Gray, fontSize = 14.sp)
            }

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
                    Text(text = "Detalle del descuento estandarizado", color = Color(0xFF4D3E00), fontSize = 14.sp)
                }
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun RestaurantDetailScreenPreview() {
    FoodboxdTheme {
        RestaurantDetailScreen()
    }
}