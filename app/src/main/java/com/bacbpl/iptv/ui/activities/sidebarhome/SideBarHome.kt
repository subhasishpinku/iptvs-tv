//package com.bacbpl.iptv.ui.activities.sidebarhome
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.focusable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Logout
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.tv.material3.Icon
//import androidx.tv.material3.Text
//import com.bacbpl.iptv.ui.activities.StartScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.*
//import com.bacbpl.iptv.jetStram.presentation.screens.Device.DeviceSection
//import com.bacbpl.iptv.jetStram.presentation.screens.Screens
//import com.bacbpl.iptv.jetStram.presentation.screens.dashboard.DashboardScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.ProfileScreens
//import com.bacbpl.iptv.jetStram.presentation.screens.shows.ShowsAutoScreen
//import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
//import com.bacbpl.iptv.utils.LanguageManager
//import com.bacbpl.iptv.utils.UserSession
//import com.bacbpl.iptv.utils.rememberLanguageState
//import dagger.hilt.android.AndroidEntryPoint
//
//@AndroidEntryPoint
//class SideBarHome : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            JetStreamTheme {
//                TVHomeScreen()
//            }
//        }
//    }
//}
//
//@Composable
//fun TVHomeScreen() {
//    val navController = rememberNavController()
//    var isSidebarExpanded by remember { mutableStateOf(false) }
//    var selectedMenu by remember { mutableStateOf(ProfileScreens.Accounts) }
//
//    val menuItems = ProfileScreens.entries.toList()
//
//    // Shared state for subtitles
//    var isSubtitlesChecked by remember { mutableStateOf(true) }
//
//    // Language state
//    val languageState = rememberLanguageState()
//    var selectedLanguageIndex by remember {
//        mutableIntStateOf(
//            when (languageState.currentLanguage) {
//                LanguageManager.LANGUAGE_BENGALI -> 1
//                LanguageManager.LANGUAGE_HINDI -> 2
//                else -> 0
//            }
//        )
//    }
//
//    // Required for DashboardScreen
//    var isComingBackFromDifferentScreen by remember { mutableStateOf(false) }
//
//    Row(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF0A0E14))
//    ) {
//        // SIDEBAR
//        AnimatedContent(targetState = isSidebarExpanded, label = "sidebar_animation") { expanded ->
//            if (expanded) {
//                ExpandedSidebar(
//                    selectedItem = selectedMenu,
//                    menuItems = menuItems,
//                    onItemSelected = { screen ->
//                        selectedMenu = screen
//                        navController.navigate(screen())
//                    },
//                    onCollapse = { isSidebarExpanded = false }
//                )
//            } else {
//                CollapsedSidebar(
//                    selectedItem = selectedMenu,
//                    menuItems = menuItems,
//                    onItemSelected = { screen ->
//                        selectedMenu = screen
//                        navController.navigate(screen())
//                    },
//                    onExpand = { isSidebarExpanded = true }
//                )
//            }
//        }
//
//        // MAIN CONTENT
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(Color(0xFF0A0E14), Color(0xFF11161F))
//                    )
//                )
//                .padding(24.dp)
//        ) {
//            val titleText = selectedMenu.tabTitle?.let { stringResource(id = it) } ?: selectedMenu.name
//            Text(
//                text = titleText,
//                color = Color.White,
//                fontSize = 32.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(30.dp))
//
//            NavHost(
//                navController = navController,
//                startDestination = ProfileScreens.Accounts()
//            ) {
//
////                composable(ProfileScreens.Live()) {
////                    ShowsAutoScreen(
////                        onTVShowClick = { movie -> openMovieDetailsScreen(movie.id) },
////                        onScroll = updateTopBarVisibility,
////                        isTopBarVisible = isTopBarVisible
////                    )
////                }
//                // Dashboard Screen (main app)
////                composable(route = ProfileScreens.Dashboard()) {
////                    DashboardScreen(
////                        openCategoryMovieList = { categoryId ->
////                            navController.navigate(
////                                Screens.CategoryMovieList.withArgs(categoryId)
////                            )
////                        },
////                        openMovieDetailsScreen = { movieId ->
////                            navController.navigate(
////                                Screens.MovieDetails.withArgs(movieId)
////                            )
////                        },
////                        openVideoPlayer = { movie ->
////                            navController.navigate(
////                                Screens.VideoPlayer.withArgs(movie.id)
////                            )
////                        },
////                        onBackPressed = { /* Handle back from dashboard - finish activity */ },
////                        isComingBackFromDifferentScreen = isComingBackFromDifferentScreen,
////                        resetIsComingBackFromDifferentScreen = {
////                            isComingBackFromDifferentScreen = false
////                        }
////                    )
////                }
//
//                composable(ProfileScreens.Accounts()) {
//                    AccountsSection(onNavigateToLeft = {})
//                }
//                composable(ProfileScreens.Live()) {
//                    ShowsAutoScreen(
//                        onTVShowClick = { movie ->
//                            // Handle movie click - you can show a toast or navigate
//                            android.util.Log.d("SideBarHome", "Movie clicked: ${movie.name}")
//                        },
//                        onScroll = { /* No top bar in sidebar mode, so do nothing */ },
//                        isTopBarVisible = false
//                    )
//                }
//                composable(ProfileScreens.Wallet()) {
//                    WalletSection(
//                        isSubtitlesChecked = isSubtitlesChecked,
//                        onSubtitleCheckChange = { isSubtitlesChecked = it }
//                    )
//                }
//                composable(ProfileScreens.Device()) {
//                    DeviceSection(
//                        isSubtitlesChecked = isSubtitlesChecked,
//                        onSubtitleCheckChange = { isSubtitlesChecked = it }
//                    )
//                }
//                composable(ProfileScreens.About()) {
//                    AboutSection()
//                }
//                composable(ProfileScreens.Language()) {
//                    LanguageSection(
//                        selectedIndex = selectedLanguageIndex,
//                        onSelectedIndexChange = { index ->
//                            selectedLanguageIndex = index
//                            val languageCode = when (index) {
//                                1 -> LanguageManager.LANGUAGE_BENGALI
//                                2 -> LanguageManager.LANGUAGE_HINDI
//                                else -> LanguageManager.LANGUAGE_ENGLISH
//                            }
//                            languageState.updateLanguage(languageCode)
//                        }
//                    )
//                }
//                composable(ProfileScreens.SearchHistory()) {
//                    SearchHistorySection()
//                }
//                composable(ProfileScreens.HelpAndSupport()) {
//                    HelpAndSupportSection(
//                        onNavigateToPrivacyPolicy = { /* show dialog */ },
//                        onNavigateToFAQ = { /* show FAQ */ },
//                        onNavigateToContact = { /* show contact dialog */ }
//                    )
//                }
//            }
//        }
//    }
//}
//
//// ================= SIDEBAR COMPONENTS (unchanged) =================
//@Composable
//fun ExpandedSidebar(
//    selectedItem: ProfileScreens,
//    menuItems: List<ProfileScreens>,
//    onItemSelected: (ProfileScreens) -> Unit,
//    onCollapse: () -> Unit
//) {
//    val context = LocalContext.current
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(260.dp)
//            .background(Color(0xFF11161F))
//            .padding(16.dp)
//    ) {
//        IconButton(
//            onClick = onCollapse,
//            modifier = Modifier
//                .size(56.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(Color(0xFF1A2029))
//        ) {
//            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//
//        menuItems.forEach { screen ->
//            val title = screen.tabTitle?.let { stringResource(id = it) } ?: screen.name
//            SidebarMenuItem(
//                title = title,
//                icon = screen.icon,
//                isSelected = selectedItem == screen,
//                onClick = { onItemSelected(screen) }
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//        SidebarMenuItem(
//            title = "Logout",
//            icon = Icons.Default.Logout,
//            isSelected = false,
//            onClick = {
//                UserSession.clearSession(context)
//                val intent = Intent(context, StartScreen::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                context.startActivity(intent)
//                (context as? android.app.Activity)?.finish()
//            }
//        )
//    }
//}
//
//@Composable
//fun CollapsedSidebar(
//    selectedItem: ProfileScreens,
//    menuItems: List<ProfileScreens>,
//    onItemSelected: (ProfileScreens) -> Unit,
//    onExpand: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(90.dp)
//            .background(Color(0xFF11161F))
//            .padding(vertical = 20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        IconButton(
//            onClick = onExpand,
//            modifier = Modifier
//                .size(56.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(Color(0xFF1A2029))
//        ) {
//            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
//        }
//        Spacer(modifier = Modifier.height(30.dp))
//
//        menuItems.forEach { screen ->
//            CollapsedSidebarItem(
//                title = screen.tabTitle?.let { stringResource(id = it) } ?: screen.name,
//                icon = screen.icon,
//                isSelected = selectedItem == screen,
//                onClick = { onItemSelected(screen) }
//            )
//            Spacer(modifier = Modifier.height(18.dp))
//        }
//    }
//}
//
//@Composable
//fun SidebarMenuItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
//    var isFocused by remember { mutableStateOf(false) }
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(14.dp))
//            .background(
//                when {
//                    isSelected -> Color(0xFF2D7A6E)
//                    isFocused -> Color(0xFF1A2029)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { isFocused = it.isFocused }
//            .focusable()
//            .padding(14.dp)
//            .clickable { onClick() },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(icon, contentDescription = title, tint = Color.White)
//        Spacer(modifier = Modifier.width(14.dp))
//        Text(title, color = Color.White, fontSize = 16.sp)
//    }
//}
//
//@Composable
//fun CollapsedSidebarItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
//    var isFocused by remember { mutableStateOf(false) }
//    Box(
//        modifier = Modifier
//            .size(56.dp)
//            .clip(RoundedCornerShape(14.dp))
//            .background(
//                when {
//                    isSelected -> Color(0xFF2D7A6E)
//                    isFocused -> Color(0xFF1A2029)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { isFocused = it.isFocused }
//            .focusable()
//            .padding(12.dp)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            icon, contentDescription = title,
//            tint = if (isSelected || isFocused) Color.White else Color(0xFF9CA3AF),
//            modifier = Modifier.size(26.dp)
//        )
//    }
//}

