//
//package com.bacbpl.iptv.ui.activities.sidebarhome
//
//import android.content.Intent
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.OnBackPressedCallback
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
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import androidx.compose.ui.window.Dialog
//import androidx.compose.ui.window.DialogProperties
//import com.bacbpl.iptv.R
//import com.bacbpl.iptv.jetStram.data.entities.Movie
//import com.bacbpl.iptv.jetStram.data.entities.OttWidgetItem
//import com.bacbpl.iptv.ui.activities.StartScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.*
//import com.bacbpl.iptv.jetStram.presentation.screens.Device.DeviceSection
//import com.bacbpl.iptv.jetStram.presentation.screens.home.HomeScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.home.TvChannelViewModel
//import com.bacbpl.iptv.jetStram.presentation.screens.movies.MovieDetailsScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.player.TvPlayer
//import com.bacbpl.iptv.jetStram.presentation.screens.profile.ProfileScreens
//import com.bacbpl.iptv.jetStram.presentation.screens.shows.ShowsAutoScreen
//import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
//import com.bacbpl.iptv.utils.LanguageManager
//import com.bacbpl.iptv.utils.UserSession
//import com.bacbpl.iptv.utils.rememberLanguageState
//import dagger.hilt.android.AndroidEntryPoint
//import androidx.hilt.navigation.compose.hiltViewModel
//import android.widget.Toast
//import androidx.compose.ui.res.stringResource
//import com.bacbpl.iptv.jetStram.presentation.screens.shows.GenreScreen
//import com.bacbpl.iptv.jetStram.presentation.screens.shows.Ott
//
//@AndroidEntryPoint
//class SideBarHome : ComponentActivity() {
//
//    private lateinit var backPressedCallback: OnBackPressedCallback
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Initialize back pressed callback
//        backPressedCallback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                // Show exit dialog instead of closing
//                showExitDialog()
//            }
//        }
//
//        // Add the callback to the activity
//        onBackPressedDispatcher.addCallback(this, backPressedCallback)
//
//        setContent {
//            JetStreamTheme {
//                TVHomeScreen()
//            }
//        }
//    }
//
//    private fun showExitDialog() {
//        androidx.appcompat.app.AlertDialog.Builder(this)
//            .setTitle("Exit App")
//            .setMessage("Are you sure you want to exit?")
//            .setPositiveButton("Yes") { _, _ ->
//                finishAffinity() // Close the app
//            }
//            .setNegativeButton("No") { dialog, _ ->
//                dialog.dismiss()
//            }
//            .setCancelable(true)
//            .show()
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
//// Define routes for navigation
//object SideBarScreens {
//    const val LIVE = "live"
//    const val OTT = "OTT"
//    const val GENRE = "GENRE"
//    const val ACCOUNTS = "accounts"
//    const val WALLET = "wallet"
//    const val HELP_SUPPORT = "help_support"
//
//    // Screen Title Functions
//    @Composable
//    fun getTitle(screen: String): String {
//        return when (screen) {
//            LIVE -> stringResource(R.string.screen_live)
//            OTT -> stringResource(R.string.screen_ott)
//            GENRE -> stringResource(R.string.screen_genre)
//            ACCOUNTS -> stringResource(R.string.screen_accounts)
//            WALLET -> stringResource(R.string.screen_wallet)
//            HELP_SUPPORT -> stringResource(R.string.screen_help_support)
//            else -> "BACB IPTV"
//        }
//    }
//
//    fun MovieDetails(movieId: String) = "movie_details/$movieId"
//
//    fun getMovieIdFromRoute(route: String): String {
//        return route.substringAfterLast("/")
//    }
//}
//
//@Composable
//fun TVHomeScreen() {
//    val navController = rememberNavController()
//    var isSidebarExpanded by remember { mutableStateOf(true) }
//    var selectedMenu by remember { mutableStateOf(SideBarScreens.LIVE) }
//    var isSettingsExpanded by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    val activity = context as? androidx.activity.ComponentActivity
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
//        MenuItem("Live", Icons.Default.Tv, SideBarScreens.LIVE),
//        MenuItem("OTT", Icons.Default.Movie, SideBarScreens.OTT),
//        MenuItem("GENRE", Icons.Default.Category, SideBarScreens.GENRE),
//    )
//
//    val settingsMenuItems = listOf(
//        MenuItem("Accounts", Icons.Default.Person, SideBarScreens.ACCOUNTS),
//        MenuItem("Payment", Icons.Outlined.AccountBalanceWallet, SideBarScreens.WALLET),
//    )
//
//    val helpSupportItem = MenuItem("Help & Support", Icons.Default.Support, SideBarScreens.HELP_SUPPORT)
//
//    Row(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF0A0E14))
//    ) {
//        AnimatedContent(targetState = isSidebarExpanded, label = "sidebar_animation") { expanded ->
//            if (expanded) {
//                ExpandedSidebar(
//                    selectedItem = selectedMenu,
//                    mainMenuItems = mainMenuItems,
//                    settingsMenuItems = settingsMenuItems,
//                    helpSupportItem = helpSupportItem,
//                    isSettingsExpanded = isSettingsExpanded,
//                    onSettingsExpandedChange = { isSettingsExpanded = it },
//                    onItemSelected = { route ->
//                        selectedMenu = route
//                        navController.navigate(route) {
//                            popUpTo(SideBarScreens.LIVE) { inclusive = false }
//                        }
//                    },
//                    onCollapse = { isSidebarExpanded = false },
//                    context = context
//                )
//            } else {
//                CollapsedSidebar(
//                    selectedItem = selectedMenu,
//                    mainMenuItems = mainMenuItems,
//                    settingsMenuItems = settingsMenuItems,
//                    helpSupportItem = helpSupportItem,
//                    isSettingsExpanded = isSettingsExpanded,
//                    onItemSelected = { route ->
//                        selectedMenu = route
//                        navController.navigate(route) {
//                            popUpTo(SideBarScreens.LIVE) { inclusive = false }
//                        }
//                    },
//                    onExpand = { isSidebarExpanded = true },
//                    context = context
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
//                .padding(20.dp)
//        ) {
//            Text(
//                text = SideBarScreens.getTitle(selectedMenu),
//                color = Color.White,
//                fontSize = 28.sp,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(20.dp))
//
//            SideBarNavHost(
//                navController = navController,
//                selectedLanguageIndex = selectedLanguageIndex,
//                onLanguageChange = { index ->
//                    selectedLanguageIndex = index
//                    val languageCode = when (index) {
//                        1 -> LanguageManager.LANGUAGE_BENGALI
//                        2 -> LanguageManager.LANGUAGE_HINDI
//                        else -> LanguageManager.LANGUAGE_ENGLISH
//                    }
//                    languageState.updateLanguage(languageCode)
//                    // Restart activity to apply language changes
//                    activity?.recreate()
//                }
//            )
//        }
//    }
//}
//
//@Composable
//fun SideBarNavHost(
//    navController: NavHostController,
//    selectedLanguageIndex: Int,
//    onLanguageChange: (Int) -> Unit
//) {
//    val context = LocalContext.current
//    var showPrivacyPolicy by remember { mutableStateOf(false) }
//    var showContactDialog by remember { mutableStateOf(false) }
//    var showAboutDialog by remember { mutableStateOf(false) } // যোগ করুন
//    var showDeviceInfoDialog by remember { mutableStateOf(false) }
//// SideBarNavHost composable-এ Language Dialog স্টেট যোগ করুন
//    var showLanguageDialog by remember { mutableStateOf(false) }
//
//// Show Language Dialog when needed
//    if (showLanguageDialog) {
//        LanguageDialog(
//            currentLanguage = when (selectedLanguageIndex) {
//                1 -> "bn"
//                2 -> "hi"
//                else -> "en"
//            },
//            onLanguageSelected = { languageCode ->
//                val newIndex = when (languageCode) {
//                    "bn" -> 1
//                    "hi" -> 2
//                    else -> 0
//                }
//                onLanguageChange(newIndex)
//                // Close dialog first, then activity will recreate via onLanguageChange
//                showLanguageDialog = false
//            },
//            onDismiss = { showLanguageDialog = false }
//        )
//    }
//
//
//// Show Device Info Dialog when needed
//    if (showDeviceInfoDialog) {
//        DeviceInfoDialog(
//            onDismiss = { showDeviceInfoDialog = false }
//        )
//    }
//
//    // Show Privacy Policy Dialog when needed
//    if (showPrivacyPolicy) {
//        PrivacyPolicyDialog(
//            onDismiss = { showPrivacyPolicy = false }
//        )
//    }
//
//    // Show Contact Us Dialog when needed
//    if (showContactDialog) {
//        ContactUsDialog(
//            onDismiss = { showContactDialog = false },
//            onSubmit = { name, email, subject, message ->
//                android.util.Log.d("ContactUs", "Name: $name, Email: $email, Subject: $subject, Message: $message")
//                showContactDialog = false
//            }
//        )
//    }
//
//    // Show About Dialog when needed
//    if (showAboutDialog) {
//        AboutDialog(
//            onDismiss = { showAboutDialog = false }
//        )
//    }
//    NavHost(
//        navController = navController,
//        startDestination = SideBarScreens.LIVE
//    ) {
//        // Live Screen
//        composable(route = SideBarScreens.LIVE) {
//            ShowsAutoScreen(
//                onTVShowClick = { movie ->
//                    navController.navigate(SideBarScreens.MovieDetails(movie.id))
//                },
//                onScroll = {},
//                isTopBarVisible = false
//            )
//        }
//
//        composable(route = SideBarScreens.OTT) {
//            Ott(
//                navController = navController,  // Pass navController
//                onNavigateToLeft = {
//                    // Optional back navigation
//                }
//            )
//        }
//        composable(route = SideBarScreens.GENRE) {
//            GenreScreen(navController = navController)
//        }
//
//        // Accounts Screen
//        composable(route = SideBarScreens.ACCOUNTS) {
//            AccountsSection(onNavigateToLeft = {})
//        }
//
//        // Wallet Screen
//        composable(route = SideBarScreens.WALLET) {
//            WalletSection(
//                isSubtitlesChecked = true,
//                onSubtitleCheckChange = {}
//            )
//        }
//
//        // Device Screen
////        composable(route = SideBarScreens.DEVICE) {
////            DeviceSection(
////                isSubtitlesChecked = true,
////                onSubtitleCheckChange = {}
////            )
////        }
//
////        // About Screen
////        composable(route = SideBarScreens.ABOUT) {
////            AboutSection()
////        }
//
//        // Language Screen
////        composable(route = SideBarScreens.LANGUAGE) {
////            LanguageSection(
////                selectedIndex = selectedLanguageIndex,
////                onSelectedIndexChange = onLanguageChange
////            )
////        }
//
//        // Help & Support Screen
//        // Help & Support Screen - ল্যাঙ্গুয়েজ ডায়ালোগ যোগ করুন
//        composable(route = SideBarScreens.HELP_SUPPORT) {
//            HelpAndSupportSection(
//                onNavigateToPrivacyPolicy = { showPrivacyPolicy = true },
//                onNavigateToFAQ = {
//                    android.util.Log.d("SideBarHome", "Navigate to FAQ")
//                },
//                onNavigateToContact = { showContactDialog = true },
//                onNavigateToAbout = { showAboutDialog = true },
//                onNavigateToDeviceInfo = { showDeviceInfoDialog = true },
//                onNavigateToLanguage = { showLanguageDialog = true }  // ADD THIS
//            )
//        }
//
//        // Movie Details Screen
//        composable(
//            route = "movie_details/{movieId}",
//            arguments = listOf(
//                navArgument("movieId") {
//                    type = NavType.StringType
//                }
//            )
//        ) { backStackEntry ->
//            val movieId = backStackEntry.arguments?.getString("movieId") ?: return@composable
//            MovieDetailsScreen(
//                goToMoviePlayer = {
//                    Toast.makeText(
//                        context,
//                        "Playing movie: $movieId",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                },
//                refreshScreenWithNewMovie = { movie ->
//                    navController.navigate(SideBarScreens.MovieDetails(movie.id)) {
//                        popUpTo("movie_details/$movieId") {
//                            inclusive = true
//                        }
//                    }
//                },
//                onBackPressed = {
//                    navController.popBackStack()
//                }
//            )
//        }
//
//        // FIX: Add Genre Player route with proper argument
//    }
//}
//@Composable
//fun ExpandedSidebar(
//    selectedItem: String,
//    mainMenuItems: List<MenuItem>,
//    settingsMenuItems: List<MenuItem>,
//    helpSupportItem: MenuItem,
//    isSettingsExpanded: Boolean,
//    onSettingsExpandedChange: (Boolean) -> Unit,
//    onItemSelected: (String) -> Unit,
//    onCollapse: () -> Unit,
//    context: android.content.Context
//) {
//    var settingsFocused by remember { mutableStateOf(false) }
//    var isCloseButtonFocused by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(200.dp)
//            .background(Color(0xFF11161F))
//            .padding(12.dp)
//    ) {
//        // CLOSE BUTTON with red color on focus
//        IconButton(
//            onClick = onCollapse,
//            modifier = Modifier
//                .size(48.dp)
//                .clip(RoundedCornerShape(12.dp))
//                .background(
//                    when {
//                        isCloseButtonFocused -> Color(0xFFE50914)
//                        else -> Color(0xFF1A2029)
//                    }
//                )
//                .onFocusChanged { focusState ->
//                    isCloseButtonFocused = focusState.isFocused
//                }
//                .focusable()
//        ) {
//            Icon(
//                Icons.Default.Close,
//                contentDescription = "Close Sidebar",
//                tint = Color.White,
//                modifier = Modifier.size(22.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // MAIN MENU ITEMS
//        mainMenuItems.forEach { item ->
//            SidebarMenuItem(
//                title = item.title,
//                icon = item.icon,
//                isSelected = selectedItem == item.route,
//                onClick = { onItemSelected(item.route) }
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//        }
//
//        Spacer(modifier = Modifier.height(6.dp))
//
//        // SETTINGS SECTION (Collapsible)
//        val rotationAngle by animateFloatAsState(
//            targetValue = if (isSettingsExpanded) 90f else 0f,
//            animationSpec = tween(200),
//            label = "settings_rotation"
//        )
//
//        // Settings Header - Fixed to show red when focused
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .clip(RoundedCornerShape(12.dp))
//                .background(
//                    when {
//                        settingsFocused -> Color(0xFFE50914)
//                        else -> Color.Transparent
//                    }
//                )
//                .onFocusChanged { focusState ->
//                    settingsFocused = focusState.isFocused
//                }
//                .focusable()
//                .clickable { onSettingsExpandedChange(!isSettingsExpanded) }
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Icon(
//                Icons.Default.Settings,
//                contentDescription = "Settings",
//                tint = Color.White,
//                modifier = Modifier.rotate(rotationAngle).size(20.dp)
//            )
//            Spacer(modifier = Modifier.width(12.dp))
//            Text(
//                text = "Settings",
//                color = Color.White,
//                fontSize = 14.sp,
//                fontWeight = if (settingsFocused) FontWeight.Bold else FontWeight.Normal
//            )
//        }
//
//        // Settings Sub-items (All menu items inside Settings except Help & Support)
//        if (isSettingsExpanded) {
//            Spacer(modifier = Modifier.height(6.dp))
//            settingsMenuItems.forEach { item ->
//                SidebarMenuItem(
//                    title = item.title,
//                    icon = item.icon,
//                    isSelected = selectedItem == item.route,
//                    onClick = { onItemSelected(item.route) },
//                    indent = true
//                )
//                Spacer(modifier = Modifier.height(6.dp))
//            }
//        }
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // Help & Support - Now at the bottom, above Logout
//        SidebarMenuItem(
//            title = helpSupportItem.title,
//            icon = helpSupportItem.icon,
//            isSelected = selectedItem == helpSupportItem.route,
//            onClick = { onItemSelected(helpSupportItem.route) }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
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
//
//        Spacer(modifier = Modifier.height(8.dp))
//    }
//}
//
//@Composable
//fun CollapsedSidebar(
//    selectedItem: String,
//    mainMenuItems: List<MenuItem>,
//    settingsMenuItems: List<MenuItem>,
//    helpSupportItem: MenuItem,
//    isSettingsExpanded: Boolean,
//    onItemSelected: (String) -> Unit,
//    onExpand: () -> Unit,
//    context: android.content.Context
//) {
//    var isMenuButtonFocused by remember { mutableStateOf(false) }
//
//    Column(
//        modifier = Modifier
//            .fillMaxHeight()
//            .width(72.dp)
//            .background(Color(0xFF11161F))
//            .padding(vertical = 16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        // MENU BUTTON with red on focus
//        IconButton(
//            onClick = onExpand,
//            modifier = Modifier
//                .size(48.dp)
//                .clip(RoundedCornerShape(12.dp))
//                .background(
//                    when {
//                        isMenuButtonFocused -> Color(0xFFE50914)
//                        else -> Color(0xFF1A2029)
//                    }
//                )
//                .onFocusChanged { focusState ->
//                    isMenuButtonFocused = focusState.isFocused
//                }
//                .focusable()
//        ) {
//            Icon(
//                Icons.Default.Menu,
//                contentDescription = "Menu",
//                tint = Color.White,
//                modifier = Modifier.size(22.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // MAIN MENU ITEMS
//        mainMenuItems.forEach { item ->
//            CollapsedSidebarItem(
//                title = item.title,
//                icon = item.icon,
//                isSelected = selectedItem == item.route,
//                onClick = { onItemSelected(item.route) }
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//        }
//
//        // SETTINGS ICON (Collapsed)
//        CollapsedSidebarItem(
//            title = "Settings",
//            icon = Icons.Default.Settings,
//            isSelected = false,
//            onClick = onExpand
//        )
//
//        Spacer(modifier = Modifier.weight(1f))
//
//        // Help & Support Icon in collapsed state
//        CollapsedSidebarItem(
//            title = helpSupportItem.title,
//            icon = helpSupportItem.icon,
//            isSelected = selectedItem == helpSupportItem.route,
//            onClick = { onItemSelected(helpSupportItem.route) }
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // Logout Icon in collapsed state
//        CollapsedSidebarItem(
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
//            .clip(RoundedCornerShape(12.dp))
//            .background(
//                when {
//                    isSelected -> Color(0xFFE50914)
//                    isFocused -> Color(0xFFE50914).copy(alpha = 0.5f)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { isFocused = it.isFocused }
//            .focusable()
//            .clickable { onClick() }
//            .padding(start = if (indent) 36.dp else 12.dp, top = 10.dp, bottom = 10.dp, end = 12.dp),
//        verticalAlignment = Alignment.CenterVertically
//    ) {
//        Icon(
//            icon,
//            contentDescription = title,
//            tint = if (isSelected || isFocused) Color.White else Color.White,
//            modifier = Modifier.size(20.dp)
//        )
//        Spacer(modifier = Modifier.width(12.dp))
//        Text(
//            text = title,
//            color = if (isSelected || isFocused) Color.White else Color.White,
//            fontSize = 13.sp,
//            fontWeight = if (isSelected || isFocused) FontWeight.Medium else FontWeight.Normal
//        )
//    }
//}
//
//@Composable
//fun CollapsedSidebarItem(
//    title: String,
//    icon: ImageVector,
//    isSelected: Boolean,
//    onClick: () -> Unit
//) {
//    var isFocused by remember { mutableStateOf(false) }
//
//    Box(
//        modifier = Modifier
//            .size(48.dp)
//            .clip(RoundedCornerShape(12.dp))
//            .background(
//                when {
//                    isSelected || isFocused -> Color(0xFFE50914)
//                    else -> Color.Transparent
//                }
//            )
//            .onFocusChanged { focusState ->
//                isFocused = focusState.isFocused
//            }
//            .focusable()
//            .clickable { onClick() }
//            .padding(10.dp),
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(
//            icon,
//            contentDescription = title,
//            tint = if (isSelected || isFocused) Color.White else Color(0xFF9CA3AF),
//            modifier = Modifier.size(22.dp)
//        )
//    }
//}

package com.bacbpl.iptv.ui.activities.sidebarhome

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.presentation.screens.profile.*
import com.bacbpl.iptv.jetStram.presentation.screens.movies.MovieDetailsScreen
import com.bacbpl.iptv.jetStram.presentation.screens.shows.ShowsAutoScreen
import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamTheme
import com.bacbpl.iptv.utils.LanguageManager
import com.bacbpl.iptv.utils.UserSession
import com.bacbpl.iptv.utils.rememberLanguageState
import dagger.hilt.android.AndroidEntryPoint
import android.widget.Toast
import com.bacbpl.iptv.ui.activities.StartScreen
import com.bacbpl.iptv.jetStram.presentation.screens.shows.GenreScreen
import com.bacbpl.iptv.jetStram.presentation.screens.shows.Ott

@AndroidEntryPoint
class SideBarHome : ComponentActivity() {

    private lateinit var backPressedCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        backPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        }
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
                finishAffinity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .setCancelable(true)
            .show()
    }
}

