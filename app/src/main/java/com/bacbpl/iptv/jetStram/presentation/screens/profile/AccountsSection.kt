package com.bacbpl.iptv.jetStram.presentation.screens.profile

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bacbpl.iptv.ui.activities.StartScreen
import com.bacbpl.iptv.utils.UserSession
import com.bacbpl.iptv.jetStram.presentation.screens.dashboard.rememberChildPadding
import com.bacbpl.iptv.jetStram.presentation.viewmodel.ProfileViewModel
import com.bacbpl.iptv.jetStram.presentation.viewmodel.LogoutViewModel
import com.bacbpl.iptv.ui.activities.signupscreen.data.repository.Resource
import com.bacbpl.iptv.jetStram.presentation.viewmodel.DeleteAccountViewModel
import android.widget.Toast

val QrCode = Icons.Default.QrCodeScanner
val ConfirmationNumber = Icons.Default.ConfirmationNumber
val LocationOn = Icons.Default.LocationOn
val PersonOutline = Icons.Default.PersonOutline
val Logout = Icons.Default.ExitToApp
val Subscriptions = Icons.Default.Subscriptions
val Map = Icons.Default.Map

@Immutable
data class AccountsSectionData(
    val title: String,
    val value: String? = null,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val onClick: () -> Unit = {}
)

