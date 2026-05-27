package com.bacbpl.iptv.jetStram.presentation.screens.player

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.*
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.data.entities.TvChannel
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.StyledPlayerView
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TvAutoPlayer : ComponentActivity() {
    private var exoPlayer: ExoPlayer? = null

    companion object {
        const val EXTRA_CHANNEL_ID = "id"
        const val EXTRA_CHANNEL_NAME = "name"
        const val EXTRA_CHANNEL_LOGO_URL = "logoUrl"
        const val EXTRA_CHANNEL_STREAM_URL = "streamUrl"
        const val EXTRA_CHANNEL_CATEGORY = "category"
        const val EXTRA_CHANNEL_LOCAL_NUMBER = "localNumber"
        const val EXTRA_CHANNEL_LIST = "channel_list"
        const val EXTRA_CURRENT_INDEX = "current_index"
        const val EXTRA_GENRE_NAME = "genre_name"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            exoPlayer = ExoPlayer.Builder(this).build().apply {
                playWhenReady = true
                repeatMode = Player.REPEAT_MODE_OFF
            }
        } catch (e: Exception) {
            e.printStackTrace()
            finish()
            return
        }

        val channelId = intent.getIntExtra(EXTRA_CHANNEL_ID, -1)
        if (channelId == -1) {
            finish()
            return
        }
        val currentIndex = intent.getIntExtra(EXTRA_CURRENT_INDEX, 0)
        val genreName = intent.getStringExtra(EXTRA_GENRE_NAME) ?: ""

        val channel = TvChannel(
            id = channelId,
            name = intent.getStringExtra(EXTRA_CHANNEL_NAME) ?: "",
            logoUrl = intent.getStringExtra(EXTRA_CHANNEL_LOGO_URL) ?: "",
            streamUrl = intent.getStringExtra(EXTRA_CHANNEL_STREAM_URL) ?: "",
            category = intent.getStringExtra(EXTRA_CHANNEL_CATEGORY) ?: "Live TV",
            localNumber = intent.getStringExtra(EXTRA_CHANNEL_LOCAL_NUMBER) ?: ""
        )

        val channelsList = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableArrayListExtra(EXTRA_CHANNEL_LIST, TvChannel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableArrayListExtra(EXTRA_CHANNEL_LIST)
        } ?: arrayListOf(channel)

        setContent {
            TvAutoPlayerScreen(
                initialChannel = channel,
                allChannels = channelsList,
                initialIndex = currentIndex,
                genreName = genreName,
                exoPlayer = exoPlayer,
                onBackPressed = { finish() }
            )
        }
    }

    override fun onDestroy() {
        try {
            exoPlayer?.run {
                if (playbackState != Player.STATE_IDLE) {
                    playWhenReady = false
                    stop()
                }
                release()
            }
            exoPlayer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onDestroy()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_RIGHT, KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_CHANNEL_UP, KeyEvent.KEYCODE_CHANNEL_DOWN -> false
            KeyEvent.KEYCODE_BACK -> false
            else -> super.onKeyDown(keyCode, event)
        }
    }
}

