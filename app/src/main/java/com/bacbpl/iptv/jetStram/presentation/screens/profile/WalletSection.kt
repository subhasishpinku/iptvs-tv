package com.bacbpl.iptv.jetStram.presentation.screens.profile

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bacbpl.iptv.R
import com.caverock.androidsvg.SVG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.concurrent.TimeUnit

@Composable
fun WalletSection(
    isSubtitlesChecked: Boolean,
    onSubtitleCheckChange: (isChecked: Boolean) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_image),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF101010).copy(alpha = 0.7f))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Connect Mobile with TV",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Card(
                    modifier = Modifier
                        .width(250.dp)
                        .height(450.dp)
                        .shadow(0.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Black.copy(alpha = 0.0f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ph_image),
                            contentDescription = "Mobile QR",
                            modifier = Modifier
                                .width(250.dp)
                                .height(450.dp)
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .width(550.dp)
                        .height(500.dp)
                        .shadow(20.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.Transparent
                    )
                ) {
                    PaymentScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun PaymentScreen() {
    var selectedTab by remember { mutableStateOf("QR Code") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2A1B1B),
                        Color(0xFF1A0F0F),
                        Color(0xFF0A0505)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 15.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚡ Payment",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            Row(
                modifier = Modifier
                    .background(Color(0xFF3A2A2A), RoundedCornerShape(30))
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                PaymentTab(
                    title = "📱 QR Code",
                    selected = selectedTab == "QR Code",
                    onSelected = {
                        selectedTab = "QR Code"
                    }
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (selectedTab) {
                "QR Code" -> QRCodePaymentContent()
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "🔒 Secure Payment",
                    color = Color.Green.copy(alpha = 0.7f),
                    fontSize = 11.sp
                )
                Text(
                    text = "Need help? help.bacbpl.com",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun PaymentTab(title: String, selected: Boolean, onSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .then(
                if (selected) {
                    Modifier.background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color.Red,
                                Color(0xFFFF6B6B)
                            )
                        ),
                        RoundedCornerShape(30)
                    )
                } else Modifier
            )
            .clip(RoundedCornerShape(30))
            .clickable { onSelected() }
            .padding(horizontal = 24.dp, vertical = 12.dp)
    ) {
        Text(
            text = title,
            color = if (selected) Color.White else Color.LightGray,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            fontSize = 14.sp
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.08f))
            .padding(horizontal = 16.dp)
            .onKeyEvent { keyEvent ->
                if (keyEvent.type == KeyEventType.KeyDown) {
                    when (keyEvent.key) {
                        Key.Enter, Key.NumPadEnter -> {
                            keyboardController?.hide()
                            focusManager.clearFocus()
                            true
                        }
                        Key.DirectionDown -> {
                            focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)
                            true
                        }
                        Key.DirectionUp -> {
                            focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Up)
                            true
                        }
                        else -> false
                    }
                } else false
            },
        textStyle = LocalTextStyle.current.copy(
            color = Color.White,
            fontSize = 16.sp
        ),
        cursorBrush = SolidColor(Color.Red),
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        keyboardActions = KeyboardActions(
            onDone = {
                keyboardController?.hide()
                focusManager.clearFocus()
            },
            onNext = {
                focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down)
            }
        ),
        visualTransformation = if (isPassword) {
            VisualTransformation { text ->
                androidx.compose.ui.text.input.TransformedText(
                    androidx.compose.ui.text.AnnotatedString("•".repeat(text.text.length)),
                    object : androidx.compose.ui.text.input.OffsetMapping {
                        override fun originalToTransformed(offset: Int): Int = offset
                        override fun transformedToOriginal(offset: Int): Int = offset
                    }
                )
            }
        } else {
            VisualTransformation.None
        },
        decorationBox = { innerTextField ->
            Box(
                contentAlignment = Alignment.CenterStart
            ) {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
                innerTextField()
            }
        }
    )
}

