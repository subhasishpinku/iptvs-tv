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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
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

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HelpAndSupportSection(
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToFAQ: () -> Unit = {},
    onNavigateToContact: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {} // ADD THIS

) {
    // Get string resources
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
            title = "About Us",
            onClick = onNavigateToAbout
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

    // ফাংশনটি এখানে ডিফাইন করুন
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
        // Use Box to position the Surface to the right (same as ContactUsDialog)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 60.dp) // Leave space for sidebar
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.85f)  // Same width as ContactUsDialog
                    .fillMaxHeight(0.90f) // Same height as ContactUsDialog
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
                        color = Color.White, // Changed to White for consistency
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Divider(
                        color = Color.White.copy(alpha = 0.2f), // Changed to White
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
                                color = Color.White.copy(alpha = 0.9f), // Changed to White
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Buttons in Row (same as ContactUsDialog)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally)
                    ) {
                        // Back Button - Extra Small
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(100.dp)  // Fixed width
                                .height(32.dp), // Smaller height
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

                        // Accept Button - Extra Small
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier
                                .width(100.dp)  // Fixed width
                                .height(32.dp), // Smaller height
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

    // Get localized strings
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
                    // ================= LEFT SIDE (MAP + CONTACT INFO) =================
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

                        Text(
                            text = phone,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = emailText,
                            color = Color.White,
                            fontSize = 12.sp
                        )
                        Text(
                            text = website,
                            color = Color.White,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = companyName,
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.White
                        )

                        Text(
                            text = companyAddress,
                            color = Color.White,
                            fontSize = 11.sp
                        )

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
                                focusedContainerColor = Color(0xFFE50914), // Focused state same color
                                focusedContentColor = Color.White  // Focused text stays white
                            )
                        ) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = openInGoogleMaps,
                                color = Color.White,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        androidx.tv.material3.Card(
                            onClick = {
                                val url = "https://www.google.com/maps?q=22.5350378,88.3434308"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp),
                            shape = androidx.tv.material3.CardDefaults.shape(
                                shape = MaterialTheme.shapes.medium
                            ),
                            colors = androidx.tv.material3.CardDefaults.colors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            if (mapLoadingError || !webViewReady) {
                                Image(
                                    painter = painterResource(id = R.drawable.map),
                                    contentDescription = "Location Map",
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(6.dp),
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

                                                override fun onReceivedError(
                                                    view: WebView?,
                                                    errorCode: Int,
                                                    description: String?,
                                                    failingUrl: String?
                                                ) {
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
                                                        * {
                                                            margin: 0;
                                                            padding: 0;
                                                            box-sizing: border-box;
                                                        }
                                                        body {
                                                            margin: 0;
                                                            padding: 0;
                                                            height: 100%;
                                                            width: 100%;
                                                            overflow: hidden;
                                                            background-color: #f0f0f0;
                                                        }
                                                        .map-container {
                                                            height: 100%;
                                                            width: 100%;
                                                            position: relative;
                                                        }
                                                        iframe {
                                                            width: 100%;
                                                            height: 100%;
                                                            border: 0;
                                                        }
                                                    </style>
                                                </head>
                                                <body>
                                                    <div class="map-container">
                                                        <iframe
                                                            src="https://www.google.com/maps/embed/v1/place?key=$apiKey&q=$latitude,$longitude&zoom=16&maptype=roadmap"
                                                            allowfullscreen>
                                                        </iframe>
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

                    // ================= RIGHT SIDE (FORM) =================
                    Column(
                        modifier = Modifier
                            .weight(1.2f)
                            .padding(start = 12.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = sendUsMessage,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )

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
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(yourName, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(yourEmail, color = Color.White, fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = subject,
                            onValueChange = { subject = it },
                            label = { Text(subjectText, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = message,
                            onValueChange = { message = it },
                            label = { Text(messageText, color = Color.White, fontSize = 12.sp) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            maxLines = 4,
                            colors = textFieldColors,
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                        ) {
                            // Back Button
                            Button(
                                onClick = onDismiss,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFF333333),
                                    contentColor = Color.White,
                                    focusedContainerColor = Color(0xFF555555), // Lighter gray when focused
                                    focusedContentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = back,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }

                            // Send Message Button
                            Button(
                                onClick = {
                                    onSubmit(name, email, subject, message)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.colors(
                                    containerColor = Color(0xFFE50914),
                                    contentColor = Color.White,
                                    focusedContainerColor = Color(0xFFFF2020), // Slightly brighter red when focused
                                    focusedContentColor = Color.White
                                )
                            ) {
                                Text(
                                    text = sendMessage,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

