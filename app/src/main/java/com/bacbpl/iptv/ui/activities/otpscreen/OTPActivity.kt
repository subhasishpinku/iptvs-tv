package com.bacbpl.iptv.ui.activities.otpscreen

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bacbpl.iptv.R
import com.bacbpl.iptv.data.api.RetrofitClient
import com.bacbpl.iptv.utils.ToastUtils
import com.bacbpl.iptv.ui.activities.otpscreen.viewmodel.OtpUiState
import com.bacbpl.iptv.ui.activities.otpscreen.viewmodel.OtpViewModel
import com.bacbpl.iptv.ui.activities.otpscreen.viewmodel.OtpViewModelFactory
import com.bacbpl.iptv.ui.activities.otpscreen.viewmodel.VerifyOtpUiState
import com.bacbpl.iptv.utils.UserSession

@Composable
fun OTPActivity(
    phoneNumber: String,
    deviceId: String,
    macId: String,
    deviceName: String,
    onBack: () -> Unit,
    onSubmit: (String) -> Unit,
    onResend: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: OtpViewModel = viewModel(
        factory = OtpViewModelFactory(context.applicationContext as Application)
    )
    val otpLength = 4
    val otpValues = remember { mutableStateListOf("", "", "", "") }
    val focusRequesters = List(otpLength) { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val sendOtpState by viewModel.sendOtpState.collectAsState()
    val verifyOtpState by viewModel.verifyOtpState.collectAsState()
    val timerState by viewModel.timerState.collectAsState()

    var isSubmitting by remember { mutableStateOf(false) }
    var showBackDialog by remember { mutableStateOf(false) }

    // Focus states for buttons
    var verifyButtonFocused by remember { mutableStateOf(false) }
    var resendButtonFocused by remember { mutableStateOf(false) }
    var backButtonFocused by remember { mutableStateOf(false) }
    var dialogYesButtonFocused by remember { mutableStateOf(false) }
    var dialogNoButtonFocused by remember { mutableStateOf(false) }

    // Handle physical back button press
    BackHandler {
        showBackDialog = true
    }

    // Auto-send OTP when screen loads
    LaunchedEffect(Unit) {
        viewModel.sendOtp(phoneNumber, deviceId, macId, deviceName)
    }

    // Handle send OTP state
    LaunchedEffect(sendOtpState) {
        when (sendOtpState) {
            is OtpUiState.Success -> {
                val response = (sendOtpState as OtpUiState.Success).response
                ToastUtils.showSafeToast(context, "OTP: ${response.otp} (For testing)")
            }
            is OtpUiState.Error -> {
                ToastUtils.showSafeToast(context, (sendOtpState as OtpUiState.Error).message)
            }
            else -> {}
        }
    }

    // Handle verify OTP state
    LaunchedEffect(verifyOtpState) {
        when (verifyOtpState) {
            is VerifyOtpUiState.Success -> {
                isSubmitting = false
                val response = (verifyOtpState as VerifyOtpUiState.Success).response
                ToastUtils.showSafeToast(context, response!!.message)

                if (response.status) {
                    response.token?.let { token ->
                        RetrofitClient.setAuthToken(token)
                        println("=== Token set in RetrofitClient after OTP verification ===")
                        println("Token: ${token.take(20)}...")
                    }

                    UserSession.updateSession(context)

                    ToastUtils.showSafeToast(
                        context,
                        "Login Successful! Welcome ${response.user?.name ?: "User"}"
                    )
                    onSubmit(otpValues.joinToString(""))
                }
            }
            is VerifyOtpUiState.Error -> {
                isSubmitting = false
                ToastUtils.showSafeToast(context, (verifyOtpState as VerifyOtpUiState.Error).message)
                otpValues.forEachIndexed { index, _ ->
                    otpValues[index] = ""
                }
                focusRequesters[0].requestFocus()
            }
            is VerifyOtpUiState.Loading -> {
                isSubmitting = true
            }
            else -> {}
        }
    }

    // Back Button Dialog
    if (showBackDialog) {
        Dialog(
            onDismissRequest = { showBackDialog = false },
            properties = DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false
            )
        ) {
            Card(
                modifier = Modifier
                    .width(400.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Exit OTP Verification?",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Are you sure you want to go back?",
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // No Button
                        Button(
                            onClick = { showBackDialog = false },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .onFocusChanged { dialogNoButtonFocused = it.isFocused }
                                .then(
                                    if (dialogNoButtonFocused) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dialogNoButtonFocused) Color(0xFFE50914) else Color(0xFF333333)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "No",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = if (dialogNoButtonFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        }

                        // Yes Button
                        Button(
                            onClick = {
                                showBackDialog = false
                                onBack()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .onFocusChanged { dialogYesButtonFocused = it.isFocused }
                                .then(
                                    if (dialogYesButtonFocused) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (dialogYesButtonFocused) Color(0xFFE50914) else Color(0xFFE50914).copy(alpha = 0.7f)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = "Yes",
                                fontSize = 16.sp,
                                color = Color.White,
                                fontWeight = if (dialogYesButtonFocused) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        AsyncImage(
            model = R.drawable.bg_movies,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color.Black.copy(alpha = 0.7f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .width(500.dp)
                    .clip(RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A1A1A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                ) {
                    // Back Button with focus visual feedback
                    IconButton(
                        onClick = { showBackDialog = true },
                        modifier = Modifier
                            .size(56.dp)
                            .onFocusChanged { backButtonFocused = it.isFocused }
                            .then(
                                if (backButtonFocused) {
                                    Modifier.border(
                                        width = 2.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                } else {
                                    Modifier
                                }
                            ),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (backButtonFocused) Color(0xFFE50914).copy(alpha = 0.3f) else Color.Transparent
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "OTP Verification",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Enter the 4-digit OTP sent to",
                        fontSize = 18.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Text(
                        text = phoneNumber,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE50914)
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    if (sendOtpState is OtpUiState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .size(48.dp)
                                .align(Alignment.CenterHorizontally),
                            color = Color(0xFFE50914)
                        )
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            for (i in 0 until otpLength) {
                                OtpTextField(
                                    value = otpValues[i],
                                    onValueChange = { newValue ->
                                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                                            otpValues[i] = newValue
                                            if (newValue.isNotEmpty() && i < otpLength - 1) {
                                                focusRequesters[i + 1].requestFocus()
                                            } else if (newValue.isNotEmpty() && i == otpLength - 1) {
                                                // Auto-verify when last digit is entered
                                                val otp = (otpValues.toList().subList(0, otpLength - 1) + newValue).joinToString("")
                                                if (otp.length == otpLength && timerState.isRunning) {
                                                    viewModel.verifyOtp(phoneNumber, otp, deviceId, macId, deviceName)
                                                }
                                            }
                                        }
                                    },
                                    modifier = Modifier
                                        .size(80.dp)
                                        .focusRequester(focusRequesters[i])
                                        .padding(end = if (i < otpLength - 1) 16.dp else 0.dp),
                                    enabled = !isSubmitting
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Timer Display
                        if (timerState.isRunning) {
                            Text(
                                text = String.format("00:%02d", timerState.seconds),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }

                        if (isSubmitting) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(48.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = Color(0xFFE50914)
                            )
                        }

                        // Resend Button with focus visual feedback
                        if (!timerState.isRunning && !isSubmitting) {
                            TextButton(
                                onClick = {
                                    viewModel.sendOtp(phoneNumber, deviceId, macId, deviceName)
                                    otpValues.forEachIndexed { index, _ ->
                                        otpValues[index] = ""
                                    }
                                    focusRequesters[0].requestFocus()
                                    onResend()
                                },
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .onFocusChanged { resendButtonFocused = it.isFocused }
                                    .then(
                                        if (resendButtonFocused) {
                                            Modifier.border(
                                                width = 2.dp,
                                                color = Color.White,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                        } else {
                                            Modifier
                                        }
                                    ),
                                enabled = !isSubmitting
                            ) {
                                Text(
                                    text = "Resend OTP",
                                    fontSize = 22.sp,
                                    fontWeight = if (resendButtonFocused) FontWeight.Bold else FontWeight.Normal,
                                    color = Color(0xFFE50914)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Verify OTP Button
                        Button(
                            onClick = {
                                val otp = otpValues.joinToString("")
                                if (otp.length == otpLength && timerState.isRunning) {
                                    viewModel.verifyOtp(phoneNumber, otp, deviceId, macId, deviceName)
                                }
                            },
                            enabled = otpValues.all { it.isNotEmpty() } && !isSubmitting && timerState.isRunning,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .onFocusChanged { verifyButtonFocused = it.isFocused }
                                .then(
                                    if (verifyButtonFocused) {
                                        Modifier.border(
                                            width = 2.dp,
                                            color = Color.White,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                    } else {
                                        Modifier
                                    }
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (verifyButtonFocused) Color(0xFFFF3B24) else Color(0xFFE50914),
                                disabledContainerColor = Color(0xFFE50914).copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Verify OTP",
                                fontSize = 20.sp,
                                fontWeight = if (verifyButtonFocused) FontWeight.Bold else FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OtpTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier,
    enabled: Boolean
) {
    var isFocused by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { isFocused = it.isFocused }
            .then(
                if (isFocused) {
                    Modifier.border(
                        width = 2.dp,
                        color = Color(0xFFE50914),
                        shape = RoundedCornerShape(8.dp)
                    )
                } else {
                    Modifier
                }
            ),
        enabled = enabled,
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            textAlign = TextAlign.Center,
            fontSize = 24.sp,
            color = Color.White
        ),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFE50914),
            unfocusedBorderColor = Color.Gray,
            cursorColor = Color(0xFFE50914),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(8.dp)
    )
}