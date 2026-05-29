package com.bacbpl.iptv.jetStram.presentation.screens.movies

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.OpenInBrowser
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bacbpl.iptv.R
import com.bacbpl.iptv.data.SharedPrefManager
import com.bacbpl.iptv.jetStram.data.util.StringConstants
import com.bacbpl.iptv.jetStram.data.entities.MovieDetails
import com.bacbpl.iptv.jetStram.presentation.screens.dashboard.rememberChildPadding
import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamButtonShape
import com.bacbpl.iptv.ui.activities.OTTplayDeepLinkActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MovieDetails(
    movieDetails: MovieDetails,
    goToMoviePlayer: () -> Unit,
    ottplayUrl: String? = null
) {
    val childPadding = rememberChildPadding()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var imageLoadingError by remember { mutableStateOf(false) }
    var isLoadingAuthUrl by remember { mutableStateOf(false) }

    val gradientColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(432.dp)
            .bringIntoViewRequester(bringIntoViewRequester)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (movieDetails.posterUri.isNotEmpty() && !imageLoadingError) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movieDetails.posterUri)
                        .crossfade(true)
                        .error(R.drawable.logoss)
                        .build(),
                    contentDescription = StringConstants
                        .Composable
                        .ContentDescription
                        .moviePoster(movieDetails.name).toString(),
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier.fillMaxSize(),
                    onError = { imageLoadingError = true }
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1a1a2e),
                                    Color(0xFF16213e),
                                    Color(0xFF0f0f1f)
                                )
                            )
                        )
                ) {
                    Text(
                        text = movieDetails.name,
                        color = Color.White.copy(alpha = 0.3f),
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, gradientColor),
                                startY = 600f
                            )
                        )
                        drawRect(
                            Brush.horizontalGradient(
                                colors = listOf(gradientColor, Color.Transparent),
                                endX = 1000f,
                                startX = 300f
                            )
                        )
                        drawRect(
                            Brush.linearGradient(
                                colors = listOf(gradientColor, Color.Transparent),
                                start = Offset(x = 500f, y = 500f),
                                end = Offset(x = 1000f, y = 0f)
                            )
                        )
                    }
            )
        }

        Column(modifier = Modifier.fillMaxWidth(0.55f)) {
            Spacer(modifier = Modifier.height(108.dp))
            Column(
                modifier = Modifier.padding(start = childPadding.start)
            ) {
                Text(
                    text = movieDetails.name,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    ),
                    maxLines = 1
                )

                Column(
                    modifier = Modifier.alpha(0.75f)
                ) {
                    Text(
                        text = movieDetails.description,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(top = 8.dp),
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.padding(top = 20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val texts = listOf(
                            movieDetails.pgRating,
                            movieDetails.releaseDate,
                            movieDetails.categories.joinToString(", "),
                            movieDetails.duration
                        )
                        texts.forEachIndexed { index, text ->
                            Text(
                                text = text,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                )
                            )
                            if (index != texts.lastIndex) {
                                Box(
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp)
                                        .size(4.dp)
                                        .background(Color.White.copy(alpha = 0.5f), shape = androidx.compose.foundation.shape.CircleShape)
                                )
                            }
                        }
                    }

                    Row(modifier = Modifier.padding(top = 32.dp)) {
                        Column(
                            modifier = Modifier
                                .padding(end = 32.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.director),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                            Text(
                                text = movieDetails.director,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                ),
                                maxLines = 3
                            )
                        }
                        Column(
                            modifier = Modifier
                                .padding(end = 32.dp)
                                .weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.screenplay),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                            Text(
                                text = movieDetails.screenplay,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                ),
                                maxLines = 3
                            )
                        }
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = stringResource(R.string.music),
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White.copy(alpha = 0.75f)
                                )
                            )
                            Text(
                                text = movieDetails.music,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Normal,
                                    color = Color.White
                                ),
                                maxLines = 3
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Watch Trailer Button
                    ActionButton(
                        icon = Icons.Outlined.PlayArrow,
                        text = stringResource(R.string.watch_trailer),
                        onClick = { goToMoviePlayer() },
                        modifier = Modifier
                            .weight(1f)
                            .onFocusChanged {
                                if (it.isFocused) {
                                    coroutineScope.launch { bringIntoViewRequester.bringIntoView() }
                                }
                            }
                    )

                    // Now Playing / OTTplay Button - অ্যাপ ফার্স্ট
                    ActionButton(
                        icon = Icons.Outlined.OpenInBrowser,
                        text = stringResource(R.string.now_playing),
                        onClick = {
                            coroutineScope.launch {
                                isLoadingAuthUrl = true
                                try {
                                    if (!ottplayUrl.isNullOrEmpty()) {
                                        val finalUrl = withContext(Dispatchers.IO) {
                                            getAuthorizedOttplayUrl(ottplayUrl, context) ?: ottplayUrl
                                        }
                                        // প্রথমে অ্যাপ খোলার চেষ্টা করুন
                                        val opened = openOttplayAppDirectly(context, finalUrl)
                                        if (!opened) {
                                            // অ্যাপ না খুললে WebView খুলুন
                                            openInWebView(context, finalUrl)
                                        }
                                    } else {
                                        showNoUrlMessage(context)
                                    }
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    showErrorMessage(context)
                                } finally {
                                    isLoadingAuthUrl = false
                                }
                            }
                        },
                        modifier = Modifier.weight(1f),
                        isLoading = isLoadingAuthUrl
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
        shape = ButtonDefaults.shape(shape = JetStreamButtonShape),
        enabled = !isLoading
    ) {
        if (isLoading) {
            androidx.compose.material3.CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
            )
        } else {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleSmall.copy(color = Color.White)
            )
        }
    }
}