data class SubscriberInfo(
    val useAltLcoCode: String = "0",
    val phone: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val address: String = "",
    val partnerReferenceId: String = "",
    val zone: String = "",
    val serviceNumber: String = "",
    val stateCode: String = ""
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AccountsSection(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    logoutViewModel: LogoutViewModel = hiltViewModel(),
    deleteAccountViewModel: DeleteAccountViewModel = hiltViewModel(),
    onNavigateToLeft: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val isLoggedIn by UserSession.isLoggedIn.collectAsState()
    val userName by UserSession.userName.collectAsState()
    val userEmail by UserSession.userEmail.collectAsState()
    val userMobile by UserSession.userMobile.collectAsState()
    val deviceId by UserSession.deviceId.collectAsState()

    val childPadding = rememberChildPadding()
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showSubscriberInfo by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    var focusedItemIndex by remember { mutableIntStateOf(0) }

    val updateProfileState by profileViewModel.updateProfileState.collectAsState()
    val isUpdating by profileViewModel.isUpdating.collectAsState()
    val subscriberDetailsState by profileViewModel.subscriberDetailsState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Observe logout state
    val logoutState by logoutViewModel.logoutState.collectAsState()
    val isLoggingOut by logoutViewModel.isLoggingOut.collectAsState()

    var showDeleteOtpDialog by remember { mutableStateOf(false) }

    // Observe delete account states
    val sendOtpState by deleteAccountViewModel.sendOtpState.collectAsState()
    val isSendingOtp by deleteAccountViewModel.isSendingOtp.collectAsState()
    val deleteAccountState by deleteAccountViewModel.deleteAccountState.collectAsState()
    val isDeletingAccount by deleteAccountViewModel.isDeletingAccount.collectAsState()
    val generatedOtp by deleteAccountViewModel.generatedOtp.collectAsState()

    var deleteErrorMessage by remember { mutableStateOf<String?>(null) }

    // Handle logout response
    LaunchedEffect(logoutState) {
        when (logoutState) {
            is Resource.Success -> {
                val message = (logoutState as Resource.Success).data?.message ?: "Logged out successfully"
                snackbarHostState.showSnackbar(
                    message,
                    duration = SnackbarDuration.Short
                )
                val intent = Intent(context, StartScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                logoutViewModel.resetLogoutState()
            }
            is Resource.Error -> {
                val error = (logoutState as Resource.Error).message ?: "Logout failed"
                snackbarHostState.showSnackbar(
                    error,
                    duration = SnackbarDuration.Short
                )
                UserSession.clearSession(context)
                val intent = Intent(context, StartScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                logoutViewModel.resetLogoutState()
            }
            else -> {}
        }
    }

    LaunchedEffect(updateProfileState) {
        if (updateProfileState is Resource.Success) {
            snackbarHostState.showSnackbar(
                (updateProfileState as Resource.Success).data?.message ?: "Profile updated successfully",
                duration = SnackbarDuration.Short
            )
            profileViewModel.resetUpdateState()
            showSubscriberInfo = false
        } else if (updateProfileState is Resource.Error) {
            snackbarHostState.showSnackbar(
                (updateProfileState as Resource.Error).message ?: "Failed to update profile",
                duration = SnackbarDuration.Short
            )
            profileViewModel.resetUpdateState()
        }
    }

    var subscriberInfo by remember {
        mutableStateOf(
            SubscriberInfo(
                phone = userMobile?.replace("+91", "") ?: "",
                email = userEmail ?: "",
                firstName = userName?.split(" ")?.firstOrNull() ?: "",
                lastName = userName?.split(" ")?.drop(1)?.joinToString(" ") ?: ""
            )
        )
    }
    var apiCalled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!apiCalled && isLoggedIn && userMobile != null) {
            apiCalled = true
            val mobileNumber = userMobile!!
                .replace("+91", "")
                .replace(" ", "")
            if (mobileNumber.isNotEmpty()) {
                profileViewModel.getSubscriberDetails(mobileNumber, context)
            }
        }
    }

    LaunchedEffect(subscriberDetailsState) {
        when (subscriberDetailsState) {
            is Resource.Loading -> {
                // Do nothing - no loading indicator
            }
            is Resource.Success -> {
                val response = (subscriberDetailsState as Resource.Success).data
                response?.let { details ->
                    val ottplayData = details.ottplayDetails?.data
                    val subscriber = details.subscriber
                    subscriberInfo = subscriberInfo.copy(
                        useAltLcoCode = subscriber?.useAltLcoCode ?: "0",
                        phone = subscriber?.mobile ?: ottplayData?.phone ?: subscriberInfo.phone,
                        email = subscriber?.email ?: ottplayData?.email ?: subscriberInfo.email,
                        firstName = subscriber?.firstname ?: ottplayData?.name?.split(" ")?.firstOrNull() ?: subscriberInfo.firstName,
                        lastName = subscriber?.lastname ?: ottplayData?.name?.split(" ")?.drop(1)?.joinToString(" ") ?: subscriberInfo.lastName,
                        address = subscriber?.address ?: subscriberInfo.address,
                        zone = subscriber?.zone ?: ottplayData?.zone ?: subscriberInfo.zone,
                        serviceNumber = subscriber?.serviceNumber ?: ottplayData?.serviceNo ?: subscriberInfo.serviceNumber,
                        stateCode = subscriber?.stateCode ?: subscriberInfo.stateCode,
                        partnerReferenceId = ottplayData?.subCode ?: subscriberInfo.partnerReferenceId
                    )
                }
            }
            is Resource.Error -> {
                Toast.makeText(
                    context,
                    (subscriberDetailsState as Resource.Error).message ?: "Failed to load profile",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else -> {}
        }
    }

    LaunchedEffect(Unit) {
        UserSession.updateSession(context)
    }

    // Handle send OTP response
    LaunchedEffect(sendOtpState) {
        when (sendOtpState) {
            is Resource.Success -> {
                deleteErrorMessage = null
                Toast.makeText(context, "OTP sent successfully", Toast.LENGTH_SHORT).show()
            }
            is Resource.Error -> {
                deleteErrorMessage = (sendOtpState as Resource.Error).message
                Toast.makeText(context, deleteErrorMessage, Toast.LENGTH_SHORT).show()
                showDeleteOtpDialog = false
                deleteAccountViewModel.resetStates()
            }
            else -> {}
        }
    }

    // Handle delete account response
    LaunchedEffect(deleteAccountState) {
        when (deleteAccountState) {
            is Resource.Success -> {
                val message = (deleteAccountState as Resource.Success).data?.message ?: "Account deleted successfully"
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                deleteAccountViewModel.resetStates()
                showDeleteOtpDialog = false
                showDeleteDialog = false
                val intent = Intent(context, StartScreen::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
            }
            is Resource.Error -> {
                val error = (deleteAccountState as Resource.Error).message ?: "Failed to delete account"
                deleteErrorMessage = error
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
                deleteAccountViewModel.resetStates()
            }
            else -> {}
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Black
    ) { paddingValues ->
        if (!isLoggedIn) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Please login to view profile",
                    color = Color.White,
                    fontSize = 18.sp
                )
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black)
                        .padding(paddingValues)
                ) {
                    // Quick Stats Cards
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = childPadding.start, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        QuickStatCard(
                            title = "Partner Ref ID",
                            value = subscriberInfo.partnerReferenceId.ifEmpty { "Not Set" },
                            icon = QrCode,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = "Zone",
                            value = subscriberInfo.zone.ifEmpty { "Not Set" },
                            icon = LocationOn,
                            modifier = Modifier.weight(1f)
                        )
                        QuickStatCard(
                            title = "Service No",
                            value = subscriberInfo.serviceNumber.ifEmpty { "Not Set" },
                            icon = ConfirmationNumber,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    val accountsSectionListItems = remember(userName, userEmail, userMobile, subscriberInfo) {
                        listOf(
                            AccountsSectionData(
                                title = "Email",
                                value = userEmail ?: "Email not set",
                                icon = Icons.Default.Email
                            ),
                            AccountsSectionData(
                                title = "Mobile",
                                value = userMobile ?: "Mobile not set",
                                icon = Icons.Default.Phone
                            ),
                            AccountsSectionData(
                                title = "Address",
                                value = subscriberInfo.address.ifEmpty { "Not set" },
                                icon = Icons.Default.Home
                            ),
                            AccountsSectionData(
                                title = "Service Number",
                                value = subscriberInfo.serviceNumber.ifEmpty { "Not set" },
                                icon = ConfirmationNumber
                            ),
                            AccountsSectionData(
                                title = "State Code",
                                value = subscriberInfo.stateCode.ifEmpty { "Not set" },
                                icon = Map
                            ),
                            AccountsSectionData(
                                title = "Edit Profile",
                                icon = Icons.Default.Edit,
                                onClick = { showSubscriberInfo = true }
                            ),
                            AccountsSectionData(
                                title = "Change Password",
                                value = "Change",
                                icon = Icons.Default.Lock,
                                onClick = { }
                            ),
                            AccountsSectionData(
                                title = "View Subscriptions",
                                icon = Subscriptions,
                                onClick = { }
                            ),
                            AccountsSectionData(
                                title = "Log Out",
                                icon = Logout,
                                onClick = {
                                    if (!isLoggingOut) {
                                        val currentDeviceId = deviceId ?: UserSession.getDeviceId(context)
                                        if (currentDeviceId != null) {
                                            logoutViewModel.logout(currentDeviceId, context)
                                        } else {
                                            Log.e("AccountsSection", "Device ID is null")
                                            UserSession.clearSession(context)
                                            val intent = Intent(context, StartScreen::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            context.startActivity(intent)
                                        }
                                    }
                                }
                            ),
                            AccountsSectionData(
                                title = "Delete Account",
                                icon = Icons.Default.Delete,
                                onClick = { showDeleteDialog = true }
                            )
                        )
                    }

                    LazyVerticalGrid(
                        userScrollEnabled = true,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                            .padding(horizontal = childPadding.start)
                            .onPreviewKeyEvent { keyEvent ->
                                when {
                                    keyEvent.key == Key.Back && keyEvent.type == KeyEventType.KeyUp -> {
                                        onNavigateToLeft()
                                        return@onPreviewKeyEvent true
                                    }
                                    keyEvent.key == Key.DirectionLeft && keyEvent.type == KeyEventType.KeyUp -> {
                                        if (focusedItemIndex % 3 == 0) {
                                            onNavigateToLeft()
                                            return@onPreviewKeyEvent true
                                        }
                                    }
                                }
                                false
                            },
                        columns = GridCells.Fixed(3),
                    ) {
                        items(accountsSectionListItems.size) { index ->
                            AccountsSelectionItem(
                                modifier = Modifier
                                    .focusRequester(if (index == 0) focusRequester else FocusRequester())
                                    .padding(4.dp),
                                itemKey = index,
                                index = index,
                                accountsSectionData = accountsSectionListItems[index],
                                onFocusChanged = { isFocused ->
                                    if (isFocused) {
                                        focusedItemIndex = index
                                    }
                                }
                            )
                        }
                    }
                }

                // Request focus to first item when screen loads
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(100)
                    focusRequester.requestFocus()
                }
            }

            if (showSubscriberInfo) {
                SubscriberInfoDialog(
                    subscriberInfo = subscriberInfo,
                    isUpdating = isUpdating,
                    onDismiss = {
                        showSubscriberInfo = false
                        profileViewModel.resetUpdateState()
                    },
                    onSave = { updatedInfo ->
                        subscriberInfo = updatedInfo
                        profileViewModel.updateSubscriberInfo(updatedInfo, context)
                    }
                )
            }

            // Delete confirmation dialog
            if (showDeleteDialog) {
                AccountsSectionDeleteDialog(
                    showDialog = showDeleteDialog,
                    onDismissRequest = {
                        showDeleteDialog = false
                        deleteAccountViewModel.resetStates()
                    },
                    onConfirm = {
                        showDeleteDialog = false
                        val mobileNumber = userMobile?.replace("+91", "")?.replace(" ", "") ?: ""
                        val currentDeviceId = deviceId ?: UserSession.getDeviceId(context)
                        if (mobileNumber.isNotEmpty() && currentDeviceId != null) {
                            deleteAccountViewModel.sendDeleteOtp(mobileNumber, currentDeviceId, context)
                            showDeleteOtpDialog = true
                        } else {
                            Toast.makeText(context, "Unable to process delete request", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            // OTP verification dialog
            if (showDeleteOtpDialog) {
                DeleteAccountOtpDialog(
                    mobile = userMobile?.replace("+91", "") ?: "",
                    generatedOtp = generatedOtp,
                    isSendingOtp = isSendingOtp,
                    isDeleting = isDeletingAccount,
                    errorMessage = deleteErrorMessage,
                    onDismiss = {
                        showDeleteOtpDialog = false
                        deleteAccountViewModel.resetStates()
                        deleteErrorMessage = null
                    },
                    onSendOtp = {
                        deleteErrorMessage = null
                        val mobileNumber = userMobile?.replace("+91", "")?.replace(" ", "") ?: ""
                        val currentDeviceId = deviceId ?: UserSession.getDeviceId(context)
                        if (mobileNumber.isNotEmpty() && currentDeviceId != null) {
                            deleteAccountViewModel.sendDeleteOtp(mobileNumber, currentDeviceId, context)
                        }
                    },
                    onVerifyAndDelete = { otp ->
                        deleteErrorMessage = null
                        val mobileNumber = userMobile?.replace("+91", "")?.replace(" ", "") ?: ""
                        val currentDeviceId = deviceId ?: UserSession.getDeviceId(context)
                        if (mobileNumber.isNotEmpty() && currentDeviceId != null) {
                            deleteAccountViewModel.verifyOtpAndDelete(otp, currentDeviceId, mobileNumber, context)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(70.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A1A)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFE50914),
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = value,
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = title,
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun SubscriberInfoDialog(
    subscriberInfo: SubscriberInfo,
    isUpdating: Boolean = false,
    onDismiss: () -> Unit,
    onSave: (SubscriberInfo) -> Unit
) {
    var useAltLcoCode by remember { mutableStateOf(subscriberInfo.useAltLcoCode) }
    var phone by remember { mutableStateOf(subscriberInfo.phone) }
    var email by remember { mutableStateOf(subscriberInfo.email) }
    var firstName by remember { mutableStateOf(subscriberInfo.firstName) }
    var lastName by remember { mutableStateOf(subscriberInfo.lastName) }
    var address by remember { mutableStateOf(subscriberInfo.address) }
    var partnerReferenceId by remember { mutableStateOf(subscriberInfo.partnerReferenceId) }
    var zone by remember { mutableStateOf(subscriberInfo.zone) }
    var serviceNumber by remember { mutableStateOf(subscriberInfo.serviceNumber) }
    var stateCode by remember { mutableStateOf(subscriberInfo.stateCode) }

    AlertDialog(
        onDismissRequest = {
            if (!isUpdating) onDismiss()
        },
        title = {
            Text(
                text = "Edit Subscriber Information",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Use Alt LCO Code:",
                        color = Color.White,
                        fontSize = 13.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Row {
                        listOf("No (0)", "Yes (1)").forEachIndexed { idx, option ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .background(
                                        color = if ((idx == 0 && useAltLcoCode == "0") ||
                                            (idx == 1 && useAltLcoCode == "1"))
                                            Color(0xFFE50914)
                                        else
                                            Color(0xFF333333),
                                        shape = RoundedCornerShape(3.dp)
                                    )
                                    .clickable(enabled = !isUpdating) {
                                        useAltLcoCode = if (idx == 0) "0" else "1"
                                    }
                                    .padding(horizontal = 10.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = option,
                                    color = Color.White,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                SubscriberInfoField(
                    label = "Phone (10 digits)",
                    value = phone,
                    onValueChange = { if (it.length <= 10) phone = it },
                    isNumber = true,
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Address",
                    value = address,
                    onValueChange = { address = it },
                    isMultiline = true,
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Partner Reference ID",
                    value = partnerReferenceId,
                    onValueChange = { if (it.length <= 30) partnerReferenceId = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Zone",
                    value = zone,
                    onValueChange = { zone = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "Service Number",
                    value = serviceNumber,
                    onValueChange = { serviceNumber = it },
                    enabled = !isUpdating
                )

                SubscriberInfoField(
                    label = "State Code",
                    value = stateCode,
                    onValueChange = { stateCode = it },
                    enabled = !isUpdating
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSave(
                        SubscriberInfo(
                            useAltLcoCode = useAltLcoCode,
                            phone = phone,
                            email = email,
                            firstName = firstName,
                            lastName = lastName,
                            address = address,
                            partnerReferenceId = partnerReferenceId,
                            zone = zone,
                            serviceNumber = serviceNumber,
                            stateCode = stateCode
                        )
                    )
                },
                enabled = !isUpdating
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFFE50914)
                    )
                } else {
                    Text("Save", color = Color(0xFFE50914))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isUpdating
            ) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@Composable
fun SubscriberInfoField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isNumber: Boolean = false,
    isMultiline: Boolean = false,
    enabled: Boolean = true
) {
    var isFocused by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 3.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(if (isMultiline) 65.dp else 42.dp)
                .background(
                    color = if (isFocused) Color(0xFF333333) else Color(0xFF2A2A2A),
                    shape = RoundedCornerShape(3.dp)
                )
                .border(
                    width = 1.dp,
                    color = if (isFocused) Color(0xFFE50914) else Color.Transparent,
                    shape = RoundedCornerShape(3.dp)
                )
                .onFocusChanged { isFocused = it.isFocused }
                .focusable(enabled = enabled)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                textStyle = LocalTextStyle.current.copy(
                    color = Color.White,
                    fontSize = 13.sp
                ),
                keyboardOptions = if (isNumber) {
                    KeyboardOptions(keyboardType = KeyboardType.Number)
                } else {
                    KeyboardOptions.Default
                },
                maxLines = if (isMultiline) 3 else 1,
                singleLine = !isMultiline,
                enabled = enabled
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountsSelectionItem(
    modifier: Modifier = Modifier,
    itemKey: Int,
    index: Int,
    accountsSectionData: AccountsSectionData,
    onFocusChanged: (Boolean) -> Unit = {}
) {
    var isFocused by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .onFocusChanged {
                isFocused = it.isFocused
                onFocusChanged(it.isFocused)
            }
            .focusable(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFocused) Color(0xFF2A2A2A) else Color(0xFF1A1A1A),
        ),
        interactionSource = interactionSource,
        onClick = accountsSectionData.onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                accountsSectionData.icon?.let {
                    Icon(
                        imageVector = it,
                        contentDescription = accountsSectionData.title,
                        tint = if (isFocused) Color(0xFFE50914) else Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = accountsSectionData.title,
                        color = if (isFocused) Color(0xFFE50914) else Color.White,
                        fontSize = 14.sp,
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    accountsSectionData.value?.let {
                        Text(
                            text = it,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }

            if (accountsSectionData.title == "Change Password" ||
                accountsSectionData.title == "Edit Profile") {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Navigate",
                    tint = if (isFocused) Color(0xFFE50914) else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun AccountsSectionDeleteDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit
) {
    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text(
                    text = "Delete Account",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to delete your account?",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "This action cannot be undone. All your data will be permanently removed.",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = onConfirm,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFE50914)
                    )
                ) {
                    Text("Delete", color = Color(0xFFE50914), fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissRequest,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color.White
                    )
                ) {
                    Text("Cancel", color = Color.White, fontSize = 14.sp)
                }
            },
            containerColor = Color(0xFF1A1A1A),
            titleContentColor = Color.White,
            textContentColor = Color.White
        )
    }
}

@Composable
fun DeleteAccountOtpDialog(
    mobile: String,
    generatedOtp: String?,
    isSendingOtp: Boolean,
    isDeleting: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onSendOtp: () -> Unit,
    onVerifyAndDelete: (String) -> Unit
) {
    var otpValue by remember { mutableStateOf("") }
    var isOtpSent by remember { mutableStateOf(false) }
    var countdown by remember { mutableStateOf(0) }

    // Auto-send OTP when dialog opens
    LaunchedEffect(Unit) {
        if (!isOtpSent && !isSendingOtp) {
            onSendOtp()
            isOtpSent = true
            countdown = 60
        }
    }

    // Countdown timer
    LaunchedEffect(countdown) {
        if (countdown > 0) {
            kotlinx.coroutines.delay(1000)
            countdown--
        }
    }

    // Auto-fill OTP if generated
    LaunchedEffect(generatedOtp) {
        if (!generatedOtp.isNullOrEmpty() && otpValue.isEmpty()) {
            otpValue = generatedOtp
        }
    }

    AlertDialog(
        onDismissRequest = {
            if (!isDeleting) onDismiss()
        },
        title = {
            Text(
                text = "Verify Account Deletion",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = "An OTP has been sent to $mobile",
                    color = Color.White,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = otpValue,
                    onValueChange = { if (it.length <= 6) otpValue = it },
                    label = { Text("Enter OTP", color = Color.White.copy(alpha = 0.7f)) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFE50914),
                        unfocusedBorderColor = Color.Gray,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedLabelColor = Color(0xFFE50914),
                        unfocusedLabelColor = Color.White.copy(alpha = 0.7f)
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    enabled = !isDeleting
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage,
                        color = Color(0xFFE50914),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (countdown > 0) {
                        Text(
                            text = "Resend OTP in ${countdown}s",
                            color = Color.White.copy(alpha = 0.5f),
                            fontSize = 12.sp
                        )
                    } else {
                        TextButton(
                            onClick = {
                                if (!isSendingOtp) {
                                    onSendOtp()
                                    countdown = 60
                                }
                            },
                            enabled = !isSendingOtp && !isDeleting
                        ) {
                            if (isSendingOtp) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = Color(0xFFE50914)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }
                            Text(
                                text = "Resend OTP",
                                color = Color(0xFFE50914),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onVerifyAndDelete(otpValue) },
                enabled = otpValue.length == 6 && !isDeleting
            ) {
                if (isDeleting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Color(0xFFE50914)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Deleting...", color = Color(0xFFE50914))
                } else {
                    Text("Verify & Delete", color = Color(0xFFE50914))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isDeleting
            ) {
                Text("Cancel", color = Color.White)
            }
        },
        containerColor = Color(0xFF1A1A1A),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}