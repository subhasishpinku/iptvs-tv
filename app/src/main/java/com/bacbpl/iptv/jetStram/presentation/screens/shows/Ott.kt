package com.bacbpl.iptv.jetStram.presentation.screens.shows

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.bacbpl.iptv.jetStram.data.entities.OttWidgetItem
import com.bacbpl.iptv.jetStram.presentation.common.OttWidgetRow
import com.bacbpl.iptv.jetStram.presentation.viewmodel.OttUiState
import com.bacbpl.iptv.jetStram.presentation.viewmodel.OttViewModel


@Composable
fun Ott(
    navController: NavController,
    onNavigateToLeft: () -> Unit,
    viewModel: OttViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
            is OttUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color(0xFFE50914),
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
            is OttUiState.Success -> {
                val widgets = (uiState as OttUiState.Success).widgets
                if (widgets.isEmpty()) {
                    EmptyOttState()
                } else {
                    OttContent(
                        widgets = widgets,
                        onWidgetItemClick = { widgetItem ->
                            navController.navigate("movie_details/${widgetItem.id}")
                        }
                    )
                }
            }
            is OttUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = "Error",
                            tint = Color(0xFFE50914),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = (uiState as OttUiState.Error).message,
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = { viewModel.fetchOttWidgets() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE50914)
                            )
                        ) {
                            Text("Retry", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OttContent(
    widgets: List<com.bacbpl.iptv.jetStram.data.entities.OttWidget>,
    onWidgetItemClick: (OttWidgetItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 2.dp)  // Reduced padding
    ) {
        widgets.forEach { widget ->
            item(contentType = widget.type) {
                OttWidgetRow(
                    widget = widget,
                    onItemClick = onWidgetItemClick,
                    modifier = Modifier.padding(top = 2.dp)  // Reduced top padding
                )
            }
        }
    }
}

@Composable
fun EmptyOttState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = "No Content",
                tint = Color(0xFFE50914),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No OTT Content Available",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check back later for updates",
                color = Color.Gray,
                fontSize = 14.sp
            )

        }
    }}