//package com.bacbpl.iptv.ui.activities.sidebarhome
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.animation.AnimatedContent
//import androidx.compose.animation.core.animateFloatAsState
//import androidx.compose.animation.core.tween
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.focusable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.AccountBalanceWallet
//import androidx.compose.material.icons.outlined.Devices
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.draw.rotate
//import androidx.compose.ui.focus.onFocusChanged
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.vector.ImageVector
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.tv.material3.Icon
//import androidx.tv.material3.Text
//import com.bacbpl.iptv.R
//import com.bacbpl.iptv.jetStram.data.entities.Movie
//import com.bacbpl.iptv.jetStram.data.entities.OttWidgetItem
//import com.bacbpl.iptv.ui.activities.StartScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.*
//import com.bacbpl.iptv.jetStram.presentation.screens.Device.DeviceSection
//import com.bacbpl.iptv.jetStram.presentation.screens.home.HomeScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.player.TvPlayer
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.ProfileScreens
//import com.bacbpl.iptv.jetStram.presentation.screens.shows.ShowsAutoScreen
//import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
//import com.bacbpl.iptv.utils.LanguageManager
//import com.bacbpl.iptv.utils.UserSession
//import com.bacbpl.iptv.utils.rememberLanguageState
//import dagger.hilt.android.AndroidEntryPoint
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.bacbpl.iptv.jetStram.presentation.screens.home.TvChannelViewModel
//
//@AndroidEntryPoint
//class SideBarHome : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            JetStreamTheme {
//                TVHomeScreen()
//            }
//        }
//    }
//}
//
//// Data class for menu items
//data class MenuItem(
//    val title: String,
//    val icon: ImageVector,
//    val route: String,
//    val isSettingsItem: Boolean = false
//)
//
//@Composable
//fun TVHomeScreen() {
//    val navController = rememberNavController()
//    var isSidebarExpanded by remember { mutableStateOf(false) }
//    var selectedMenu by remember { mutableStateOf(ProfileScreens.Movies()) }
//    var isSettingsExpanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//
//    // Language state
//    val languageState = rememberLanguageState()
//    var selectedLanguageIndex by remember {
//        mutableIntStateOf(
//            when (languageState.currentLanguage) {
//                LanguageManager.LANGUAGE_BENGALI -> 1
//                LanguageManager.LANGUAGE_HINDI -> 2
//                else -> 0
//            }
//        )
//    }
//
//    // Define menu items
//    val mainMenuItems = listOf(
//        MenuItem("Movies", Icons.Default.Movie, ProfileScreens.Movies()),
//        MenuItem("Live", Icons.Default.Tv, ProfileScreens.Live()),
//    )
//
//    val settingsMenuItems = listOf(
//        MenuItem("Accounts", Icons.Default.Person, ProfileScreens.Accounts()),
//        MenuItem("Payment", Icons.Outlined.AccountBalanceWallet, ProfileScreens.Wallet()),
//        MenuItem("Device Info", Icons.Outlined.Devices, ProfileScreens.Device()),
//        MenuItem("About", Icons.Default.Info, ProfileScreens.About()),
//        MenuItem("Language", Icons.Default.Translate, ProfileScreens.Language()),
//        MenuItem("Search History", Icons.Default.Search, ProfileScreens.SearchHistory()),
//        MenuItem("Help & Support", Icons.Default.Support, ProfileScreens.HelpAndSupport())
//    )
//
//    // Function to navigate to movie details
//    fun navigateToMovieDetails(movieId: String) {
//        // You can implement navigation here
//        android.util.Log.d("SideBarHome", "Navigate to movie: $movieId")
//    }
//
//    // Function to open video player
//    fun openVideoPlayer(movie: Movie) {
//        android.util.Log.d("SideBarHome", "Open video player: ${movie.name}")
//    }
//
//    Row(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF0A0E14))
//    ) {
//        // SIDEBAR
//        AnimatedContent(targetState = isSidebarExpanded, label = "sidebar_animation") { expanded ->
//            if (expanded) {
//                ExpandedSidebar(
//                    selectedItem = selectedMenu,
//                    mainMenuItems = mainMenuItems,
//                    settingsMenuItems = settingsMenuItems,
//                    isSettingsExpanded = isSettingsExpanded,
//                    onSettingsExpandedChange = { isSettingsExpanded = it },
//                    onItemSelected = { route ->
//                        selectedMenu = route
//                        navController.navigate(route)
//                    },
//                    onCollapse = { isSidebarExpanded = false }
//                )
//            } else {
//                CollapsedSidebar(
//                    selectedItem = selectedMenu,
//                    mainMenuItems = mainMenuItems,
//                    settingsMenuItems = settingsMenuItems,
//                    isSettingsExpanded = isSettingsExpanded,
//                    onItemSelected = { route ->
//                        selectedMenu = route
//                        navController.navigate(route)
//                    },
//                    onExpand = { isSidebarExpanded = true }
//                )
//            }
//        }
//
//        // MAIN CONTENT
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .fillMaxHeight()
//                .background(
//                    Brush.verticalGradient(
//                        colors = listOf(Color(0xFF0A0E14), Color(0xFF11161F))
//                    )
//                )
//                .padding(24.dp)
//        ) {
//            Text(
//                text = selectedMenu,
//                color = Color.White,
//                fontSize = 32.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(30.dp))
//
//            NavHost(
//                navController = navController,
//                startDestination = ProfileScreens.Movies()
//            ) {
//                composable(ProfileScreens.Movies()) {
//                    val tvChannelViewModel: TvChannelViewModel = hiltViewModel()
//                    HomeScreen(
//                        onMovieClick = { selectedMovie ->
//                            navigateToMovieDetails(selectedMovie.id)
//                        },
//                        goToVideoPlayer = { movie ->
//                            openVideoPlayer(movie)
//                        },
//                        onTvChannelClick = { channel ->
//                            val allChannels = tvChannelViewModel.channels.value
//                            val currentIndex = allChannels.indexOfFirst { it.id == channel.id }
//
//                            val intent = Intent(context, TvPlayer::class.java).apply {
//                                putExtra("id", channel.id)
//                                putExtra("name", channel.name)
//                                putExtra("logoUrl", channel.logoUrl)
//                                putExtra("streamUrl", channel.streamUrl)
//                                putExtra("category", channel.category)
//                                putExtra("current_index", currentIndex)
//                                putParcelableArrayListExtra("channel_list", ArrayList(allChannels))
//                            }
//                            context.startActivity(intent)
//                        },
//                        onWidgetItemClick = { widgetItem: OttWidgetItem ->
//                            navigateToMovieDetails(widgetItem.id)
//                        },
//                        onScroll = { /* No scroll handling in sidebar mode */ },
//                        isTopBarVisible = false
//                    )
//                }
//                composable(ProfileScreens.Live()) {
//                    ShowsAutoScreen(
//                        onTVShowClick = { movie ->
//                            android.util.Log.d("SideBarHome", "Movie clicked: ${movie.name}")
//                        },
//                        onScroll = {},
//                        isTopBarVisible = false
//                    )
//                }
//                composable(ProfileScreens.Accounts()) {
//                    AccountsSection(onNavigateToLeft = {})
//                }
//                composable(ProfileScreens.Wallet()) {
//                    WalletSection(
//                        isSubtitlesChecked = true,
//                        onSubtitleCheckChange = {}
//                    )
//                }
//                composable(ProfileScreens.Device()) {
//                    DeviceSection(
//                        isSubtitlesChecked = true,
//                        onSubtitleCheckChange = {}
//                    )
//                }
//                composable(ProfileScreens.About()) {
//                    AboutSection()
//                }
//                composable(ProfileScreens.Language()) {
//                    LanguageSection(
//                        selectedIndex = selectedLanguageIndex,
//                        onSelectedIndexChange = { index ->
//                            selectedLanguageIndex = index
//                            val languageCode = when (index) {
//                                1 -> LanguageManager.LANGUAGE_BENGALI
//                                2 -> LanguageManager.LANGUAGE_HINDI
//                                else -> LanguageManager.LANGUAGE_ENGLISH
//                            }
//                            languageState.updateLanguage(languageCode)
//                        }
//                    )
//                }
//                composable(ProfileScreens.SearchHistory()) {
//                    SearchHistorySection()
//                }
//                composable(ProfileScreens.HelpAndSupport()) {
//                    HelpAndSupportSection(
//                        onNavigateToPrivacyPolicy = {},
//                        onNavigateToFAQ = {},
//                        onNavigateToContact = {}
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun ExpandedSidebar(
//    selectedItem: String,
//    mainMenuItems: List<MenuItem>,
//    settingsMenuItems: List<MenuItem>,
//    isSettingsExpanded: Boolean,
//    onSettingsExpandedChange: (Boolean) -> Unit,
//    onItemSelected: (String) -> Unit,
//    onCollapse: () -> Unit
//) {
//    val context = LocalContext.current
//    var settingsFocused by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(280.dp)
//            .background(Color(0xFF11161F))
//            .padding(16.dp)
//    ) {
//        // CLOSE BUTTON
//        IconButton(
//            onClick = onCollapse,
//            modifier = Modifier
//                .size(56.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(Color(0xFF1A2029))
//        ) {
//            Icon(Icons.Default.Close, contentDescription = null, tint = Color.White)
//        }
//
//        Spacer(modifier = Modifier.height(30.dp))
//
//        // MAIN MENU ITEMS
//        mainMenuItems.forEach { item ->
//            SidebarMenuItem(
//                title = item.title,
//                icon = item.icon,
//                isSelected = selectedItem == item.route,
//                onClick = { onItemSelected(item.route) }
//            )
//            Spacer(modifier = Modifier.height(12.dp))
//        }
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // SETTINGS SECTION (Collapsible)
//        val rotationAngle by animateFloatAsState(
//            targetValue = if (isSettingsExpanded) 90f else 0f,
//            animationSpec = tween(200),
//            label = "settings_rotation"
//        )
//
//        // Settings Header
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(14.dp))
//                .background(
//                    when {
//                        settingsFocused -> Color(0xFF1A2029)
//                        else -> Color.Transparent
//                    }
//                )
//                .onFocusChanged { settingsFocused = it.isFocused }
//                .focusable()
//                .clickable { onSettingsExpandedChange(!isSettingsExpanded) }
//                .padding(14.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                Icons.Default.Settings,
//                contentDescription = "Settings",
//                tint = Color.White,
//                modifier = Modifier.rotate(rotationAngle)
//            )
//            Spacer(modifier = Modifier.width(14.dp))
//            Text(
//                text = "Settings",
//                color = Color.White,
//                fontSize = 16.sp,
//                fontWeight = if (settingsFocused) FontWeight.Bold else FontWeight.Normal
//            )
//        }
//
//        // Settings Sub-items (Collapsible)
//        if (isSettingsExpanded) {
//            Spacer(modifier = Modifier.height(8.dp))
//            settingsMenuItems.forEach { item ->
//                SidebarMenuItem(
//                    title = item.title,
//                    icon = item.icon,
//                    isSelected = selectedItem == item.route,
//                    onClick = { onItemSelected(item.route) },
//                    indent = true
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // LOGOUT BUTTON
//        SidebarMenuItem(
//            title = "Logout",
//            icon = Icons.Default.Logout,
//            isSelected = false,
//            onClick = {
//                UserSession.clearSession(context)
//                val intent = Intent(context, StartScreen::class.java).apply {
//                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                }
//                context.startActivity(intent)
//                (context as? android.app.Activity)?.finish()
//            }
//        )
//    }
//}
//
//@Composable
//fun CollapsedSidebar(
//    selectedItem: String,
//    mainMenuItems: List<MenuItem>,
//    settingsMenuItems: List<MenuItem>,
//    isSettingsExpanded: Boolean,
//    onItemSelected: (String) -> Unit,
//    onExpand: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(90.dp)
//            .background(Color(0xFF11161F))
//            .padding(vertical = 20.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // MENU BUTTON
//        IconButton(
//            onClick = onExpand,
//            modifier = Modifier
//                .size(56.dp)
//                .clip(RoundedCornerShape(14.dp))
//                .background(Color(0xFF1A2029))
//        ) {
//            Icon(Icons.Default.Menu, contentDescription = null, tint = Color.White)
//        }
//
//        Spacer(modifier = Modifier.height(30.dp))
//
//        // MAIN MENU ITEMS
//        mainMenuItems.forEach { item ->
//            CollapsedSidebarItem(
//                title = item.title,
//                icon = item.icon,
//                isSelected = selectedItem == item.route,
//                onClick = { onItemSelected(item.route) }
//            )
//            Spacer(modifier = Modifier.height(18.dp))
//        }
//
//        // SETTINGS ICON (Collapsed)
//        CollapsedSidebarItem(
//            title = "Settings",
//            icon = Icons.Default.Settings,
//            isSelected = false,
//            onClick = onExpand
//        )
//    }
//}
//
//@Composable
//fun SidebarMenuItem(
//    title: String,
//    icon: ImageVector,
//    isSelected: Boolean,
//    onClick: () -> Unit,
//    indent: Boolean = false
//) {
//    var isFocused by remember { mutableStateOf(false) }
//
//    Row(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clip(RoundedCornerShape(14.dp))
//            .background(
//                when {
//                    isSelected -> Color(0xFFE50914)
//                    isFocused -> Color(0xFF1A2029)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { isFocused = it.isFocused }
//            .focusable()
//            .padding(start = if (indent) 42.dp else 14.dp, top = 12.dp, bottom = 12.dp, end = 14.dp)
//            .clickable { onClick() },
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(icon, contentDescription = title, tint = if (isSelected) Color.White else Color.White)
//        Spacer(modifier = Modifier.width(14.dp))
//        Text(
//            title,
//            color = if (isSelected) Color.White else Color.White,
//            fontSize = 14.sp,
//            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//fun CollapsedSidebarItem(title: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit) {
//    var isFocused by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .size(56.dp)
//            .clip(RoundedCornerShape(14.dp))
//            .background(
//                when {
//                    isSelected -> Color(0xFFE50914)
//                    isFocused -> Color(0xFF1A2029)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { isFocused = it.isFocused }
//            .focusable()
//            .padding(12.dp)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            icon, contentDescription = title,
//            tint = if (isSelected || isFocused) Color.White else Color(0xFF9CA3AF),
//            modifier = Modifier.size(26.dp)
//        )
//    }
//}
package com.bacbpl.iptv.ui.activities.sidebarhome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Devices
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.data.entities.Movie
import com.bacbpl.iptv.jetStram.data.entities.OttWidgetItem
import com.bacbpl.iptv.ui.activities.StartScreen
import com.bacbpl.iptv.jetStram.presentation.screens.profile.*
import com.bacbpl.iptv.jetStram.presentation.screens.Device.DeviceSection
import com.bacbpl.iptv.jetStram.presentation.screens.home.HomeScreen
import com.bacbpl.iptv.jetStram.presentation.screens.home.TvChannelViewModel
import com.bacbpl.iptv.jetStram.presentation.screens.movies.MovieDetailsScreen
import com.bacbpl.iptv.jetStram.presentation.screens.player.TvPlayer
import com.bacbpl.iptv.jetStram.presentation.screens.profile.ProfileScreens
import com.bacbpl.iptv.jetStram.presentation.screens.shows.ShowsAutoScreen
import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
import com.bacbpl.iptv.utils.LanguageManager
import com.bacbpl.iptv.utils.UserSession
import com.bacbpl.iptv.utils.rememberLanguageState
import dagger.hilt.android.AndroidEntryPoint
import androidx.hilt.navigation.compose.hiltViewModel
import android.widget.Toast
import com.bacbpl.iptv.jetStram.presentation.screens.shows.GenreScreen
import com.bacbpl.iptv.jetStram.presentation.screens.shows.Ott

