package com.example.foodboxd.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Review
import com.example.foodboxd.model.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class ProfileData(
    val name: String,
    val email: String,
    val initials: String,
    val bio: String,
    val reviewCount: Int,
    val favoriteCount: Int,
    val reviews: List<Review>
)

class ProfileViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<ProfileData>>(UiState.Loading)
    val uiState: StateFlow<UiState<ProfileData>> = _uiState

    init {
        fetchProfile()
    }

    fun fetchProfile(silent: Boolean = false) {
        viewModelScope.launch {
            if (!silent || _uiState.value !is UiState.Success) {
                _uiState.value = UiState.Loading
            }
            try {
                val profile = repository.getProfile()
                val userId = repository.session.userId
                val reviews = if (userId != null) {
                    runCatching { repository.getReviewsByUser(userId) }.getOrDefault(emptyList())
                } else emptyList()

                _uiState.value = UiState.Success(
                    ProfileData(
                        name = profile.name,
                        email = profile.email,
                        initials = initialsOf(profile.name),
                        bio = profile.bio,
                        reviewCount = reviews.size,
                        favoriteCount = profile.favoriteCount,
                        reviews = reviews
                    )
                )
            } catch (e: Exception) {
                if (_uiState.value !is UiState.Success) {
                    _uiState.value = UiState.Error("No se pudo cargar tu perfil. Revisa tu conexión.")
                }
            }
        }
    }

    fun updateProfile(name: String, bio: String, onDone: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = runCatching { repository.updateProfile(name.trim(), bio.trim()) }.isSuccess
            if (ok) fetchProfile(silent = true)
            onDone(ok)
        }
    }

    fun logout() = repository.logout()

    private fun initialsOf(name: String): String {
        val parts = name.trim().split(" ").filter { it.isNotBlank() }
        return when {
            parts.isEmpty() -> "?"
            parts.size == 1 -> parts[0].take(2).uppercase(Locale.getDefault())
            else -> "${parts[0].first()}${parts[1].first()}".uppercase(Locale.getDefault())
        }
    }
}
