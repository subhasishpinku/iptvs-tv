package com.bacbpl.iptv.jetStram.presentation.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bacbpl.iptv.jetStram.data.entities.OttWidget
import com.bacbpl.iptv.jetStram.data.repositories.WidgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class OttUiState {
    object Loading : OttUiState()
    data class Success(val widgets: List<OttWidget>) : OttUiState()
    data class Error(val message: String) : OttUiState()
}

@HiltViewModel
class OttViewModel @Inject constructor(
    private val widgetRepository: WidgetRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<OttUiState>(OttUiState.Loading)
    val uiState: StateFlow<OttUiState> = _uiState.asStateFlow()

    init {
        fetchOttWidgets()
    }

    fun fetchOttWidgets() {
        viewModelScope.launch {
            _uiState.value = OttUiState.Loading
            try {
                val widgets = widgetRepository.getWidgets()
                if (widgets.isNotEmpty()) {
                    _uiState.value = OttUiState.Success(widgets)
                } else {
                    _uiState.value = OttUiState.Success(emptyList())
                }
            } catch (e: Exception) {
                _uiState.value = OttUiState.Error(e.message ?: "Failed to load OTT content")
            }
        }
    }
}