@AndroidEntryPoint
class SideBarHome : ComponentActivity() {

    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize back pressed callback
        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Show exit dialog instead of closing
                showExitDialog()
            }
        }

        // Add the callback to the activity
        onBackPressedDispatcher.addCallback(this, backPressedCallback)

        setContent {
            JetStreamTheme {
                TVHomeScreen()
            }
        }
    }

    private fun showExitDialog() {
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit App")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("Yes") { _, _ ->
                finishAffinity() // Close the app
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}

// Data class for menu items
data class MenuItem(
    val title: String,
    val icon: ImageVector,
    val route: String,
    val isSettingsItem: Boolean = false
)

// Define routes for navigation
object SideBarScreens {
    const val LIVE = "live"

    const val OTT = "OTT"
    const val GENRE = "GENRE"

    const val ACCOUNTS = "accounts"
    const val WALLET = "wallet"
    const val DEVICE = "device"
//    const val ABOUT = "about"
    const val LANGUAGE = "language"
    const val HELP_SUPPORT = "help_support"

    fun MovieDetails(movieId: String) = "movie_details/$movieId"

    fun getMovieIdFromRoute(route: String): String {
        return route.substringAfterLast("/")
    }
}

@Composable
fun TVHomeScreen() {
    val navController = rememberNavController()
    var isSidebarExpanded by remember { mutableStateOf(true) }
    var selectedMenu by remember { mutableStateOf(SideBarScreens.LIVE) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Language state
    val languageState = rememberLanguageState()
    var selectedLanguageIndex by remember {
        mutableIntStateOf(
            when (languageState.currentLanguage) {
                LanguageManager.LANGUAGE_BENGALI -> 1
                LanguageManager.LANGUAGE_HINDI -> 2
                else -> 0
            }
        )
    }

    // Define menu items - Live as main menu
    val mainMenuItems = listOf(
        MenuItem("Live", Icons.Default.Tv, SideBarScreens.LIVE),
        MenuItem("OTT", Icons.Default.Movie, SideBarScreens.OTT),
        MenuItem("GENRE", Icons.Default.Category, SideBarScreens.GENRE),

        )

    // Settings Menu Items - Help & Support at the bottom
    val settingsMenuItems = listOf(
        MenuItem("Accounts", Icons.Default.Person, SideBarScreens.ACCOUNTS),
        MenuItem("Payment", Icons.Outlined.AccountBalanceWallet, SideBarScreens.WALLET),
        MenuItem("Device Info", Icons.Outlined.Devices, SideBarScreens.DEVICE),
//        MenuItem("About", Icons.Default.Info, SideBarScreens.ABOUT),
        MenuItem("Language", Icons.Default.Translate, SideBarScreens.LANGUAGE)
    )

    // Help & Support as separate bottom item
    val helpSupportItem = MenuItem("Help & Support", Icons.Default.Support, SideBarScreens.HELP_SUPPORT)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E14))
    ) {
        // SIDEBAR - Slightly smaller width
        AnimatedContent(targetState = isSidebarExpanded, label = "sidebar_animation") { expanded ->
            if (expanded) {
                ExpandedSidebar(
                    selectedItem = selectedMenu,
                    mainMenuItems = mainMenuItems,
                    settingsMenuItems = settingsMenuItems,
                    helpSupportItem = helpSupportItem,
                    isSettingsExpanded = isSettingsExpanded,
                    onSettingsExpandedChange = { isSettingsExpanded = it },
                    onItemSelected = { route ->
                        selectedMenu = route
                        navController.navigate(route) {
                            popUpTo(SideBarScreens.LIVE) { inclusive = false }
                        }
                    },
                    onCollapse = { isSidebarExpanded = false },
                    context = context
                )
            } else {
                CollapsedSidebar(
                    selectedItem = selectedMenu,
                    mainMenuItems = mainMenuItems,
                    settingsMenuItems = settingsMenuItems,
                    helpSupportItem = helpSupportItem,
                    isSettingsExpanded = isSettingsExpanded,
                    onItemSelected = { route ->
                        selectedMenu = route
                        navController.navigate(route) {
                            popUpTo(SideBarScreens.LIVE) { inclusive = false }
                        }
                    },
                    onExpand = { isSidebarExpanded = true },
                    context = context
                )
            }
        }

        // MAIN CONTENT
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF0A0E14), Color(0xFF11161F))
                    )
                )
                .padding(20.dp)
        ) {
            val currentTitle = when (selectedMenu) {
                SideBarScreens.LIVE -> "Live TV"
                SideBarScreens.OTT -> "OTT"
                SideBarScreens.GENRE -> "GENRE"
                SideBarScreens.ACCOUNTS -> "Accounts"
                SideBarScreens.WALLET -> "Payment"
                SideBarScreens.DEVICE -> "Device Info"
//                SideBarScreens.ABOUT -> "About"
                SideBarScreens.LANGUAGE -> "Language"
                SideBarScreens.HELP_SUPPORT -> "Help & Support"
                else -> "BACB IPTV"
            }

            Text(
                text = currentTitle,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(20.dp))

            SideBarNavHost(
                navController = navController,
                selectedLanguageIndex = selectedLanguageIndex,
                onLanguageChange = { index ->
                    selectedLanguageIndex = index
                    val languageCode = when (index) {
                        1 -> LanguageManager.LANGUAGE_BENGALI
                        2 -> LanguageManager.LANGUAGE_HINDI
                        else -> LanguageManager.LANGUAGE_ENGLISH
                    }
                    languageState.updateLanguage(languageCode)
                }
            )
        }
    }
}

