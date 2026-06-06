package com.example.foodboxd.ui.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ProfileHeader()

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Mis Reseñas",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            item {
                MyReviewItem(
                    restaurantName = "Nombre de Restaurante",
                    reviewText = "Texto de reseña de prueba estandarizado para ver el diseño.",
                    date = "DD de Mes de YYYY"
                )
            }
            item {
                MyReviewItem(
                    restaurantName = "Otro Restaurante",
                    reviewText = "Otra reseña de prueba para verificar el espaciado.",
                    date = "DD de Mes de YYYY"
                )
            }
        }

        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            ProfileMenuItem(icon = Icons.Default.Settings, title = "Configuración de cuenta")
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            ProfileMenuItem(icon = Icons.Default.Info, title = "Ayuda")
            HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

            ProfileMenuItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Cerrar sesión",
                textColor = Color.Red,
                iconColor = Color.Red
            )
        }
    }
}

@Composable
fun ProfileHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Neutral950)
            .padding(top = 48.dp, bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(YellowPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NA",
                color = Neutral950,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "nombre y apellido",
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "telefono",
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color = Neutral950,
    iconColor: Color = Neutral950
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = textColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun MyReviewItem(restaurantName: String, reviewText: String, date: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = restaurantName, fontWeight = FontWeight.Bold)
            Row {
                repeat(5) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = YellowPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = reviewText, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = date, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}

@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    FoodboxdTheme {
        ProfileScreen()
    }
}