// Data class for menu items - Using String Resource ID
data class MenuItem(
    @StringRes val titleResId: Int,
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
    const val HELP_SUPPORT = "help_support"

    @Composable
    fun getTitle(screen: String): String {
        return when (screen) {
            LIVE -> stringResource(R.string.screen_live)
            OTT -> stringResource(R.string.screen_ott)
            GENRE -> stringResource(R.string.screen_genre)
            ACCOUNTS -> stringResource(R.string.screen_accounts)
            WALLET -> stringResource(R.string.screen_wallet)
            HELP_SUPPORT -> stringResource(R.string.screen_help_support)
            else -> "BACB IPTV"
        }
    }

    fun MovieDetails(movieId: String) = "movie_details/$movieId"
    fun getMovieIdFromRoute(route: String): String = route.substringAfterLast("/")
}

@Composable
fun TVHomeScreen() {
    val navController = rememberNavController()
    var isSidebarExpanded by remember { mutableStateOf(true) }
    var selectedMenu by remember { mutableStateOf(SideBarScreens.LIVE) }
    var isSettingsExpanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val activity = context as? androidx.activity.ComponentActivity

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

    // Menu items with String Resources
    val mainMenuItems = listOf(
        MenuItem(R.string.menu_live, Icons.Default.Tv, SideBarScreens.LIVE),
        MenuItem(R.string.menu_ott, Icons.Default.Movie, SideBarScreens.OTT),
        MenuItem(R.string.menu_genre, Icons.Default.Category, SideBarScreens.GENRE),
    )

    val settingsMenuItems = listOf(
        MenuItem(R.string.menu_accounts, Icons.Default.Person, SideBarScreens.ACCOUNTS),
        MenuItem(R.string.menu_payment, Icons.Outlined.AccountBalanceWallet, SideBarScreens.WALLET),
    )

    val helpSupportItem = MenuItem(R.string.menu_help_support, Icons.Default.Support, SideBarScreens.HELP_SUPPORT)

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0E14))
    ) {
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
            Text(
                text = SideBarScreens.getTitle(selectedMenu),
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
                    activity?.recreate()
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
    var showAboutDialog by remember { mutableStateOf(false) }
    var showDeviceInfoDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    if (showLanguageDialog) {
        LanguageDialog(
            currentLanguage = when (selectedLanguageIndex) {
                1 -> "bn"
                2 -> "hi"
                else -> "en"
            },
            onLanguageSelected = { languageCode ->
                val newIndex = when (languageCode) {
                    "bn" -> 1
                    "hi" -> 2
                    else -> 0
                }
                onLanguageChange(newIndex)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showDeviceInfoDialog) {
        DeviceInfoDialog(onDismiss = { showDeviceInfoDialog = false })
    }

    if (showPrivacyPolicy) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyPolicy = false })
    }

    if (showContactDialog) {
        ContactUsDialog(
            onDismiss = { showContactDialog = false },
            onSubmit = { name, email, subject, message ->
                android.util.Log.d("ContactUs", "Name: $name, Email: $email, Subject: $subject, Message: $message")
                showContactDialog = false
            }
        )
    }

    if (showAboutDialog) {
        AboutDialog(onDismiss = { showAboutDialog = false })
    }

    NavHost(
        navController = navController,
        startDestination = SideBarScreens.LIVE
    ) {
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
            Ott(
                navController = navController,
                onNavigateToLeft = {}
            )
        }

        composable(route = SideBarScreens.GENRE) {
            GenreScreen(navController = navController)
        }

        composable(route = SideBarScreens.ACCOUNTS) {
            AccountsSection(onNavigateToLeft = {})
        }

        composable(route = SideBarScreens.WALLET) {
            WalletSection(
                isSubtitlesChecked = true,
                onSubtitleCheckChange = {}
            )
        }

        composable(route = SideBarScreens.HELP_SUPPORT) {
            HelpAndSupportSection(
                onNavigateToPrivacyPolicy = { showPrivacyPolicy = true },
                onNavigateToFAQ = {
                    android.util.Log.d("SideBarHome", "Navigate to FAQ")
                },
                onNavigateToContact = { showContactDialog = true },
                onNavigateToAbout = { showAboutDialog = true },
                onNavigateToDeviceInfo = { showDeviceInfoDialog = true },
                onNavigateToLanguage = { showLanguageDialog = true }
            )
        }

        composable(
            route = "movie_details/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.StringType })
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId") ?: return@composable
            MovieDetailsScreen(
                goToMoviePlayer = {
                    Toast.makeText(context, "Playing movie: $movieId", Toast.LENGTH_SHORT).show()
                },
                refreshScreenWithNewMovie = { movie ->
                    navController.navigate(SideBarScreens.MovieDetails(movie.id)) {
                        popUpTo("movie_details/$movieId") { inclusive = true }
                    }
                },
                onBackPressed = { navController.popBackStack() }
            )
        }
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
        IconButton(
            onClick = onCollapse,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isCloseButtonFocused) Color(0xFFE50914) else Color(0xFF1A2029))
                .onFocusChanged { isCloseButtonFocused = it.isFocused }
                .focusable()
        ) {
            Icon(Icons.Default.Close, contentDescription = "Close Sidebar", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        mainMenuItems.forEach { item ->
            SidebarMenuItem(
                titleResId = item.titleResId,
                icon = item.icon,
                isSelected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) }
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        Spacer(modifier = Modifier.height(6.dp))

        val rotationAngle by animateFloatAsState(
            targetValue = if (isSettingsExpanded) 90f else 0f,
            animationSpec = tween(200),
            label = "settings_rotation"
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (settingsFocused) Color(0xFFE50914) else Color.Transparent)
                .onFocusChanged { settingsFocused = it.isFocused }
                .focusable()
                .clickable { onSettingsExpandedChange(!isSettingsExpanded) }
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White, modifier = Modifier.rotate(rotationAngle).size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = stringResource(R.string.menu_settings), color = Color.White, fontSize = 14.sp, fontWeight = if (settingsFocused) FontWeight.Bold else FontWeight.Normal)
        }

        if (isSettingsExpanded) {
            Spacer(modifier = Modifier.height(6.dp))
            settingsMenuItems.forEach { item ->
                SidebarMenuItem(
                    titleResId = item.titleResId,
                    icon = item.icon,
                    isSelected = selectedItem == item.route,
                    onClick = { onItemSelected(item.route) },
                    indent = true
                )
                Spacer(modifier = Modifier.height(6.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        SidebarMenuItem(
            titleResId = helpSupportItem.titleResId,
            icon = helpSupportItem.icon,
            isSelected = selectedItem == helpSupportItem.route,
            onClick = { onItemSelected(helpSupportItem.route) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        SidebarMenuItem(
            titleResId = R.string.menu_logout,
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
        IconButton(
            onClick = onExpand,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (isMenuButtonFocused) Color(0xFFE50914) else Color(0xFF1A2029))
                .onFocusChanged { isMenuButtonFocused = it.isFocused }
                .focusable()
        ) {
            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        mainMenuItems.forEach { item ->
            CollapsedSidebarItem(
                titleResId = item.titleResId,
                icon = item.icon,
                isSelected = selectedItem == item.route,
                onClick = { onItemSelected(item.route) }
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        CollapsedSidebarItem(
            titleResId = R.string.menu_settings,
            icon = Icons.Default.Settings,
            isSelected = false,
            onClick = onExpand
        )

        Spacer(modifier = Modifier.weight(1f))

        CollapsedSidebarItem(
            titleResId = helpSupportItem.titleResId,
            icon = helpSupportItem.icon,
            isSelected = selectedItem == helpSupportItem.route,
            onClick = { onItemSelected(helpSupportItem.route) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        CollapsedSidebarItem(
            titleResId = R.string.menu_logout,
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
    @StringRes titleResId: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    indent: Boolean = false
) {
    var isFocused by remember { mutableStateOf(false) }
    val title = stringResource(titleResId)

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
        Icon(icon, contentDescription = title, tint = if (isSelected || isFocused) Color.White else Color.White, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = title, color = if (isSelected || isFocused) Color.White else Color.White, fontSize = 13.sp, fontWeight = if (isSelected || isFocused) FontWeight.Medium else FontWeight.Normal)
    }
}

@Composable
fun CollapsedSidebarItem(
    @StringRes titleResId: Int,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val title = stringResource(titleResId)

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected || isFocused) Color(0xFFE50914) else Color.Transparent)
            .onFocusChanged { isFocused = it.isFocused }
            .focusable()
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = title, tint = if (isSelected || isFocused) Color.White else Color(0xFF9CA3AF), modifier = Modifier.size(22.dp))
    }
}