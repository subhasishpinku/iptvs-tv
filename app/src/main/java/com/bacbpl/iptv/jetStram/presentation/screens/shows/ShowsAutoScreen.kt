package com.bacbpl.iptv.jetStram.presentation.screens.shows

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bacbpl.iptv.jetStram.data.entities.Movie
import com.bacbpl.iptv.jetStram.data.entities.TvChannel
import com.bacbpl.iptv.jetStram.presentation.screens.home.TvChannelViewModel
import com.bacbpl.iptv.jetStram.presentation.screens.player.TvAutoPlayer
import com.bacbpl.iptv.jetStram.presentation.screens.player.TvPlayer

@Composable
fun ShowsAutoScreen(
    onTVShowClick: (movie: Movie) -> Unit,
    onScroll: (isTopBarVisible: Boolean) -> Unit,
    isTopBarVisible: Boolean,
    tvChannelViewModel: TvChannelViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val channels by tvChannelViewModel.channels.collectAsStateWithLifecycle()
    val isLoading by tvChannelViewModel.isLoading.collectAsStateWithLifecycle()

    var hasNavigated by remember { mutableStateOf(false) }

    // Auto-navigate to TV Player when channels are available
    LaunchedEffect(channels, isLoading, hasNavigated) {
        if (channels.isNotEmpty() && !isLoading && !hasNavigated) {
            hasNavigated = true

            val intent = Intent(context, TvAutoPlayer::class.java).apply {
                // Get first channel as initial
                val firstChannel = channels[0]
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_ID, firstChannel.id ?: 0)
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_NAME, firstChannel.name ?: "")
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_LOGO_URL, firstChannel.logoUrl ?: "")
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_STREAM_URL, firstChannel.streamUrl ?: "")
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_CATEGORY, firstChannel.category ?: "Live TV")
                putExtra(TvAutoPlayer.EXTRA_CHANNEL_LOCAL_NUMBER, firstChannel.localNumber ?: "")  // Added
                putExtra(TvAutoPlayer.EXTRA_GENRE_NAME, "")  // Send genreName

                // Convert all channels
                val channelsList = ArrayList(channels.map { movie ->
                    TvChannel(
                        id = movie.id ?: 0,
                        name = movie.name ?: "",
                        logoUrl = movie.logoUrl ?: "",
                        streamUrl = movie.streamUrl ?: "",
                        category = movie.category ?: "Live TV",
                        localNumber = movie.localNumber ?: ""  // Added
                    )
                })

                putExtra(TvPlayer.EXTRA_CHANNEL_LIST, channelsList)
                putExtra(TvPlayer.EXTRA_CURRENT_INDEX, 0)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }

            context.startActivity(intent)
        }
    }

    // Show loading or empty state while waiting
    if (isLoading || channels.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}