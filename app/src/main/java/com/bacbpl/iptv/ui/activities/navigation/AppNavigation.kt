package com.bacbpl.iptv.ui.activities.navigation

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.bacbpl.iptv.JetStreamActivity
import com.bacbpl.iptv.jetStram.presentation.screens.shows.GenreScreen
import com.bacbpl.iptv.ui.activities.*
import com.bacbpl.iptv.ui.activities.otpscreen.OTPActivity
import com.bacbpl.iptv.ui.activities.signupscreen.SignupActivity
import com.bacbpl.iptv.utils.LoginUtils
import com.bacbpl.iptv.utils.ToastUtils
import kotlinx.coroutines.delay

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var isLoggedIn by remember { mutableStateOf(false) }
    var isReady by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        isLoggedIn = LoginUtils.isUserLoggedIn(context)
        isReady = true
    }

    if (!isReady) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color(0xFFE50914),
                modifier = Modifier.size(48.dp)
            )
        }
        return
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate("video_splash") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("video_splash") {
            VideoSplashScreen(
                onSplashFinished = {
                    // Direct navigation without extra transition screen
                    if (isLoggedIn) {
                        navController.navigate("main") {
                            popUpTo("video_splash") { inclusive = true }
                        }
                    } else {
                        navController.navigate("signin") {
                            popUpTo("video_splash") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("signin") {
            SignInActivity(
                onNavigateToOTP = { phone, deviceId, macId, deviceName ->
                    navController.navigate("otp/$phone/$deviceId/$macId/$deviceName")
                },
                onNavigateToSignUp = {
                    navController.navigate("signup")
                },
                onNavigateToForgotPassword = {},
                onSkip = {
                    isLoggedIn = true
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = "otp/{phone}/{deviceId}/{macId}/{deviceName}",
            arguments = listOf(
                navArgument("phone") { type = NavType.StringType },
                navArgument("deviceId") { type = NavType.StringType },
                navArgument("macId") { type = NavType.StringType },
                navArgument("deviceName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            val deviceId = backStackEntry.arguments?.getString("deviceId") ?: ""
            val macId = backStackEntry.arguments?.getString("macId") ?: ""
            val deviceName = backStackEntry.arguments?.getString("deviceName") ?: ""

            OTPActivity(
                phoneNumber = phone,
                deviceId = deviceId,
                macId = macId,
                deviceName = deviceName,
                onBack = { navController.popBackStack() },
                onSubmit = {
                    isLoggedIn = true
                    navController.navigate("main") {
                        popUpTo("signin") { inclusive = true }
                    }
                },
                onResend = {}
            )
        }

        composable("signup") {
            SignupActivity(
                onNavigateToSignIn = {
                    navController.popBackStack()
                },
                onSignupSuccess = {
                    ToastUtils.showSafeToast(
                        context,
                        "Account created successfully! Please sign in"
                    )
                    navController.navigate("signin") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        // Add Genre Screen route
        composable("genre_screen") {
            GenreScreen(navController = navController)
        }

        // Add Genre Player Screen route with categoryId parameter

        composable("main") {
            JetStreamActivityLauncher()
        }
    }
}