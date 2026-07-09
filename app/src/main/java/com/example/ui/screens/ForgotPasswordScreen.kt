package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.validation.isValidEmail
import com.example.ui.validation.isStrongPassword

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(viewModel: MainViewModel) {
    var email by remember { mutableStateOf(viewModel.forgotPasswordEmail) }
    var otpInput by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        onDispose { }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Animated ambient gradient blobs
        val infiniteTransition = rememberInfiniteTransition(label = "fpBg")
        val blob1X by infiniteTransition.animateFloat(
            initialValue = 0f, targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse),
            label = "blob1"
        )
        val blob2X by infiniteTransition.animateFloat(
            initialValue = 1f, targetValue = 0f,
            animationSpec = infiniteRepeatable(tween(11000, easing = LinearEasing), RepeatMode.Reverse),
            label = "blob2"
        )
        val blob1OffsetX = 300.dp * (blob1X - 0.5f)
        val blob2OffsetX = 320.dp * (blob2X - 0.5f)

        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 90.dp + blob1OffsetX, y = (-90).dp)
                .blur(100.dp)
                .alpha(0.10f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(SecondaryGreen, AccentTeal, GradientNeonStart)
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(320.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-90).dp + blob2OffsetX, y = 90.dp)
                .blur(110.dp)
                .alpha(0.10f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryBlue, TertiaryViolet, GradientBloomEnd)
                    ),
                    CircleShape
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        viewModel.resetForgotPasswordState()
                        viewModel.navigateTo(AppScreen.Login)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                        ),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (!viewModel.isOtpSent) Icons.Default.Email
                    else if (!viewModel.isOtpVerified) Icons.Default.Pin
                    else Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Title
            Text(
                text = when {
                    !viewModel.isOtpSent -> "Forgot Password"
                    !viewModel.isOtpVerified -> "Verify OTP"
                    else -> "Reset Password"
                },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = when {
                    !viewModel.isOtpSent -> "Enter your email address and we'll send you a verification code."
                    !viewModel.isOtpVerified -> "Enter the 6-digit code sent to your email."
                    else -> "Create a new password for your account."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Error message
            viewModel.forgotPasswordError?.let { error ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.7f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = error,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Success message
            viewModel.forgotPasswordSuccess?.let { success ->
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = SecondaryGreen.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = success,
                            style = MaterialTheme.typography.bodySmall,
                            color = SecondaryGreen
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Step 1: Email input
            if (!viewModel.isOtpSent) {
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                    },
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = emailError != null,
                    supportingText = emailError?.let { error -> { Text(error) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Send OTP button
                Surface(
                    onClick = {
                        if (!isValidEmail(email)) {
                            emailError = "Please enter a valid email address"
                        } else {
                            viewModel.sendOtpToEmail(email.trim())
                        }
                    },
                    enabled = !viewModel.isSendingOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (viewModel.isSendingOtp) {
                                        listOf(
                                            PrimaryBlue.copy(alpha = 0.5f),
                                            TertiaryViolet.copy(alpha = 0.5f)
                                        )
                                    } else {
                                        listOf(GradientPrimaryStart, GradientPrimaryEnd)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.isSendingOtp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Sending OTP...",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Send Verification Code",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Step 2: OTP verification
            if (viewModel.isOtpSent && !viewModel.isOtpVerified) {
                OutlinedTextField(
                    value = otpInput,
                    onValueChange = {
                        if (it.length <= 6 && it.all { c -> c.isDigit() }) {
                            otpInput = it
                        }
                    },
                    label = { Text("Enter 6-digit OTP") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Pin,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Resend OTP
                TextButton(
                    onClick = { viewModel.sendOtpToEmail(email.trim()) },
                    enabled = !viewModel.isSendingOtp
                ) {
                    Text(
                        text = "Resend Code",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verify button
                Surface(
                    onClick = {
                        if (otpInput.length != 6) {
                            viewModel.forgotPasswordError = "Please enter the 6-digit code"
                        } else {
                            viewModel.verifyOtp(otpInput)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Verify Code",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            // Step 3: New password
            if (viewModel.isOtpVerified) {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = {
                        newPassword = it
                        passwordError = null
                    },
                    label = { Text("New Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = if (isPasswordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    isError = passwordError != null,
                    supportingText = passwordError?.let { error -> { Text(error) } },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.LockReset,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reset password button
                Surface(
                    onClick = {
                        when {
                            !isStrongPassword(newPassword) -> {
                                passwordError = "Password must be at least 8 characters"
                            }
                            newPassword != confirmPassword -> {
                                passwordError = "Passwords do not match"
                            }
                            else -> {
                                viewModel.resetPassword(newPassword)
                            }
                        }
                    },
                    enabled = !viewModel.isSendingOtp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(18.dp),
                    color = Color.Transparent,
                    shadowElevation = 4.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = if (viewModel.isSendingOtp) {
                                        listOf(
                                            PrimaryBlue.copy(alpha = 0.5f),
                                            TertiaryViolet.copy(alpha = 0.5f)
                                        )
                                    } else {
                                        listOf(GradientPrimaryStart, GradientPrimaryEnd)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.isSendingOtp) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Resetting...",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = "Reset Password",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