@Composable
fun SideBarNavHost(
    navController: NavHostController,
    selectedLanguageIndex: Int,
    onLanguageChange: (Int) -> Unit
) {
    val context = LocalContext.current
    var showPrivacyPolicy by remember { mutableStateOf(false) }
    var showContactDialog by remember { mutableStateOf(false) }
    var showAboutDialog by remember { mutableStateOf(false) } // যোগ করুন

    // Show Privacy Policy Dialog when needed
    if (showPrivacyPolicy) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyPolicy = false }
        )
    }

    // Show Contact Us Dialog when needed
    if (showContactDialog) {
        ContactUsDialog(
            onDismiss = { showContactDialog = false },
            onSubmit = { name, email, subject, message ->
                android.util.Log.d("ContactUs", "Name: $name, Email: $email, Subject: $subject, Message: $message")
                showContactDialog = false
            }
        )
    }

    // Show About Dialog when needed
    if (showAboutDialog) {
        AboutDialog(
            onDismiss = { showAboutDialog = false }
        )
    }
    NavHost(
        navController = navController,
        startDestination = SideBarScreens.LIVE
    ) {
        // Live Screen
        composable(route = SideBarScreens.LIVE) {
            ShowsAutoScreen(
                onTVShowClick = { movie ->
                    navController.navigate(SideBarScreens.MovieDetails(movie.id))
                },
                onScroll = {},
                isTopBarVisible = false
            )
        }

        composable(route = SideBarScreens.OTT) {
            Ott(onNavigateToLeft = {})
        }

        composable(route = SideBarScreens.GENRE) {
            GenreScreen(navController = navController)
        }

        // Accounts Screen
        composable(route = SideBarScreens.ACCOUNTS) {
            AccountsSection(onNavigateToLeft = {})
        }

        // Wallet Screen
        composable(route = SideBarScreens.WALLET) {
            WalletSection(
                isSubtitlesChecked = true,
                onSubtitleCheckChange = {}
            )
        }

        // Device Screen
        composable(route = SideBarScreens.DEVICE) {
            DeviceSection(
                isSubtitlesChecked = true,
                onSubtitleCheckChange = {}
            )
        }

//        // About Screen
//        composable(route = SideBarScreens.ABOUT) {
//            AboutSection()
//        }

        // Language Screen
        composable(route = SideBarScreens.LANGUAGE) {
            LanguageSection(
                selectedIndex = selectedLanguageIndex,
                onSelectedIndexChange = onLanguageChange
            )
        }

        // Help & Support Screen
        composable(route = SideBarScreens.HELP_SUPPORT) {
            HelpAndSupportSection(
                onNavigateToPrivacyPolicy = { showPrivacyPolicy = true },
                onNavigateToFAQ = {
                    android.util.Log.d("SideBarHome", "Navigate to FAQ")
                },
                onNavigateToContact = { showContactDialog = true },
                onNavigateToAbout = { showAboutDialog = true } // ADD THIS
            )
        }

        // Movie Details Screen
        composable(
            route = "movie_details/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: return@composable
            MovieDetailsScreen(
                goToMoviePlayer = {
                    Toast.makeText(
                        context,
                        "Playing movie: $movieId",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                refreshScreenWithNewMovie = { movie ->
                    navController.navigate(SideBarScreens.MovieDetails(movie.id)) {
                        popUpTo("movie_details/$movieId") {
                            inclusive = true
                        }
                    }
                },
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }

        // FIX: Add Genre Player route with proper argument
    }
}
@Composable
fun ExpandedSidebar(
    selectedItem: String,
    mainMenuItems: List<MenuItem>,
    settingsMenuItems: List<MenuItem>,
    helpSupportItem: MenuItem,
    isSettingsExpanded: Boolean,
    onSettingsExpandedChange: (Boolean) -> Unit,
    onItemSelected: (String) -> Unit,
    onCollapse: () -> Unit,
    context: android.content.Context
) {
    var settingsFocused by remember { mutableStateOf(false) }
    var isCloseButtonFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(Color(0xFF11161F))
            .padding(12.dp)
    ) {
        // CLOSE BUTTON with red color on focus
        IconButton(
            onClick = onCollapse,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isCloseButtonFocused -> Color(0xFFE50914)
                        else -> Color(0xFF1A2029)
                    }
                )
                .onFocusChanged { focusState ->
                    isCloseButtonFocused = focusState.isFocused
                }
                .focusable()
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Close Sidebar",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MAIN MENU ITEMS
        mainMenuItems.forEach { item ->
            SidebarMenuItem(
                title = item.title,
                icon = item.icon,
                isSelected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        // SETTINGS SECTION (Collapsible)
        val rotationAngle by animateFloatAsState(
            targetValue = if (isSettingsExpanded) 90f else 0f,
            animationSpec = tween(200),
            label = "settings_rotation"
        )

        // Settings Header - Fixed to show red when focused
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        settingsFocused -> Color(0xFFE50914)
                        else -> Color.Transparent
                    }
                )
                .onFocusChanged { focusState ->
                    settingsFocused = focusState.isFocused
                }
                .focusable()
                .clickable { onSettingsExpandedChange(!isSettingsExpanded) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.rotate(rotationAngle).size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Settings",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = if (settingsFocused) FontWeight.Bold else FontWeight.Normal
            )
        }

        // Settings Sub-items (All menu items inside Settings except Help & Support)
        if (isSettingsExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            settingsMenuItems.forEach { item ->
                SidebarMenuItem(
                    title = item.title,
                    icon = item.icon,
                    isSelected = selectedItem == item.route,
                    onClick = { onItemSelected(item.route) },
                    indent = true
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Help & Support - Now at the bottom, above Logout
        SidebarMenuItem(
            title = helpSupportItem.title,
            icon = helpSupportItem.icon,
            isSelected = selectedItem == helpSupportItem.route,
            onClick = { onItemSelected(helpSupportItem.route) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // LOGOUT BUTTON
        SidebarMenuItem(
            title = "Logout",
            icon = Icons.Default.Logout,
            isSelected = false,
            onClick = {
                UserSession.clearSession(context)
                val intent = Intent(context, StartScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                (context as? android.app.Activity)?.finish()
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun CollapsedSidebar(
    selectedItem: String,
    mainMenuItems: List<MenuItem>,
    settingsMenuItems: List<MenuItem>,
    helpSupportItem: MenuItem,
    isSettingsExpanded: Boolean,
    onItemSelected: (String) -> Unit,
    onExpand: () -> Unit,
    context: android.content.Context
) {
    var isMenuButtonFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(72.dp)
            .background(Color(0xFF11161F))
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // MENU BUTTON with red on focus
        IconButton(
            onClick = onExpand,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    when {
                        isMenuButtonFocused -> Color(0xFFE50914)
                        else -> Color(0xFF1A2029)
                    }
                )
                .onFocusChanged { focusState ->
                    isMenuButtonFocused = focusState.isFocused
                }
                .focusable()
        ) {
            Icon(
                Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // MAIN MENU ITEMS
        mainMenuItems.forEach { item ->
            CollapsedSidebarItem(
                title = item.title,
                icon = item.icon,
                isSelected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // SETTINGS ICON (Collapsed)
        CollapsedSidebarItem(
            title = "Settings",
            icon = Icons.Default.Settings,
            isSelected = false,
            onClick = onExpand
        )

        Spacer(modifier = Modifier.weight(1f))

        // Help & Support Icon in collapsed state
        CollapsedSidebarItem(
            title = helpSupportItem.title,
            icon = helpSupportItem.icon,
            isSelected = selectedItem == helpSupportItem.route,
            onClick = { onItemSelected(helpSupportItem.route) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Logout Icon in collapsed state
        CollapsedSidebarItem(
            title = "Logout",
            icon = Icons.Default.Logout,
            isSelected = false,
            onClick = {
                UserSession.clearSession(context)
                val intent = Intent(context, StartScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                context.startActivity(intent)
                (context as? android.app.Activity)?.finish()
            }
        )
    }
}

@Composable
fun SidebarMenuItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    indent: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> Color(0xFFE50914)
                    isFocused -> Color(0xFFE50914).copy(alpha = 0.5f)
                    else -> Color.Transparent
                }
            )
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(start = if (indent) 36.dp else 12.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (isSelected || isFocused) Color.White else Color.White,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = if (isSelected || isFocused) Color.White else Color.White,
            fontSize = 13.sp,
            fontWeight = if (isSelected || isFocused) FontWeight.Medium else FontWeight.Normal
        )
    }
}

@Composable
fun CollapsedSidebarItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected || isFocused -> Color(0xFFE50914)
                    else -> Color.Transparent
                }
            )
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            }
            .focusable()
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (isSelected || isFocused) Color.White else Color(0xFF9CA3AF),
            modifier = Modifier.size(22.dp)
        )
    }
}