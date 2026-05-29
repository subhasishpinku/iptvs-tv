/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bacbpl.iptv.jetStram.presentation.screens.profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.surfaceColorAtElevation
import com.bacbpl.iptv.R
import com.bacbpl.iptv.jetStram.data.util.StringConstants
import com.bacbpl.iptv.jetStram.presentation.theme.JetStreamCardShape
import kotlinx.coroutines.delay
import java.net.NetworkInterface
import java.util.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HelpAndSupportSection(
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToFAQ: () -> Unit = {},
    onNavigateToContact: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    onNavigateToDeviceInfo: () -> Unit = {},
    onNavigateToLanguage: () -> Unit = {}  // ADD LANGUAGE PARAMETER
) {
    val helpAndSupportTitle = stringResource(id = StringConstants.Composable.Placeholders.HelpAndSupportSectionTitle)
    val privacyPolicyItem = stringResource(id = StringConstants.Composable.Placeholders.HelpAndSupportSectionPrivacyItem)
    val contactItem = stringResource(id = StringConstants.Composable.Placeholders.HelpAndSupportSectionContactItem)
    val contactValue = stringResource(id = StringConstants.Composable.Placeholders.HelpAndSupportSectionContactValue)

    Column(modifier = Modifier.padding(horizontal = 72.dp)) {
        Text(
            text = helpAndSupportTitle,
            style = MaterialTheme.typography.headlineSmall
        )

        HelpAndSupportSectionItem(
            title = privacyPolicyItem,
            onClick = onNavigateToPrivacyPolicy
        )

        HelpAndSupportSectionItem(
            title = contactItem,
            value = contactValue,
            onClick = onNavigateToContact
        )

        HelpAndSupportSectionItem(
            title = stringResource(R.string.help_about_us),  // Using string resource
            onClick = onNavigateToAbout
        )

        HelpAndSupportSectionItem(
            title = stringResource(R.string.help_device_info),  // Using string resource
            onClick = onNavigateToDeviceInfo
        )

        HelpAndSupportSectionItem(
            title = stringResource(R.string.help_language),  // Using string resource
            onClick = onNavigateToLanguage
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun HelpAndSupportSectionItem(
    title: String,
    value: String? = null,
    onClick: () -> Unit = {}
) {
    val iconDescription = stringResource(id = StringConstants.Composable.Placeholders.HelpAndSupportSectionListItemIconDescription)

    ListItem(
        modifier = Modifier.padding(top = 16.dp),
        selected = false,
        onClick = onClick,
        trailingContent = {
            value?.let { nnValue ->
                Text(
                    text = nnValue,
                    style = MaterialTheme.typography.titleMedium
                )
            } ?: run {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    modifier = Modifier.size(ListItemDefaults.IconSizeDense),
                    contentDescription = iconDescription
                )
            }
        },
        headlineContent = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )
        },
        colors = ListItemDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            focusedContentColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = ListItemDefaults.shape(shape = JetStreamCardShape)
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AboutDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    fun getVersionNumber(ctx: Context): String {
        return try {
            val packageName = ctx.packageName
            val packageInfo = ctx.packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    val versionNumber = remember(context) {
        getVersionNumber(context)
    }

    val aboutSectionTitle = stringResource(id = StringConstants.Composable.Placeholders.AboutSectionTitle)
    val aboutSectionDescription = stringResource(id = StringConstants.Composable.Placeholders.AboutSectionDescription)
    val aboutSectionAppVersionTitle = stringResource(id = StringConstants.Composable.Placeholders.AboutSectionAppVersionTitle)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.70f)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = aboutSectionTitle,
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Text(
                        modifier = Modifier.padding(bottom = 16.dp),
                        text = aboutSectionDescription,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f),
                        lineHeight = 22.sp
                    )

                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    Text(
                        modifier = Modifier.padding(top = 16.dp),
                        text = aboutSectionAppVersionTitle,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )

                    Text(
                        modifier = Modifier.padding(top = 8.dp),
                        text = versionNumber,
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFE50914)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Button(
                        onClick = onDismiss,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .width(120.dp)
                            .height(36.dp),
                        colors = ButtonDefaults.colors(
                            containerColor = Color(0xFFE50914),
                            contentColor = Color.White,
                            focusedContainerColor = Color(0xFFFF2020),
                            focusedContentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.back),
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.90f)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Text(
                        text = stringResource(R.string.privacy_policy),
                        style = MaterialTheme.typography.headlineLarge,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState())
                        ) {
                            Text(
                                text = stringResource(R.string.privacy_policy_content),
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(100.dp)
                                .height(32.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFF333333),
                                contentColor = Color.White,
                                focusedContainerColor = Color(0xFF555555),
                                focusedContentColor = Color.White
                            )
                        ) {
                            Text(
                                text = stringResource(R.string.back),
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(100.dp)
                                .height(32.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFFE50914),
                                contentColor = Color.White,
                                focusedContainerColor = Color(0xFFFF2020),
                                focusedContentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Accept",
                                color = Color.White,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun ContactUsDialog(
    onDismiss: () -> Unit,
    onSubmit: (name: String, email: String, subject: String, message: String) -> Unit = { _, _, _, _ -> }
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    val context = LocalContext.current
    var mapLoadingError by remember { mutableStateOf(false) }
    var webViewReady by remember { mutableStateOf(false) }

    val contactInfo = stringResource(R.string.contact_info)
    val phone = stringResource(R.string.contact_phone)
    val emailText = stringResource(R.string.contact_email_address)
    val website = stringResource(R.string.contact_website)
    val companyName = stringResource(R.string.company_name)
    val companyAddress = stringResource(R.string.company_address)
    val openInGoogleMaps = stringResource(R.string.open_in_google_maps)
    val sendUsMessage = stringResource(R.string.send_us_message)
    val yourName = stringResource(R.string.your_name)
    val yourEmail = stringResource(R.string.your_email)
    val subjectText = stringResource(R.string.subject)
    val messageText = stringResource(R.string.message)
    val sendMessage = stringResource(R.string.send_message)
    val back = stringResource(R.string.back)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.90f)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {
                    // LEFT SIDE (MAP + CONTACT INFO)
                    Column(
                        modifier = Modifier
                            .weight(0.8f)
                            .padding(end = 12.dp)
                    ) {
                        Text(
                            text = contactInfo,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = phone, color = Color.White, fontSize = 12.sp)
                        Text(text = emailText, color = Color.White, fontSize = 12.sp)
                        Text(text = website, color = Color.White, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(text = companyName, style = MaterialTheme.typography.titleSmall, color = Color.White)
                        Text(text = companyAddress, color = Color.White, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                val url = "https://www.google.com/maps?q=22.5350378,88.3434308"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFFE50914),
                                contentColor = Color.White,
                                focusedContainerColor = Color(0xFFE50914),
                                focusedContentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = openInGoogleMaps, color = Color.White, fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        androidx.tv.material3.Card(
                            onClick = {
                                val url = "https://www.google.com/maps?q=22.5350378,88.3434308"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier.fillMaxWidth().height(160.dp),
                            shape = androidx.tv.material3.CardDefaults.shape(shape = MaterialTheme.shapes.medium),
                            colors = androidx.tv.material3.CardDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            if (mapLoadingError || !webViewReady) {
                                Image(
                                    painter = painterResource(id = R.drawable.map),
                                    contentDescription = "Location Map",
                                    modifier = Modifier.fillMaxSize().padding(6.dp),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                AndroidView(
                                    factory = { ctx ->
                                        WebView(ctx).apply {
                                            settings.apply {
                                                javaScriptEnabled = true
                                                loadWithOverviewMode = true
                                                useWideViewPort = true
                                                setSupportZoom(true)
                                                builtInZoomControls = true
                                                displayZoomControls = false
                                                domStorageEnabled = true
                                            }
                                            webViewClient = object : WebViewClient() {
                                                override fun onPageFinished(view: WebView?, url: String?) {
                                                    super.onPageFinished(view, url)
                                                    webViewReady = true
                                                }
                                                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                                    super.onReceivedError(view, errorCode, description, failingUrl)
                                                    mapLoadingError = true
                                                    webViewReady = false
                                                }
                                            }
                                            val apiKey = "AIzaSyC-ppC-01qqiKhVO66MT6UM_b74a83zMe4"
                                            val latitude = 22.5350378
                                            val longitude = 88.3434308
                                            val htmlContent = """
                                                <!DOCTYPE html>
                                                <html>
                                                <head>
                                                    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=yes">
                                                    <style>
                                                        * { margin: 0; padding: 0; box-sizing: border-box; }
                                                        body { margin: 0; padding: 0; height: 100%; width: 100%; overflow: hidden; background-color: #f0f0f0; }
                                                        .map-container { height: 100%; width: 100%; position: relative; }
                                                        iframe { width: 100%; height: 100%; border: 0; }
                                                    </style>
                                                </head>
                                                <body>
                                                    <div class="map-container">
                                                        <iframe src="https://www.google.com/maps/embed/v1/place?key=$apiKey&q=$latitude,$longitude&zoom=16&maptype=roadmap" allowfullscreen></iframe>
                                                    </div>
                                                </body>
                                                </html>
                                            """.trimIndent()
                                            loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // RIGHT SIDE (FORM)
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .padding(start = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = sendUsMessage, style = MaterialTheme.typography.titleLarge, color = Color.White)
                        Spacer(modifier = Modifier.height(12.dp))

                        val textFieldColors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color.White,
                            unfocusedBorderColor = Color.Gray,
                            focusedLabelColor = Color.White,
                            unfocusedLabelColor = Color.LightGray,
                            cursorColor = Color.White
                        )

                        OutlinedTextField(
                            value = name, onValueChange = { name = it },
                            label = { Text(yourName, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email, onValueChange = { email = it },
                            label = { Text(yourEmail, color = Color.White, fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = subject, onValueChange = { subject = it },
                            label = { Text(subjectText, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = message, onValueChange = { message = it },
                            label = { Text(messageText, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            maxLines = 4,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                        ) {
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFF333333),
                                    contentColor = Color.White,
                                    focusedContainerColor = Color(0xFF555555),
                                    focusedContentColor = Color.White
                                )
                            ) {
                                Text(text = back, color = Color.White, fontSize = 12.sp)
                            }

                            Button(
                                onClick = { onSubmit(name, email, subject, message) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFFE50914),
                                    contentColor = Color.White,
                                    focusedContainerColor = Color(0xFFFF2020),
                                    focusedContentColor = Color.White
                                )
                            ) {
                                Text(text = sendMessage, color = Color.White, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==================== DEVICE INFO DIALOG ====================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DeviceInfoDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var deviceInfo by remember { mutableStateOf<List<DeviceInfoItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(500)
        deviceInfo = getDeviceInfo(context)
        isLoading = false
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.85f)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    // Header Section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Devices,
                            contentDescription = "Device Info",
                            tint = Color(0xFFE50914),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Device Information",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.width(80.dp).height(36.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFFE50914),
                                contentColor = Color.White,
                                focusedContainerColor = Color(0xFFFF2020),
                                focusedContentColor = Color.White
                            )
                        ) {
                            Text(text = "Close", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Divider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.padding(bottom = 16.dp))

                    if (isLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(modifier = Modifier.size(48.dp), color = Color(0xFFE50914))
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(text = "Gathering device information...", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.7f))
                            }
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 240.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(deviceInfo, key = { it.label }) { item ->
                                AnimatedDeviceCard(item)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AnimatedDeviceCard(
    item: DeviceInfoItem,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.02f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    )

    val baseContainerColor = when (item.category) {
        DeviceInfoCategory.IDENTIFIER -> Color(0xFF1A237E).copy(alpha = 0.8f)
        DeviceInfoCategory.NETWORK -> Color(0xFF1B5E20).copy(alpha = 0.8f)
        DeviceInfoCategory.HARDWARE -> Color(0xFFE65100).copy(alpha = 0.8f)
        DeviceInfoCategory.SOFTWARE -> Color(0xFF4A148C).copy(alpha = 0.8f)
        DeviceInfoCategory.SYSTEM -> Color(0xFF263238).copy(alpha = 0.8f)
    }

    val containerColor = if (isFocused) {
        when (item.category) {
            DeviceInfoCategory.IDENTIFIER -> Color(0xFF3949AB)
            DeviceInfoCategory.NETWORK -> Color(0xFF2E7D32)
            DeviceInfoCategory.HARDWARE -> Color(0xFFF57C00)
            DeviceInfoCategory.SOFTWARE -> Color(0xFF7B1FA2)
            DeviceInfoCategory.SYSTEM -> Color(0xFF37474F)
        }
    } else {
        baseContainerColor
    }

    Card(
        onClick = { },
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .onFocusChanged { focusState -> isFocused = focusState.isFocused },
        colors = CardDefaults.colors(containerColor = containerColor),
        shape = CardDefaults.shape(shape = RoundedCornerShape(12.dp))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(imageVector = item.category.icon, contentDescription = null, tint = if (isFocused) Color.White else Color.White.copy(alpha = 0.8f), modifier = Modifier.size(20.dp))
                Text(text = item.label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium, color = if (isFocused) Color.White else Color.White.copy(alpha = 0.8f), maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Text(text = item.value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.White, maxLines = 2, overflow = TextOverflow.Ellipsis)
        }
    }
}

data class DeviceInfoItem(
    val label: String,
    val value: String,
    val category: DeviceInfoCategory = DeviceInfoCategory.getCategory(label)
)

enum class DeviceInfoCategory(
    val icon: ImageVector,
    val color: Color
) {
    IDENTIFIER(Icons.Default.Info, Color(0xFF2196F3)),
    NETWORK(Icons.Default.Wifi, Color(0xFF4CAF50)),
    HARDWARE(Icons.Default.Devices, Color(0xFFFF9800)),
    SOFTWARE(Icons.Default.Android, Color(0xFF9C27B0)),
    SYSTEM(Icons.Default.Settings, Color(0xFF607D8B));

    companion object {
        fun getCategory(label: String): DeviceInfoCategory {
            return when {
                label.contains("ID") || label.contains("Name") -> IDENTIFIER
                label.contains("MAC") -> NETWORK
                label.contains("Model") || label.contains("Manufacturer") -> HARDWARE
                label.contains("Version") || label.contains("API") -> SOFTWARE
                else -> SYSTEM
            }
        }
    }
}

private fun getDeviceInfo(context: Context): List<DeviceInfoItem> {
    return listOf(
        DeviceInfoItem("Device ID", getDeviceId(context)),
        DeviceInfoItem("MAC Address", getMacAddress(context)),
        DeviceInfoItem("Device Model", getDeviceModel()),
        DeviceInfoItem("Manufacturer", getManufacturer()),
        DeviceInfoItem("Android Version", getAndroidVersion()),
        DeviceInfoItem("API Level", getApiLevel().toString()),
        DeviceInfoItem("Device Name", getDeviceName())
    )
}

@SuppressLint("HardwareIds")
private fun getDeviceId(context: Context): String {
    return try {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "Not Available"
    } catch (e: Exception) { "Not Available" }
}

private fun getMacAddress(context: Context): String {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getMacAddressAndroid10Plus(context)
        } else {
            getMacAddressLegacy()
        }
    } catch (e: Exception) { "Not Available" }
}

@SuppressLint("HardwareIds")
private fun getMacAddressAndroid10Plus(context: Context): String {
    return try {
        if (hasLocationPermission(context)) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
            val wifiInfo = wifiManager?.connectionInfo
            val mac = wifiInfo?.macAddress
            if (!mac.isNullOrEmpty() && mac != "02:00:00:00:00:00" && mac != "02:00:00:00:00:00") {
                return mac.uppercase(Locale.getDefault())
            }
        }
        val networkInterfaces = NetworkInterface.getNetworkInterfaces()
        while (networkInterfaces.hasMoreElements()) {
            val networkInterface = networkInterfaces.nextElement()
            val mac = networkInterface.hardwareAddress
            if (mac != null && mac.isNotEmpty() && (networkInterface.name == "wlan0" || networkInterface.name == "eth0")) {
                return mac.joinToString(":") { "%02x".format(it) }.uppercase(Locale.getDefault())
            }
        }
        getPersistentUniqueId(context)
    } catch (e: Exception) { getPersistentUniqueId(context) }
}

private fun getMacAddressLegacy(): String {
    val networkInterfaces = NetworkInterface.getNetworkInterfaces()
    while (networkInterfaces.hasMoreElements()) {
        val networkInterface = networkInterfaces.nextElement()
        val mac = networkInterface.hardwareAddress
        if (mac != null && networkInterface.name == "wlan0") {
            return mac.joinToString(":") { "%02x".format(it) }.uppercase(Locale.getDefault())
        }
    }
    return "Not Available"
}

@SuppressLint("HardwareIds", "MissingPermission")
private fun getPersistentUniqueId(context: Context): String {
    return try {
        val sharedPref = context.getSharedPreferences("device_prefs", Context.MODE_PRIVATE)
        var uniqueId = sharedPref.getString("unique_device_id", null)
        if (uniqueId == null) {
            val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            val buildSerial = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try { Build.getSerial() } catch (e: SecurityException) { "serial_denied" }
            } else { Build.SERIAL }
            val combined = "$androidId-$buildSerial-${Build.MANUFACTURER}-${Build.MODEL}"
            uniqueId = combined.hashCode().toString(16).uppercase(Locale.getDefault())
            sharedPref.edit().putString("unique_device_id", uniqueId).apply()
        }
        "UID: $uniqueId"
    } catch (e: Exception) { "Not Available" }
}

private fun hasLocationPermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                context.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    } else { true }
}

private fun getDeviceModel(): String = Build.MODEL
private fun getManufacturer(): String = Build.MANUFACTURER
private fun getAndroidVersion(): String = "${Build.VERSION.RELEASE} (${Build.VERSION.CODENAME})"
private fun getApiLevel(): Int = Build.VERSION.SDK_INT
private fun getDeviceName(): String = Build.DEVICE


// ==================== LANGUAGE DIALOG ====================

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LanguageDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val languageItems = listOf(
        Triple("en", "English", Icons.Default.Language),
        Triple("bn", "বাংলা", Icons.Default.Language),
        Triple("hi", "हिंदी", Icons.Default.Language)
    )

    var selectedIndex by remember {
        mutableIntStateOf(
            when (currentLanguage) {
                "bn" -> 1
                "hi" -> 2
                else -> 0
            }
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp)
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .fillMaxHeight(0.60f)
                    .align(Alignment.CenterEnd)
                    .padding(end = 20.dp),
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Select Language",
                            style = MaterialTheme.typography.headlineLarge,
                            color = Color.White
                        )

                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(80.dp)
                                .height(36.dp),
                            colors = ButtonDefaults.colors(
                                containerColor = Color(0xFFE50914),
                                contentColor = Color.White,
                                focusedContainerColor = Color(0xFFFF2020),
                                focusedContentColor = Color.White
                            )
                        ) {
                            Text(text = "Close", color = Color.White, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Divider(
                        color = Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(languageItems.size) { index ->
                            val (code, name, icon) = languageItems[index]
                            var isFocused by remember { mutableStateOf(false) }
                            val isSelected = selectedIndex == index

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .onFocusChanged { isFocused = it.isFocused }
                                    .clickable {
                                        selectedIndex = index
                                        onLanguageSelected(code)
                                        onDismiss()
                                    }
                                    .then(
                                        if (isFocused) {
                                            Modifier.border(
                                                width = 2.dp,
                                                color = Color(0xFFE50914),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                        } else Modifier
                                    ),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) Color(0xFFE50914).copy(alpha = 0.2f) else Color(0xFF1A1A1A)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = icon,
                                            contentDescription = name,
                                            tint = if (isSelected || isFocused) Color(0xFFE50914) else Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )

                                        Text(
                                            text = name,
                                            color = if (isSelected || isFocused) Color(0xFFE50914) else Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = if (isSelected || isFocused) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(
                                            Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFFE50914),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}