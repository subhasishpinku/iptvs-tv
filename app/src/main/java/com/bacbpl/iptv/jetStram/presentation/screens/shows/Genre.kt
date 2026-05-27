package com.bacbpl.iptv.jetStram.presentation.screens.shows

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.bacbpl.iptv.jetStram.data.models.Genre
import com.bacbpl.iptv.jetStram.presentation.screens.player.GenrePlayerActivity
import com.bacbpl.iptv.jetStram.presentation.viewmodels.GenreUiState
import com.bacbpl.iptv.jetStram.presentation.viewmodels.GenreViewModel

@Composable
fun GenreScreen(
    navController: NavController,
    viewModel: GenreViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigateToPlayer by viewModel.navigateToPlayer.collectAsState()
    val context = LocalContext.current

    // Store selected genre name separately
    var selectedGenreName by remember { mutableStateOf("") }

    // Handle navigation - Start Activity instead of Compose navigation
    LaunchedEffect(navigateToPlayer) {
        navigateToPlayer?.let { (genreId, genreName) ->  // Destructure Pair
            val intent = Intent(context, GenrePlayerActivity::class.java).apply {
                putExtra("genre_id", genreId)
                putExtra("genre_name", genreName)
            }
            context.startActivity(intent)
            viewModel.onNavigationConsumed()
        }
    }

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
    ) {
        when (uiState) {
            is GenreUiState.Loading -> {
                LoadingScreen()
            }
            is GenreUiState.Success -> {
                GenreContent(
                    genres = (uiState as GenreUiState.Success).genres,
                    onGenreClick = { genre ->
                        selectedGenreName = genre.name  // Store the genre name
                        viewModel.onGenreClick(genre)
                    }
                )
            }
            is GenreUiState.Error -> {
                ErrorScreen(
                    message = (uiState as GenreUiState.Error).message,
                    onRetry = { viewModel.fetchGenres() }
                )
            }
        }
    }
}

// Rest of the composables remain the same...
@Composable
fun GenreContent(
    genres: List<Genre>,
    onGenreClick: (Genre) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(5.dp)
    ) {
        Spacer(modifier = Modifier.height(5.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(genres) { genre ->
                GenreItem(
                    genre = genre,
                    onClick = { onGenreClick(genre) }
                )
            }
        }
    }
}

@Composable
fun GenreItem(
    genre: Genre,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)  // Reduced from 140dp to 100dp
            .scale(if (isFocused) 1.04f else 1f)  // Reduced scale effect
            .onFocusChanged {
                isFocused = it.isFocused
            }
            .focusable(),
        shape = RoundedCornerShape(12.dp),  // Reduced from 18dp to 12dp
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) Color(0xFFE50914) else Color(0xFF1A2230)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isFocused) 8.dp else 3.dp  // Reduced elevation
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),  // Reduced from 14dp to 10dp
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.LocalMovies,
                contentDescription = genre.name,
                tint = Color.White,
                modifier = Modifier.size(28.dp)  // Reduced from 42dp to 28dp
            )

            Spacer(modifier = Modifier.height(8.dp))  // Reduced from 12dp to 8dp

            Text(
                text = genre.name,
                color = Color.White,
                fontSize = 14.sp,  // Reduced from 18sp to 14sp
                fontWeight = FontWeight.SemiBold
            )

            if (genre.categories.isNotEmpty()) {
                Text(
                    text = "${genre.categories.size} categories",
                    color = Color.Gray,
                    fontSize = 10.sp  // Reduced from 12sp to 10sp
                )
            }
        }
    }
}

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFE50914),
            modifier = Modifier.size(40.dp)  // Reduced from 48dp to 40dp
        )
    }
}

@Composable
fun ErrorScreen(
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
            modifier = Modifier.size(48.dp)  // Reduced from 64dp to 48dp
        )

        Spacer(modifier = Modifier.height(12.dp))  // Reduced from 16dp to 12dp

        Text(
            text = message,
            color = Color.White,
            fontSize = 14.sp  // Reduced from 16sp to 14sp
        )

        Spacer(modifier = Modifier.height(16.dp))  // Reduced from 24dp to 16dp

        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE50914)
            )
        ) {
            Text(
                text = "Retry",
                fontSize = 12.sp  // Added smaller font for button text
            )
        }
    }
}