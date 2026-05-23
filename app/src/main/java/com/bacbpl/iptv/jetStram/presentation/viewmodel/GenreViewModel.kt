package com.bacbpl.iptv.jetStram.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bacbpl.iptv.jetStram.data.models.Category
import com.bacbpl.iptv.jetStram.data.models.Genre
import com.bacbpl.iptv.jetStram.data.repositories.GenreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreViewModel @Inject constructor(
    private val genreRepository: GenreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenreUiState>(GenreUiState.Loading)
    private val _navigateToPlayer = MutableStateFlow<Pair<Int, String>?>(null)  // Changed to Pair

    val uiState: StateFlow<GenreUiState> = _uiState.asStateFlow()

    // Navigation state - passing GENRE ID
//    private val _navigateToPlayer = MutableStateFlow<Int?>(null)
//    val navigateToPlayer: StateFlow<Int?> = _navigateToPlayer.asStateFlow()
    val navigateToPlayer: StateFlow<Pair<Int, String>?> = _navigateToPlayer.asStateFlow()

    init {
        fetchGenres()
    }

    fun fetchGenres() {
        viewModelScope.launch {
            _uiState.value = GenreUiState.Loading
            val result = genreRepository.getGenres()
            result.fold(
                onSuccess = { response ->
                    _uiState.value = GenreUiState.Success(response.data.filter { it.status == 1 })
                },
                onFailure = { exception ->
                    _uiState.value = GenreUiState.Error(exception.message ?: "Unknown error occurred")
                }
            )
        }
    }

    // Direct navigation - passing GENRE ID
    fun onGenreClick(genre: Genre) {
        _navigateToPlayer.value = Pair(genre.id, genre.name)  // Pass both ID and name
    }

    fun onNavigationConsumed() {
        _navigateToPlayer.value = null
    }
}

sealed class GenreUiState {
    object Loading : GenreUiState()
    data class Success(val genres: List<Genre>) : GenreUiState()
    data class Error(val message: String) : GenreUiState()
}