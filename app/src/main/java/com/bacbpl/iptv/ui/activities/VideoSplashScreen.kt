package com.bacbpl.iptv.ui.activities

import android.view.ViewGroup
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import kotlinx.coroutines.delay

@Composable
fun VideoSplashScreen(
    onSplashFinished: () -> Unit
) {
    val context = LocalContext.current
    var isVideoEnded by remember { mutableStateOf(false) }
    var startFadeOut by remember { mutableStateOf(false) }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val videoUri = "android.resource://${context.packageName}/${com.bacbpl.iptv.R.raw.loading_video}"
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true

            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED && !isVideoEnded) {
                        isVideoEnded = true
                        startFadeOut = true
                    }
                }
            })
        }
    }

    // Handle fade out and navigation
    LaunchedEffect(startFadeOut) {
        if (startFadeOut) {
            delay(500) // Wait for fade animation
            onSplashFinished()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.apply {
                playWhenReady = false
                stop()
                release()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Video with fade out animation
        if (!startFadeOut) {
            AnimatedVisibility(
                visible = true,
                exit = fadeOut(animationSpec = tween(durationMillis = 300))
            ) {
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = exoPlayer
                            useController = false
                            setShutterBackgroundColor(android.graphics.Color.BLACK)
                            layoutParams = ViewGroup.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT
                            )
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // Show loading indicator while transitioning
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = Color(0xFFE50914),
                    modifier = Modifier.size(56.dp),
                    strokeWidth = 4.dp
                )
            }
        }
    }
}