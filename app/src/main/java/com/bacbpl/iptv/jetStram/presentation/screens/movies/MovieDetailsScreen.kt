package com.bacbpl.iptv.jetStram.presentation.screens.movies

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.data.entities.Movie
import com.bacbpl.iptv.jetStram.data.util.StringConstants
import com.bacbpl.iptv.jetStram.data.entities.MovieDetails
import com.bacbpl.iptv.jetStram.presentation.common.Error
import com.bacbpl.iptv.jetStram.presentation.common.Loading
import com.bacbpl.iptv.jetStram.presentation.common.MoviesRow
import com.bacbpl.iptv.jetStram.presentation.screens.dashboard.rememberChildPadding
import com.bacbpl.iptv.jetStram.presentation.screens.movies.MovieDetails as MovieDetailsComposable

object MovieDetailsScreen {
    const val MovieIdBundleKey = "movieId"
}

@Composable
fun MovieDetailsScreen(
    goToMoviePlayer: (Movie) -> Unit,
    onBackPressed: () -> Unit,
    refreshScreenWithNewMovie: (Movie) -> Unit,
    movieDetailsScreenViewModel: MovieDetailsScreenViewModel = hiltViewModel()
) {
    val uiState by movieDetailsScreenViewModel.uiState.collectAsStateWithLifecycle()

    when (val s = uiState) {
        is MovieDetailsScreenUiState.Loading -> {
            Loading(modifier = Modifier.fillMaxSize())
        }
        is MovieDetailsScreenUiState.Error -> {
            Error(modifier = Modifier.fillMaxSize())
        }
        is MovieDetailsScreenUiState.Done -> {
            val movie = Movie(
                id = s.movieDetails.id,
                videoUri = s.movieDetails.videoUri,
                subtitleUri = s.movieDetails.subtitleUri,
                posterUri = s.movieDetails.posterUri,
                name = s.movieDetails.name,
                description = s.movieDetails.description
            )
            Details(
                movieDetails = s.movieDetails,
                goToMoviePlayer = { goToMoviePlayer(movie) },
                onBackPressed = onBackPressed,
                refreshScreenWithNewMovie = refreshScreenWithNewMovie,
                modifier = Modifier
                    .fillMaxSize()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun Details(
    movieDetails: MovieDetails,
    goToMoviePlayer: () -> Unit,
    onBackPressed: () -> Unit,
    refreshScreenWithNewMovie: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    val childPadding = rememberChildPadding()

    BackHandler(onBack = onBackPressed)
    LazyColumn(
        contentPadding = PaddingValues(bottom = 135.dp),
        modifier = modifier,
    ) {
        item {
            MovieDetailsComposable(
                movieDetails = movieDetails,
                goToMoviePlayer = goToMoviePlayer,
                ottplayUrl = movieDetails.ottplayUrl
            )
        }
        item {
            CastAndCrewList(castAndCrew = movieDetails.castAndCrew)
        }
        item {
            MoviesRow(
                title = StringConstants.Composable.movieDetailsScreenSimilarTo(movieDetails.name).toString(),
                titleStyle = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                movieList = movieDetails.similarMovies,
                onMovieSelected = refreshScreenWithNewMovie
            )
        }
        item {
            MovieReviews(
                modifier = Modifier.padding(top = childPadding.top),
                reviewsAndRatings = movieDetails.reviewsAndRatings
            )
        }
        item {
            Box(
                modifier = Modifier
                    .padding(horizontal = childPadding.start)
                    .padding(BottomDividerPadding)
                    .fillMaxWidth()
                    .height(1.dp)
                    .alpha(0.15f)
                    .background(Color.White)
            )
        }
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = childPadding.start),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val itemModifier = Modifier.width(192.dp)

                // Status - WHITE text
                Column(modifier = itemModifier) {
                    Text(
                        text = stringResource(R.string.status),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                    Text(
                        text = movieDetails.status,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White
                        ),
                        maxLines = 3
                    )
                }

                // Original Language - WHITE text
                Column(modifier = itemModifier) {
                    Text(
                        text = stringResource(R.string.original_language),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                    Text(
                        text = movieDetails.originalLanguage,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White
                        ),
                        maxLines = 3
                    )
                }

                // Budget - WHITE text
                Column(modifier = itemModifier) {
                    Text(
                        text = stringResource(R.string.budget),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                    Text(
                        text = movieDetails.budget,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White
                        ),
                        maxLines = 3
                    )
                }

                // Revenue - WHITE text
                Column(modifier = itemModifier) {
                    Text(
                        text = stringResource(R.string.revenue),
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.75f)
                        )
                    )
                    Text(
                        text = movieDetails.revenue,
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Normal,
                            color = Color.White
                        ),
                        maxLines = 3
                    )
                }
            }
        }
    }
}

private val BottomDividerPadding = PaddingValues(vertical = 48.dp)