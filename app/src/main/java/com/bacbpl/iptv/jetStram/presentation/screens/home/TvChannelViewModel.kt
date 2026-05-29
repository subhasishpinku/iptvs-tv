package com.bacbpl.iptv.jetStram.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bacbpl.iptv.jetStram.data.entities.TvChannel
import com.bacbpl.iptv.jetStram.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TvChannelViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    private val _channels = MutableStateFlow<List<TvChannel>>(emptyList())
    val channels: StateFlow<List<TvChannel>> = _channels.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadChannels()
    }

    fun loadChannels() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val apiChannels = apiService.getTvChannels()
                // SAFE CONVERSION with null handling
                val safeChannels = apiChannels.map { channel ->
                    TvChannel(
                        id = channel.id,
                        name = channel.name ?: "",
                        logoUrl = channel.logoUrl ?: "",
                        streamUrl = channel.streamUrl ?: "",
                        category = channel.category ?: "Live TV",
                        localNumber = channel.localNumber ?: "",  // Handle null
                        language = channel.language ?: ""         // Handle null
                    )
                }
                _channels.value = safeChannels

                // Debug log
                println("=== Channels Loaded: ${safeChannels.size} ===")
                safeChannels.forEach {
                    println("Channel: ${it.name}, LocalNumber: ${it.localNumber}, Language: ${it.language}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _channels.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getChannelsByCategory(category: String): List<TvChannel> {
        return _channels.value.filter { it.category == category }
    }

    fun getCategories(): List<String> {
        return _channels.value.map { it.category }.distinct()
    }

    fun getLanguages(): List<String> {
        return _channels.value.map { it.language }.filter { it.isNotEmpty() }.distinct().sorted()
    }
}