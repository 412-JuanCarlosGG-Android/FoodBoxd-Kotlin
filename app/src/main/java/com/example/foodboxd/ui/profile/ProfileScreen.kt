package com.example.foodboxd.ui.profile

import android.widget.Toast
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodboxd.model.Review
import com.example.foodboxd.model.UiState
import com.example.foodboxd.ui.components.OnResume
import com.example.foodboxd.ui.theme.Neutral950
import com.example.foodboxd.ui.theme.YellowPrimary

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showEditDialog by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    // Refresca contadores y "Mis reseñas" al volver al perfil.
    OnResume { viewModel.fetchProfile(silent = true) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        when (val state = uiState) {
            is UiState.Loading -> {
                ProfileHeader(name = "…", email = "", initials = "·", reviewCount = 0, favoriteCount = 0)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = YellowPrimary)
                }
            }
            is UiState.Error -> {
                ProfileHeader(name = "Perfil", email = "", initials = "?", reviewCount = 0, favoriteCount = 0)
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = state.message, color = Color.Red, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { viewModel.fetchProfile() },
                            colors = ButtonDefaults.buttonColors(containerColor = YellowPrimary)
                        ) {
                            Text("Reintentar", color = Neutral950, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            is UiState.Success -> {
                val data = state.data
                ProfileHeader(
                    name = data.name,
                    email = data.email,
                    initials = data.initials,
                    reviewCount = data.reviewCount,
                    favoriteCount = data.favoriteCount
                )

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

                    if (data.reviews.isEmpty()) {
                        item {
                            Text(
                                text = "Todavía no has escrito reseñas. ¡Visita un restaurante y comparte tu opinión!",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    } else {
                        items(data.reviews.size) { index ->
                            MyReviewItem(review = data.reviews[index])
                        }
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
                ) {
                    ProfileMenuItem(
                        icon = Icons.Default.Settings,
                        title = "Configuración de cuenta",
                        onClick = { showEditDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    ProfileMenuItem(
                        icon = Icons.Default.Info,
                        title = "Ayuda",
                        onClick = { showHelpDialog = true }
                    )
                    HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)

                    ProfileMenuItem(
                        icon = Icons.AutoMirrored.Filled.ExitToApp,
                        title = "Cerrar sesión",
                        textColor = Color.Red,
                        iconColor = Color.Red,
                        onClick = {
                            viewModel.logout()
                            onLogout()
                        }
                    )
                }

                if (showEditDialog) {
                    EditProfileDialog(
                        currentName = data.name,
                        currentBio = data.bio,
                        onDismiss = { showEditDialog = false },
                        onSave = { name, bio ->
                            viewModel.updateProfile(name, bio) { ok ->
                                Toast.makeText(
                                    context,
                                    if (ok) "Perfil actualizado" else "No se pudo actualizar el perfil",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            showEditDialog = false
                        }
                    )
                }

                if (showHelpDialog) {
                    HelpDialog(onDismiss = { showHelpDialog = false })
                }
            }
        }
    }
}

@Composable
private fun EditProfileDialog(
    currentName: String,
    currentBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var bio by remember { mutableStateOf(currentBio) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = "Editar perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Biografía") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (name.isNotBlank()) onSave(name, bio) },
                enabled = name.isNotBlank()
            ) {
                Text("Guardar", color = Neutral950, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar", color = Color.Gray) }
        }
    )
}

@Composable
private fun HelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text(text = "Ayuda", fontWeight = FontWeight.Bold) },
        text = {
            Text(
                "FoodBoxd es una app de reseñas de restaurantes.\n\n" +
                    "• Explora restaurantes en Inicio y Top.\n" +
                    "• Busca por nombre o categoría en Buscar.\n" +
                    "• Abre un restaurante para ver su menú, dejar una reseña " +
                    "con estrellas y marcarlo como favorito ❤.\n" +
                    "• Tus favoritos y reseñas aparecen en sus pestañas.\n\n" +
                    "¿Dudas? Escríbenos a soporte@foodboxd.com"
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Entendido", color = Neutral950, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun ProfileHeader(
    name: String,
    email: String,
    initials: String,
    reviewCount: Int,
    favoriteCount: Int
) {
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
                text = initials,
                color = Neutral950,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            color = Color.White,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = email,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            ProfileStat(value = reviewCount.toString(), label = "Reseñas")
            ProfileStat(value = favoriteCount.toString(), label = "Favoritos")
        }
    }
}

@Composable
private fun ProfileStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = YellowPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(text = label, color = Color.LightGray, fontSize = 12.sp)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    textColor: Color = Neutral950,
    iconColor: Color = Neutral950,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = title, tint = iconColor)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = title, color = textColor, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MyReviewItem(review: Review) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = review.restaurantName ?: "Restaurante", fontWeight = FontWeight.Bold)
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Star",
                        tint = if (index < review.rating) YellowPrimary else Color.LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = review.comment, color = Color.DarkGray, style = MaterialTheme.typography.bodyMedium)
        if (review.date.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = review.date, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFEEEEEE), thickness = 1.dp)
    }
}