@Composable
fun TvAutoPlayerScreen(
    initialChannel: TvChannel,
    allChannels: List<TvChannel>,
    initialIndex: Int,
    genreName: String,
    exoPlayer: ExoPlayer?,
    onBackPressed: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var currentChannel by remember { mutableStateOf(initialChannel) }
    var currentIndex by remember { mutableStateOf(initialIndex) }
    var showMenuBar by remember { mutableStateOf(genreName.isNotEmpty()) }
    var showGrid by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var menuFocusedIndex by remember { mutableStateOf(0) }

    val focusRequester = remember { FocusRequester() }
    val gridFocusRequester = remember { FocusRequester() }

    val availableMainCategories = remember(allChannels) {
        allChannels
            .map { it.category.trim() }
            .filter { it.isNotEmpty() }
            .distinct()
    }

    val filteredChannels = remember(allChannels, selectedCategory) {
        if (selectedCategory.isNullOrEmpty()) {
            allChannels
        } else {
            allChannels.filter {
                it.category.equals(selectedCategory, ignoreCase = true)
            }
        }
    }

    val menuItemsFocusRequesters = remember(availableMainCategories.size) {
        List(availableMainCategories.size) { FocusRequester() }
    }

    LaunchedEffect(Unit) {
        delay(300)
        if (genreName.isNotEmpty() && availableMainCategories.isNotEmpty()) {
            selectedCategory = genreName
            showMenuBar = true
            delay(100)
            menuItemsFocusRequesters.getOrNull(0)?.requestFocus()
        }
    }

    val handleBackPress = {
        when {
            showGrid -> {
                showGrid = false
                coroutineScope.launch {
                    delay(50)
                    menuItemsFocusRequesters.getOrNull(menuFocusedIndex)?.requestFocus()
                }
            }
            showMenuBar -> {
                showMenuBar = false
                coroutineScope.launch {
                    delay(50)
                    focusRequester.requestFocus()
                }
            }
            else -> {
                onBackPressed?.invoke()
            }
        }
    }

    val localExoPlayer = exoPlayer ?: remember {
        ExoPlayer.Builder(context).build().apply {
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    fun loadChannel(streamUrl: String) {
        try {
            val mediaItem = MediaItem.fromUri(Uri.parse(streamUrl))
            localExoPlayer.setMediaItem(mediaItem)
            localExoPlayer.prepare()
            localExoPlayer.playWhenReady = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    LaunchedEffect(currentChannel) {
        loadChannel(currentChannel.streamUrl)
    }

    DisposableEffect(Unit) {
        onDispose {
            if (exoPlayer == null) {
                localExoPlayer.run {
                    playWhenReady = false
                    stop()
                    release()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }
// এই অংশটি Replace করুন যেখানে DirectionDown হ্যান্ডল করা হয়েছে

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusable()
            .onPreviewKeyEvent { keyEvent ->
                when {
                    // UP key - Show menu bar
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
                        if (!showMenuBar && availableMainCategories.isNotEmpty()) {
                            showMenuBar = true
                            coroutineScope.launch {
                                delay(100)
                                menuItemsFocusRequesters.getOrNull(menuFocusedIndex)?.requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    // OK/Enter key - Show channel grid (NEW)
                    keyEvent.type == KeyEventType.KeyDown &&
                            (keyEvent.key == Key.Enter || keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) -> {
                        if (selectedCategory != null && !showGrid) {
                            showGrid = true
                            coroutineScope.launch {
                                delay(100)
                                gridFocusRequester.requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    // BACK key handling
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Back -> {
                        handleBackPress()
                        true
                    }
                    else -> false
                }
            }
            .onKeyEvent { keyEvent ->
                when {
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Back -> {
                        handleBackPress()
                        true
                    }

                    // OK/Enter key alternative handling
                    keyEvent.type == KeyEventType.KeyDown &&
                            (keyEvent.key == Key.Enter || keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER) -> {
                        if (selectedCategory != null && !showGrid) {
                            showGrid = true
                            coroutineScope.launch {
                                delay(100)
                                gridFocusRequester.requestFocus()
                            }
                            true
                        } else {
                            false
                        }
                    }

                    // Channel Up/Down buttons
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_CHANNEL_UP -> {
                        if (currentIndex < filteredChannels.size - 1) {
                            currentIndex++
                            currentChannel = filteredChannels[currentIndex]
                            if (showMenuBar) {
                                showMenuBar = false
                                selectedCategory = null
                                showGrid = false
                            }
                        }
                        true
                    }
                    keyEvent.type == KeyEventType.KeyDown && keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN -> {
                        if (currentIndex > 0) {
                            currentIndex--
                            currentChannel = filteredChannels[currentIndex]
                            if (showMenuBar) {
                                showMenuBar = false
                                selectedCategory = null
                                showGrid = false
                            }
                        }
                        true
                    }

                    // Left/Right navigation in menu
                    selectedCategory != null && !showGrid &&
                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight -> {
                        val currentCatIndex = availableMainCategories.indexOfFirst { it == selectedCategory }
                        if (currentCatIndex < availableMainCategories.size - 1) {
                            val newIndex = currentCatIndex + 1
                            selectedCategory = availableMainCategories[newIndex]
                            menuFocusedIndex = newIndex
                            menuItemsFocusRequesters.getOrNull(newIndex)?.requestFocus()
                        }
                        true
                    }
                    selectedCategory != null && !showGrid &&
                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
                        val currentCatIndex = availableMainCategories.indexOfFirst { it == selectedCategory }
                        if (currentCatIndex > 0) {
                            val newIndex = currentCatIndex - 1
                            selectedCategory = availableMainCategories[newIndex]
                            menuFocusedIndex = newIndex
                            menuItemsFocusRequesters.getOrNull(newIndex)?.requestFocus()
                        }
                        true
                    }

                    // Left/Right navigation for channel changing (when no menu/grid)
                    (selectedCategory == null && !showGrid && !showMenuBar) &&
                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight -> {
                        if (currentIndex < filteredChannels.size - 1) {
                            currentIndex++
                            currentChannel = filteredChannels[currentIndex]
                        }
                        true
                    }
                    (selectedCategory == null && !showGrid && !showMenuBar) &&
                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
                        if (currentIndex > 0) {
                            currentIndex--
                            currentChannel = filteredChannels[currentIndex]
                        }
                        true
                    }
                    else -> false
                }
            }
    )
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color.Black)
//            .focusRequester(focusRequester)
//            .focusable()
//            .onPreviewKeyEvent { keyEvent ->
//                when {
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionUp -> {
//                        if (!showMenuBar && availableMainCategories.isNotEmpty()) {
//                            showMenuBar = true
//                            coroutineScope.launch {
//                                delay(100)
//                                menuItemsFocusRequesters.getOrNull(menuFocusedIndex)?.requestFocus()
//                            }
//                            true
//                        } else {
//                            false
//                        }
//                    }
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionDown -> {
//                        if (selectedCategory != null && !showGrid) {
//                            showGrid = true
//                            coroutineScope.launch {
//                                delay(100)
//                                gridFocusRequester.requestFocus()
//                            }
//                            true
//                        } else {
//                            false
//                        }
//                    }
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Back -> {
//                        handleBackPress()
//                        true
//                    }
//                    else -> false
//                }
//            }
//            .onKeyEvent { keyEvent ->
//                when {
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.Back -> {
//                        handleBackPress()
//                        true
//                    }
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_CHANNEL_UP -> {
//                        if (currentIndex < filteredChannels.size - 1) {
//                            currentIndex++
//                            currentChannel = filteredChannels[currentIndex]
//                            if (showMenuBar) {
//                                showMenuBar = false
//                                selectedCategory = null
//                                showGrid = false
//                            }
//                        }
//                        true
//                    }
//                    keyEvent.type == KeyEventType.KeyDown && keyEvent.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_CHANNEL_DOWN -> {
//                        if (currentIndex > 0) {
//                            currentIndex--
//                            currentChannel = filteredChannels[currentIndex]
//                            if (showMenuBar) {
//                                showMenuBar = false
//                                selectedCategory = null
//                                showGrid = false
//                            }
//                        }
//                        true
//                    }
//                    selectedCategory != null && !showGrid &&
//                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight -> {
//                        val currentCatIndex = availableMainCategories.indexOfFirst { it == selectedCategory }
//                        if (currentCatIndex < availableMainCategories.size - 1) {
//                            val newIndex = currentCatIndex + 1
//                            selectedCategory = availableMainCategories[newIndex]
//                            menuFocusedIndex = newIndex
//                            menuItemsFocusRequesters.getOrNull(newIndex)?.requestFocus()
//                        }
//                        true
//                    }
//                    selectedCategory != null && !showGrid &&
//                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
//                        val currentCatIndex = availableMainCategories.indexOfFirst { it == selectedCategory }
//                        if (currentCatIndex > 0) {
//                            val newIndex = currentCatIndex - 1
//                            selectedCategory = availableMainCategories[newIndex]
//                            menuFocusedIndex = newIndex
//                            menuItemsFocusRequesters.getOrNull(newIndex)?.requestFocus()
//                        }
//                        true
//                    }
//                    (selectedCategory == null && !showGrid && !showMenuBar) &&
//                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionRight -> {
//                        if (currentIndex < filteredChannels.size - 1) {
//                            currentIndex++
//                            currentChannel = filteredChannels[currentIndex]
//                        }
//                        true
//                    }
//                    (selectedCategory == null && !showGrid && !showMenuBar) &&
//                            keyEvent.type == KeyEventType.KeyDown && keyEvent.key == Key.DirectionLeft -> {
//                        if (currentIndex > 0) {
//                            currentIndex--
//                            currentChannel = filteredChannels[currentIndex]
//                        }
//                        true
//                    }
//                    else -> false
//                }
//            }
//    )

    {
        AndroidView(
            factory = { ctx ->
                StyledPlayerView(ctx).apply {
                    player = localExoPlayer
                    useController = true
                    setShowRewindButton(false)
                    setShowFastForwardButton(false)
                    setShowPreviousButton(false)
                    setShowNextButton(false)
                    setShowSubtitleButton(false)
                    setShowVrButton(false)
                    findViewById<View>(com.google.android.exoplayer2.ui.R.id.exo_progress)?.visibility = View.GONE
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    setShowBuffering(StyledPlayerView.SHOW_BUFFERING_WHEN_PLAYING)
                    controllerAutoShow = false
                    controllerHideOnTouch = false
                    keepScreenOn = true
                    isFocusable = true
                    isFocusableInTouchMode = true
                }
            },
            update = { view -> view.player = localExoPlayer },
            modifier = Modifier.fillMaxSize()
        )

        // Channel Number Overlay
        Surface(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 10.dp, start = 10.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "LCM", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (currentChannel.localNumber.isNotEmpty()) currentChannel.localNumber else (currentIndex + 1).toString(),
                    color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = currentChannel.name, color = Color.White.copy(alpha = 0.9f), fontSize = 14.sp, maxLines = 1)
            }
        }

        // Top Menu Bar
        if (showMenuBar && availableMainCategories.isNotEmpty()) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(5.dp)
                    .wrapContentWidth()
                    .shadow(elevation = 0.dp, shape = RoundedCornerShape(20.dp)),
                color = Color.Black.copy(alpha = 0.8f),
                shape = RoundedCornerShape(20.dp)
            ) {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    items(availableMainCategories.size) { index ->
                        val category = availableMainCategories[index]
                        val isSelected = category == selectedCategory
                        val isFocused = index == menuFocusedIndex
                        val scale by animateFloatAsState(if (isFocused) 1.1f else 1f)

                        Surface(
                            modifier = Modifier
                                .scale(scale)
                                .focusRequester(menuItemsFocusRequesters[index])
                                .onFocusChanged { if (it.isFocused) menuFocusedIndex = index }
                                .focusable()
                                .onKeyEvent { event ->
                                    if (event.type == KeyEventType.KeyDown &&
                                        (event.nativeKeyEvent.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.key == Key.Enter)) {
                                        selectedCategory = category
                                        showGrid = false
                                        menuFocusedIndex = index
                                        true
                                    } else false
                                }
                                .then(if (isSelected || isFocused) Modifier.border(2.dp, Color.White, RoundedCornerShape(20.dp)) else Modifier),
                            color = if (isSelected) Color.White.copy(alpha = 0.3f) else Color.Transparent,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                Modifier.border(1.dp, Color.White, RoundedCornerShape(20.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val icon = when {
                                    category.equals("Movies", ignoreCase = true) -> Icons.Default.Movie
                                    category.equals("Sports", ignoreCase = true) -> Icons.Default.SportsSoccer
                                    category.equals("News", ignoreCase = true) -> Icons.Default.Info
                                    category.equals("Entertainment", ignoreCase = true) -> Icons.Default.Tv
                                    category.equals("Music", ignoreCase = true) -> Icons.Default.MusicNote
                                    category.equals("Kids", ignoreCase = true) -> Icons.Default.ChildCare
                                    category.equals("Lifestyle", ignoreCase = true) -> Icons.Default.Favorite
                                    category.equals("Religious", ignoreCase = true) -> Icons.Default.Church
                                    else -> Icons.Default.Category
                                }
                                Icon(icon, contentDescription = category, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(category, color = Color.White, fontSize = 14.sp,
                                    fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }
        }

        // Channel Grid
        if (showGrid && filteredChannels.isNotEmpty()) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.85f))) {
                ChannelGrid(
                    channels = filteredChannels,
                    onChannelSelected = { channel ->
                        val newIndex = allChannels.indexOf(channel)
                        if (newIndex != -1) {
                            currentIndex = newIndex
                            currentChannel = channel
                            showGrid = false
                            selectedCategory = null
                            showMenuBar = false
                        }
                    },
                    onClose = {
                        showGrid = false
                        selectedCategory = null
                        showMenuBar = false
                        coroutineScope.launch {
                            delay(50)
                            focusRequester.requestFocus()
                        }
                    }
                )
            }
        }

        // Swipe Overlay
        if (selectedCategory == null && !showGrid && !showMenuBar && filteredChannels.size > 1) {
            Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter), contentAlignment = Alignment.Center) {
                Surface(
                    modifier = Modifier.wrapContentWidth().padding(horizontal = 4.dp, vertical = 2.dp).shadow(1.dp, RoundedCornerShape(16.dp)),
                    color = Color.Black.copy(alpha = 0.85f), shape = RoundedCornerShape(16.dp)
                ) {
                    Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (currentIndex > 0) {
                            val prevChannel = filteredChannels[currentIndex - 1]
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray)) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current).data(prevChannel.logoUrl).crossfade(true).error(R.drawable.logoss).build(),
                                        contentDescription = prevChannel.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Column(modifier = Modifier.width(50.dp)) {
                                    Text("PREV", color = Color.White.copy(alpha = 0.6f), fontSize = 6.sp, fontWeight = FontWeight.Bold)
                                    Text(prevChannel.name.take(8), color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.background(Color(0xFFE50914).copy(alpha = 0.3f), RoundedCornerShape(8.dp)).padding(8.dp, 3.dp)) {
                            Box(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(6.dp)).background(Color.DarkGray)) {
                                AsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current).data(currentChannel.logoUrl).crossfade(true).error(R.drawable.logoss).build(),
                                    contentDescription = currentChannel.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit
                                )
                            }
                            Text(currentChannel.name.take(12), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                        }

                        if (currentIndex < filteredChannels.size - 1) {
                            val nextChan = filteredChannels[currentIndex + 1]
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.width(50.dp), horizontalAlignment = Alignment.End) {
                                    Text("NEXT", color = Color.White.copy(alpha = 0.6f), fontSize = 6.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
                                    Text(nextChan.name.take(8), color = Color.White, fontSize = 7.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End, maxLines = 1)
                                }
                                Box(modifier = Modifier.size(24.dp).clip(RoundedCornerShape(4.dp)).background(Color.DarkGray)) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current).data(nextChan.logoUrl).crossfade(true).error(R.drawable.logoss).build(),
                                        contentDescription = nextChan.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Logo
        Surface(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp).width(150.dp).height(80.dp).shadow(0.dp, RoundedCornerShape(8.dp)),
            color = Color.Black.copy(alpha = 0.0f), shape = RoundedCornerShape(8.dp)
        ) {
            Image(painter = painterResource(id = R.drawable.logoss), contentDescription = "App Logo",
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(4.dp)), contentScale = ContentScale.Fit)
        }
    }
}

@Composable
fun ChannelGrid(
    channels: List<TvChannel>,
    onChannelSelected: (TvChannel) -> Unit,
    onClose: () -> Unit
) {
    val gridState = rememberLazyGridState()
    var focusedIndex by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()
    val itemFocusRequesters = remember(channels.size) { List(channels.size) { FocusRequester() } }

    LaunchedEffect(Unit) {
        delay(200)
        if (channels.isNotEmpty()) {
            itemFocusRequesters[0].requestFocus()
            focusedIndex = 0
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = "${channels.firstOrNull()?.category ?: "Channel"} Channels", color = Color.Red, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                var isCloseFocused by remember { mutableStateOf(false) }
                Button(onClick = onClose, modifier = Modifier.size(40.dp).focusable().onFocusChanged { isCloseFocused = it.isFocused }
                    .then(if (isCloseFocused) Modifier.border(2.dp, Color.White, RoundedCornerShape(8.dp)) else Modifier),
                    colors = ButtonDefaults.buttonColors(containerColor = if (isCloseFocused) Color.Red else Color.DarkGray), shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(24.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                state = gridState,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) {
                itemsIndexed(channels) { index, channel ->
                    val isFocused = index == focusedIndex
                    Card(
                        modifier = Modifier.fillMaxWidth().height(120.dp).focusRequester(itemFocusRequesters[index])
                            .onFocusChanged { if (it.isFocused) { focusedIndex = index; coroutineScope.launch { gridState.animateScrollToItem(index) } } }
                            .focusable().clickable { onChannelSelected(channel) }
                            .then(if (isFocused) Modifier.border(3.dp, Color.Red, RoundedCornerShape(12.dp)) else Modifier),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A)),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize().padding(8.dp)) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current).data(channel.logoUrl).crossfade(true).error(R.drawable.logoss).build(),
                                contentDescription = channel.name,
                                modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp)).background(Color.DarkGray),
                                contentScale = ContentScale.Fit
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = channel.name.take(15),
                                color = if (isFocused) Color.Red else Color.White,
                                fontSize = 11.sp,
                                fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 2, minLines = 2, textAlign = TextAlign.Center
                            )
                            if (channel.localNumber.isNotEmpty()) {
                                Text(text = "LCM ${channel.localNumber}", color = Color.Gray, fontSize = 9.sp, maxLines = 1)
                            }
                        }
                    }
                }
            }
        }

        // Hint text at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp)
        ) {
            Text(
                text = "▲ ▼ ◀ ▶ to navigate | OK to select | BACK to close",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}