package com.bacbpl.iptv.jetStram.presentation.viewmodel

// presentation/viewmodels/GenreChannelViewModel.kt

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bacbpl.iptv.jetStram.data.models.GenreChannel
import com.bacbpl.iptv.jetStram.data.repositories.GenreChannelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GenreChannelViewModel @Inject constructor(
    private val repository: GenreChannelRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<GenreChannelUiState>(GenreChannelUiState.Loading)
    val uiState: StateFlow<GenreChannelUiState> = _uiState.asStateFlow()

    private val _genreName = MutableStateFlow("")
    val genreName: StateFlow<String> = _genreName.asStateFlow()

    fun fetchChannels(genreId: Int) {
        viewModelScope.launch {
            _uiState.value = GenreChannelUiState.Loading
            val result = repository.getChannelsByGenre(genreId)
            result.fold(
                onSuccess = { response ->
                    _genreName.value = response.genre
                    _uiState.value = GenreChannelUiState.Success(
                        channels = response.data.filter { it.status == "running" }
                    )
                },
                onFailure = { exception ->
                    _uiState.value = GenreChannelUiState.Error(
                        message = exception.message ?: "Unknown error occurred"
                    )
                }
            )
        }
    }
}

sealed class GenreChannelUiState {
    object Loading : GenreChannelUiState()
    data class Success(val channels: List<GenreChannel>) : GenreChannelUiState()
    data class Error(val message: String) : GenreChannelUiState()
}