@Composable
fun QRCodePaymentContent() {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    // Fetch QR code from API with proper coroutine
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            isLoading = true
            try {
                println("=== Starting QR Code API Call ===")
                val result = withContext(Dispatchers.IO) {
                    fetchQRCodeFromApi()
                }
                println("API Result: ${result?.take(200)}")

                if (result != null) {
                    when {
                        result.trim().startsWith("<svg") || result.trim().startsWith("<?xml") -> {
                            println("SVG data received, converting to bitmap...")
                            qrBitmap = withContext(Dispatchers.Default) {
                                convertSvgToBitmap(result, 400, 400)
                            }
                            if (qrBitmap != null) {
                                println("SVG converted to bitmap successfully")
                                errorMessage = null
                            } else {
                                errorMessage = "Failed to convert SVG"
                            }
                        }
                        result.startsWith("http") -> {
                            println("QR Code URL: $result")
                            errorMessage = null
                        }
                        else -> {
                            errorMessage = "Invalid QR data format"
                        }
                    }
                } else {
                    errorMessage = "QR code not available"
                    println("QR code not available from API")
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to load QR code"
                println("Error loading QR: ${e.message}")
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    val hasLocalQr = try {
        R.drawable.qr_code
        true
    } catch (e: Exception) {
        false
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier
                .size(280.dp)
                .shadow(10.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                when {
                    isLoading -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CircularProgressIndicator(
                                color = Color(0xFFE50914),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Loading QR Code...",
                                color = Color.Gray,
                                fontSize = 12.sp
                            )
                        }
                    }
                    errorMessage != null && qrBitmap == null -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = "Error",
                                tint = Color.Red,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = errorMessage ?: "Failed to load QR",
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            if (hasLocalQr) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        isLoading = true
                                        errorMessage = null
                                        coroutineScope.launch {
                                            try {
                                                val result = withContext(Dispatchers.IO) {
                                                    fetchQRCodeFromApi()
                                                }
                                                if (result != null) {
                                                    if (result.trim().startsWith("<svg") || result.trim().startsWith("<?xml")) {
                                                        qrBitmap = withContext(Dispatchers.Default) {
                                                            convertSvgToBitmap(result, 400, 400)
                                                        }
                                                        if (qrBitmap != null) {
                                                            errorMessage = null
                                                        }
                                                    }
                                                }
                                            } catch (e: Exception) {
                                                errorMessage = e.message
                                            } finally {
                                                isLoading = false
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFE50914)
                                    )
                                ) {
                                    Text("Retry", color = Color.White)
                                }
                            }
                        }
                    }
                    qrBitmap != null -> {
                        Image(
                            bitmap = qrBitmap!!.asImageBitmap(),
                            contentDescription = "Payment QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    hasLocalQr -> {
                        Image(
                            painter = painterResource(id = R.drawable.qr_code),
                            contentDescription = "Payment QR Code",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> {
                        Text(
                            text = "QR CODE\nUNAVAILABLE",
                            color = Color.Black,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Scan QR Code to Pay",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Use any UPI app or Mobile Banking to scan",
            color = Color.LightGray,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Fixed Amount - No API call
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Amount:",
                    color = Color.LightGray,
                    fontSize = 14.sp
                )
                Text(
                    text = "₹199.99",
                    color = Color.Green,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                android.widget.Toast.makeText(
                    context,
                    "Payment processing...",
                    android.widget.Toast.LENGTH_SHORT
                ).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE50914)
            ),
            shape = RoundedCornerShape(26.dp)
        ) {
            Text(
                text = "Confirm Payment",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

// Function to convert SVG string to Bitmap
private fun convertSvgToBitmap(svgString: String, width: Int, height: Int): Bitmap? {
    return try {
        val cleanSvg = svgString.trim()
            .replace(Regex("^[\\uFEFF\\uFFFE\\u0000\\u0001\\u0002\\u0003]*"), "")

        val svg = SVG.getFromString(cleanSvg)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        canvas.drawColor(android.graphics.Color.WHITE)

        val svgWidth = svg.documentWidth
        val svgHeight = svg.documentHeight

        if (svgWidth > 0 && svgHeight > 0) {
            val scaleX = width.toFloat() / svgWidth
            val scaleY = height.toFloat() / svgHeight
            val scale = minOf(scaleX, scaleY)

            val dx = (width - svgWidth * scale) / 2
            val dy = (height - svgHeight * scale) / 2

            canvas.save()
            canvas.translate(dx, dy)
            canvas.scale(scale, scale)
            svg.renderToCanvas(canvas)
            canvas.restore()
        } else {
            svg.renderToCanvas(canvas)
        }

        bitmap
    } catch (e: Exception) {
        println("Error converting SVG to bitmap: ${e.message}")
        null
    }
}

// Function to fetch QR code from API - now with proper IO dispatcher
private suspend fun fetchQRCodeFromApi(): String? {
    return withContext(Dispatchers.IO) {
        try {
            println("=== Fetching QR Code from API ===")

            val client = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val request = Request.Builder()
                .url("https://iptv.yogayog.net/api/payment/qr")
                .addHeader("Accept", "image/svg+xml,image/*,*/*")
                .addHeader("User-Agent", "Android TV App")
                .get()
                .build()

            println("Request URL: ${request.url}")

            val response = client.newCall(request).execute()
            val responseCode = response.code
            println("Response Code: $responseCode")

            val responseBody = response.body?.string()

            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
                val contentType = response.header("Content-Type")
                println("Content-Type: $contentType")
                println("Response Body Preview: ${responseBody.take(500)}")

                when {
                    contentType?.contains("svg") == true || responseBody.trim().startsWith("<svg") -> {
                        println("✅ SVG format detected")
                        return@withContext responseBody
                    }
                    contentType?.contains("json") == true -> {
                        println("JSON format detected")
                        val json = JSONObject(responseBody)
                        val qrData = json.optString("qr_url", null)
                            ?: json.optString("qr_code", null)
                            ?: json.optString("data", null)
                            ?: json.optString("image", null)
                        return@withContext qrData
                    }
                    else -> {
                        println("❌ Unknown format")
                        return@withContext null
                    }
                }
            } else {
                println("❌ API call failed with code: $responseCode")
                return@withContext null
            }
        } catch (e: Exception) {
            println("❌ Exception in fetchQRCodeFromApi: ${e.message}")
            e.printStackTrace()
            return@withContext null
        }
    }
}