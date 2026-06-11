package com.example.foodboxd.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodboxd.data.FoodboxdRepository
import com.example.foodboxd.di.ServiceLocator
import com.example.foodboxd.model.Restaurant
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SearchUiState(
    val query: String = "",
    val categories: List<String> = listOf("Todo"),
    val selectedCategory: String = "Todo",
    val results: List<Restaurant> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Búsqueda contra el backend (`/api/restaurants/search` y `/api/restaurants`).
 * Aplica un pequeño debounce para no disparar una petición por cada tecla.
 * Los filtros de precio y calificación mínima se aplican en cliente sobre el
 * resultado, ya que el backend no los expone.
 */
class SearchViewModel(
    private val repository: FoodboxdRepository = ServiceLocator.repository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState

    private var searchJob: Job? = null

    init {
        loadCategories()
        runSearch()
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query)
        runSearch(debounce = true)
    }

    fun clearQuery() {
        _uiState.value = _uiState.value.copy(query = "")
        runSearch()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        runSearch()
    }

    fun retry() = runSearch()

    private fun loadCategories() {
        viewModelScope.launch {
            val cats = runCatching { repository.getCategories() }.getOrDefault(emptyList())
            _uiState.value = _uiState.value.copy(categories = listOf("Todo") + cats)
        }
    }

    private fun runSearch(debounce: Boolean = false) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (debounce) delay(350)
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val state = _uiState.value
                val all = repository.search(state.query)
                val filtered = if (state.selectedCategory == "Todo") {
                    all
                } else {
                    all.filter { it.category.equals(state.selectedCategory, ignoreCase = true) }
                }
                _uiState.value = _uiState.value.copy(results = filtered, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "No se pudo buscar. Revisa tu conexión."
                )
            }
        }
    }
}
