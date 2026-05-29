package com.bacbpl.iptv.jetStram.presentation.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.data.entities.OttWidget
import com.bacbpl.iptv.jetStram.data.entities.OttWidgetItem
import com.bacbpl.iptv.jetStram.presentation.screens.dashboard.rememberChildPadding

@Composable
fun OttWidgetRow(
    widget: OttWidget,
    onItemClick: (OttWidgetItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val childPadding = rememberChildPadding()
    val listState = rememberLazyListState()
    val firstItemFocusRequester = remember { FocusRequester() }

    Column(modifier = modifier) {
        Text(
            text = widget.name,
            style = MaterialTheme.typography.headlineMedium.copy(
                color = Color.White
            ),
            modifier = Modifier.padding(
                start = childPadding.start,
                top = 8.dp,      // Reduced from 16dp to 8dp
                bottom = 6.dp    // Reduced from 12dp to 6dp
            )
        )

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(8.dp),  // Reduced from 12dp to 8dp
            contentPadding = PaddingValues(
                start = childPadding.start,
                end = childPadding.end
            )
        ) {
            itemsIndexed(widget.items) { index, item ->
                OttWidgetItemCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    modifier = if (index == 0) {
                        Modifier.focusRequester(firstItemFocusRequester)
                    } else Modifier
                )
            }
        }
    }
}

@Composable
fun OttWidgetItemCard(
    item: OttWidgetItem,
    onClick: (OttWidgetItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isFocused) 1.05f else 1f)

    LaunchedEffect(item.id) {
        android.util.Log.d("OttWidgetItemCard", "Item: ${item.title}, Poster URL: ${item.posterUrl}")
    }

    Card(
        modifier = modifier
            .width(140.dp)
            .aspectRatio(0.65f)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused }
            .border(
                width = if (isFocused) 2.dp else 0.dp,
                color = Color.Red,
                shape = RoundedCornerShape(10.dp)
            ),
        shape = CardDefaults.shape(shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.colors(
            containerColor = Color(0xFF1A1A1A),
            focusedContainerColor = Color(0xFF252525)
        ),
        scale = CardDefaults.scale(focusedScale = 1f),
        onClick = { onClick(item) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)  // Reduced from 8dp to 6dp
        ) {
            // Poster Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(6.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.posterUrl)
                        .crossfade(true)
                        .error(R.drawable.logoss)
                        .build(),
                    contentDescription = item.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.7f),
                                    Color.Transparent
                                )
                            )
                        )
                        .padding(4.dp)  // Reduced from 6dp to 4dp
                ) {
                    Text(
                        text = item.format.uppercase().take(3),
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 9.sp,  // Reduced from 10sp to 9sp
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Text(
                        text = item.language.uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,  // Reduced from 11sp to 10sp
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))  // Reduced from 6dp to 4dp

            // Movie Title
            Text(
                text = item.title,
                color = if (isFocused) Color.Red else Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,  // Reduced from 13sp to 12sp
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(3.dp))  // Reduced from 4dp to 3dp

            // Info Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp),  // Reduced from 4dp to 3dp
                verticalAlignment = Alignment.CenterVertically
            ) {
                InfoBadgeSmall(
                    label = "Year",
                    value = "${item.releaseYear}".take(4)
                )
                InfoBadgeSmall(
                    label = "Lang",
                    value = item.language.take(2)
                )
                InfoBadgeSmall(
                    label = "⭐",
                    value = if (item.rating > 0) String.format("%.1f", item.rating) else "NR"
                )
            }

            Spacer(modifier = Modifier.height(2.dp))

            // Provider/Genre info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(
                    text = item.ottProvider.ifEmpty { "Unknown" },
                    color = Color(0xFFFFA500),
                    fontSize = 8.sp,  // Reduced from 9sp to 8sp
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = item.genre.split(",").firstOrNull()?.trim()?.take(10) ?: "",
                    color = Color.Gray,
                    fontSize = 8.sp,  // Reduced from 9sp to 8sp
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun InfoBadgeSmall(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 7.sp,  // Reduced from 8sp to 7sp
            maxLines = 1
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 9.sp,  // Reduced from 10sp to 9sp
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}