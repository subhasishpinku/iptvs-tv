package com.bacbpl.iptv.jetStram.presentation.screens.player

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bacbpl.iptv.jetStram.data.entities.TvChannel
import com.bacbpl.iptv.jetStram.data.models.toTvChannel
import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
import com.bacbpl.iptv.jetStram.presentation.viewmodel.GenreChannelUiState
import com.bacbpl.iptv.jetStram.presentation.viewmodel.GenreChannelViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenrePlayerActivity : ComponentActivity() {

    private var genreId: Int = 0
    private var genreName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        genreId = intent.getIntExtra("genre_id", 0)
        genreName = intent.getStringExtra("genre_name") ?: ""

        println("GenrePlayerActivity - genreId: $genreId, genreName: $genreName")

        setContent {
            JetStreamTheme {
                GenrePlayerLoaderScreen(
                    genreId = genreId,
                    genreName = genreName,
                    onBackPressed = { finish() }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GenrePlayerLoaderScreen(
    genreId: Int,
    genreName: String,
    onBackPressed: () -> Unit,
    viewModel: GenreChannelViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(genreId) {
        viewModel.fetchChannels(genreId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = genreName.ifEmpty { "Genre Content" },
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A2230)
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A0E14),
                            Color(0xFF11161F)
                        )
                    )
                )
                .padding(paddingValues)
        ) {
            when (uiState) {
                is GenreChannelUiState.Loading -> {
                    LoadingGenreScreen()
                }
                is GenreChannelUiState.Success -> {
                    val channels = (uiState as GenreChannelUiState.Success).channels
                    println("Channels Size = ${channels.size}")
                    println("Channels Data = $channels")

                    if (channels.isEmpty()) {
                        EmptyGenreScreen()
                    } else {
                        // Convert to TvChannel and set category to genreName
                        val tvChannels = channels.map { channel ->
                            TvChannel(
                                id = channel.id,
                                name = channel.name,
                                logoUrl = channel.logo ?: "",
                                streamUrl = channel.streamUrl,
                                category = genreName.ifEmpty { channel.name }, // Use genreName as category
                                localNumber = channel.localNumber
                            )
                        }

                        LaunchedEffect(tvChannels) {
                            if (tvChannels.isNotEmpty()) {
                                val intent = Intent(context, TvAutoPlayer::class.java).apply {
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_ID, tvChannels[0].id)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_NAME, tvChannels[0].name)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_LOGO_URL, tvChannels[0].logoUrl)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_STREAM_URL, tvChannels[0].streamUrl)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_CATEGORY, tvChannels[0].category)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_LOCAL_NUMBER, tvChannels[0].localNumber)
                                    putExtra(TvAutoPlayer.EXTRA_CHANNEL_LIST, ArrayList(tvChannels))
                                    putExtra(TvAutoPlayer.EXTRA_CURRENT_INDEX, 0)
                                    putExtra(TvAutoPlayer.EXTRA_GENRE_NAME, genreName)
                                }
                                context.startActivity(intent)
                                onBackPressed()
                            }
                        }
                    }
                }
                is GenreChannelUiState.Error -> {
                    ErrorGenreScreen(
                        message = (uiState as GenreChannelUiState.Error).message,
                        onRetry = { viewModel.fetchChannels(genreId) }
                    )
                }
            }
        }
    }
}
@Composable
fun LoadingGenreScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = Color(0xFFE50914),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Loading channels...",
                color = Color.Gray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun EmptyGenreScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Error,
                contentDescription = "No Channels",
                tint = Color.Gray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No channels available",
                color = Color.Gray,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun ErrorGenreScreen(
    message: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = "Error",
            tint = Color(0xFFE50914),
            modifier = Modifier.size(64.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE50914)
            )
        ) {
            Text("Retry")
        }
    }
}