// সরাসরি OTTplay অ্যাপ খোলার চেষ্টা করার ফাংশন
private fun openOttplayAppDirectly(context: Context, url: String): Boolean {
    try {
        // OTTplay অ্যাপের সম্ভাব্য প্যাকেজ নামগুলো
        val possiblePackages = listOf(
            "com.ht.ottplay",           // মূল প্যাকেজ
            "com.ottplay",               // বিকল্প
            "com.ottplay.app",           // বিকল্প
            "com.htlabs.ottplay"         // বিকল্প
        )

        // প্রথমে কোন অ্যাপ ইনস্টল আছে চেক করুন
        var installedPackage: String? = null
        for (pkg in possiblePackages) {
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                installedPackage = pkg
                println("Found OTTplay app: $pkg")
                break
            } catch (e: PackageManager.NameNotFoundException) {
                // অ্যাপ নেই, চালিয়ে যান
            }
        }

        if (installedPackage != null) {
            // বিভিন্ন Deep Link ফরম্যাট ট্রাই করুন
            val contentId = extractContentIdFromUrl(url)
            val deepLinkUris = listOf(
                contentId?.let { Uri.parse("ottplay://show/$it") },
                contentId?.let { Uri.parse("ottplay://details/$it") },
                contentId?.let { Uri.parse("ottplay://content/$it") },
                Uri.parse(url)  // ওয়েব URL ও ট্রাই করুন
            )

            for (deepLinkUri in deepLinkUris) {
                if (deepLinkUri == null) continue

                val intent = Intent(Intent.ACTION_VIEW, deepLinkUri).apply {
                    setPackage(installedPackage)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                }

                if (intent.resolveActivity(context.packageManager) != null) {
                    context.startActivity(intent)
                    println("✅ OTTplay app opened with: $deepLinkUri")
                    return true
                }
            }

            // Deep Link কাজ না করলে সরাসরি অ্যাপ খোলার চেষ্টা
            val packageIntent = context.packageManager.getLaunchIntentForPackage(installedPackage)
            if (packageIntent != null) {
                context.startActivity(packageIntent)
                println("✅ OTTplay app opened directly")
                return true
            }
        }

        println("❌ Could not open OTTplay app")
        return false

    } catch (e: Exception) {
        println("Error opening OTTplay app: ${e.message}")
        return false
    }
}

// WebView এ খোলার ফাংশন (ব্যাকআপ)
private fun openInWebView(context: Context, url: String) {
    try {
        val intent = Intent(context, OTTplayDeepLinkActivity::class.java).apply {
            putExtra("url", url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        println("Opening in WebView: $url")
    } catch (e: Exception) {
        println("Error opening WebView: ${e.message}")
        // WebView না খুললে ব্রাউজার খুলুন
        try {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(browserIntent)
        } catch (e2: Exception) {
            println("Browser also failed: ${e2.message}")
        }
    }
}

// URL থেকে Content ID বের করার ফাংশন
private fun extractContentIdFromUrl(url: String): String? {
    return try {
        val patterns = listOf(
            Regex("""/([a-f0-9]{12,})$"""),
            Regex("""/([a-zA-Z0-9_-]{10,})/?$""")
        )

        for (pattern in patterns) {
            val matchResult = pattern.find(url)
            matchResult?.groupValues?.get(1)?.let {
                if (it.length >= 8) return it
            }
        }

        val segments = url.trimEnd('/').split("/")
        val lastSegment = segments.lastOrNull()

        if (!lastSegment.isNullOrEmpty() && lastSegment.length >= 8) {
            lastSegment
        } else {
            null
        }
    } catch (e: Exception) {
        println("Error extracting content ID: ${e.message}")
        null
    }
}

// No URL থাকলে মেসেজ দেখানোর ফাংশন
private fun showNoUrlMessage(context: Context) {
    try {
        android.widget.Toast.makeText(
            context,
            "No content available",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        println("Error showing toast: ${e.message}")
    }
}

// Error মেসেজ দেখানোর ফাংশন
private fun showErrorMessage(context: Context) {
    try {
        android.widget.Toast.makeText(
            context,
            "Failed to load content",
            android.widget.Toast.LENGTH_SHORT
        ).show()
    } catch (e: Exception) {
        println("Error showing toast: ${e.message}")
    }
}

// Authorized URL পাওয়ার ফাংশন
private suspend fun getAuthorizedOttplayUrl(ottplayUrl: String, context: Context): String? {
    return try {
        val url = "https://iptv.yogayog.net/api/get-ottplay-url-authorized/"
        val sharedPrefManager = SharedPrefManager(context)
        val token = sharedPrefManager.getToken()

        if (token.isNullOrEmpty()) {
            println("ERROR: No authentication token found")
            return ottplayUrl
        }

        val client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val formBody = FormBody.Builder()
            .add("ottplay_url", ottplayUrl)
            .build()

        val request = Request.Builder()
            .url(url)
            .addHeader("Authorization", "Bearer $token")
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .addHeader("Accept", "application/json")
            .post(formBody)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string()

        if (response.isSuccessful && responseBody != null) {
            val jsonObject = JSONObject(responseBody)
            val success = jsonObject.optBoolean("success", false)
            val authorizedUrl = jsonObject.optString("url", null)

            if (success && !authorizedUrl.isNullOrEmpty() && authorizedUrl != "null") {
                println("Got authorized URL: $authorizedUrl")
                return authorizedUrl
            }
        }

        println("API failed, using original URL: $ottplayUrl")
        ottplayUrl

    } catch (e: Exception) {
        println("Exception: ${e.message}")
        ottplayUrl
    }
}