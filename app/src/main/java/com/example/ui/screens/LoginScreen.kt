package com.example.ui.screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.validation.isNonBlank
import com.example.ui.validation.isStrongPassword
import com.example.ui.validation.isValidEmail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(viewModel: MainViewModel, isSignUpMode: Boolean = false) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf(viewModel.userName) }
    var university by remember { mutableStateOf(viewModel.userUniversity) }
    var faculty by remember { mutableStateOf(viewModel.userFaculty) }
    
    var isPasswordVisible by remember { mutableStateOf(false) }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var universityError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.userAvatarUrl = it.toString()
        }
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
        val isCompactHeight = maxHeight < 720.dp
        val isWideScreen = maxWidth >= 720.dp

        val horizontalPadding = when {
            maxWidth >= 900.dp -> 48.dp
            maxWidth >= 600.dp -> 32.dp
            else -> 20.dp
        }

        val formMaxWidth = if (isWideScreen) 520.dp else Dp.Unspecified
        val containerRadius = if (isWideScreen) 36.dp else 28.dp

        // Animated ambient gradient blobs
        val infiniteTransition = rememberInfiniteTransition(label = "bg")
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
        val blob1OffsetX = (if (isWideScreen) 420.dp else 300.dp) * (blob1X - 0.5f)
        val blob2OffsetX = (if (isWideScreen) 440.dp else 320.dp) * (blob2X - 0.5f)

        Box(
            modifier = Modifier
                .size(if (isWideScreen) 420.dp else 300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 90.dp + blob1OffsetX, y = (-90).dp)
                .blur(100.dp)
                .alpha(0.12f)
                .background(
                    Brush.linearGradient(
                        colors = listOf(SecondaryGreen, AccentTeal, GradientNeonStart)
                    ),
                    CircleShape
                )
        )

        Box(
            modifier = Modifier
                .size(if (isWideScreen) 440.dp else 320.dp)
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

        Box(
            modifier = Modifier
                .size(if (isWideScreen) 200.dp else 150.dp)
                .align(Alignment.Center)
                .offset(x = (-140).dp, y = 0.dp)
                .blur(80.dp)
                .alpha(0.06f)
                .background(AccentAmber, CircleShape)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = horizontalPadding)
                .padding(bottom = 16.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp)
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                                ),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Surface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                    tonalElevation = 2.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                            contentDescription = stringResource(R.string.help),
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(if (isCompactHeight) 8.dp else 18.dp))

            // Main form card (glassmorphism)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(max = formMaxWidth)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhite,
                                GlassWhite.copy(alpha = 0.85f),
                                GlassWhite.copy(alpha = 0.95f)
                            )
                        ),
                        shape = RoundedCornerShape(containerRadius)
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhiteBorder,
                                GlassWhiteBorder.copy(alpha = 0.3f),
                                GlassWhiteBorder
                            )
                        ),
                        shape = RoundedCornerShape(containerRadius)
                    )
                    .padding(
                        horizontal = if (isWideScreen) 32.dp else 22.dp,
                        vertical = if (isCompactHeight) 20.dp else 28.dp
                    )
            ) {
                // Logo / Avatar Picker
                Box(
                    modifier = Modifier
                        .size(if (isCompactHeight) 90.dp else 110.dp)
                        .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryBlue.copy(alpha = 0.10f),
                                TertiaryViolet.copy(alpha = 0.06f)
                            )
                        )
                    )
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                        ),
                        shape = CircleShape
                    )
                    .clickable { if (isSignUpMode) imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                    if (isSignUpMode && viewModel.userAvatarUrl.isNotEmpty()) {
                        coil.compose.AsyncImage(
                            model = viewModel.userAvatarUrl,
                            contentDescription = stringResource(R.string.selected_avatar),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = stringResource(R.string.life_u_logo),
                            modifier = Modifier
                                .size(if (isCompactHeight) 64.dp else 80.dp)
                                .clip(RoundedCornerShape(16.dp))
                        )
                    }
                    
                    if (isSignUpMode && viewModel.userAvatarUrl.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .background(MaterialTheme.colorScheme.primary, CircleShape)
                                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(if (isCompactHeight) 24.dp else 32.dp))

                // Title
                Text(
                    text = if (isSignUpMode) stringResource(R.string.create_account_title) else stringResource(R.string.welcome_back_title),
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = if (isWideScreen) 34.sp else 30.sp,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Subtitle
                Text(
                    text = if (isSignUpMode) {
                        stringResource(R.string.sign_up_subtitle)
                    } else {
                        stringResource(R.string.login_subtitle)
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = if (isWideScreen) 24.dp else 8.dp)
                )

                Spacer(modifier = Modifier.height(if (isCompactHeight) 20.dp else 30.dp))

                // Auth error banner
                viewModel.authError?.let { error ->
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f),
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(text = error, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Profile fields (sign-up only)
                if (isSignUpMode) {
                    OutlinedTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            viewModel.userName = it
                            fullNameError = null
                            viewModel.authError = null
                        },
                        label = { Text(stringResource(R.string.full_name)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = fullNameError != null,
                        supportingText = fullNameError?.let { error -> { Text(error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = university,
                        onValueChange = {
                            university = it
                            viewModel.userUniversity = it
                            universityError = null
                            viewModel.authError = null
                        },
                        label = { Text(stringResource(R.string.university_school)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier.fillMaxWidth(),
                        isError = universityError != null,
                        supportingText = universityError?.let { error -> { Text(error) } },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = faculty,
                        onValueChange = {
                            faculty = it
                            viewModel.userFaculty = it
                            viewModel.authError = null
                        },
                        label = { Text(stringResource(R.string.faculty_major_optional)) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null
                            )
                        },
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
                }

                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = null
                        viewModel.authError = null
                    },
                    label = { Text(stringResource(R.string.email_address)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Mail,
                            contentDescription = null
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

                Spacer(modifier = Modifier.height(16.dp))

                // Password field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = null
                        viewModel.authError = null
                    },
                    label = { Text(stringResource(R.string.password)) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = if (isPasswordVisible) {
                                    stringResource(R.string.hide_password)
                                } else {
                                    stringResource(R.string.show_password)
                                }
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
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

                // Forgot password (login mode only)
                if (!isSignUpMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { viewModel.navigateTo(AppScreen.ForgotPassword) }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Primary action button
                Surface(
                    onClick = {
                        var hasError = false
                        if (!isValidEmail(email)) {
                            emailError = context.getString(R.string.error_invalid_email)
                            hasError = true
                        }
                        if (!isStrongPassword(password)) {
                            passwordError = context.getString(R.string.error_weak_password)
                            hasError = true
                        }
                        if (isSignUpMode && !isNonBlank(fullName)) {
                            fullNameError = context.getString(R.string.error_empty_name)
                            hasError = true
                        }
                        if (isSignUpMode && !isNonBlank(university)) {
                            universityError = context.getString(R.string.error_empty_university)
                            hasError = true
                        }
                        if (!hasError) {
                            if (isSignUpMode) {
                                viewModel.signUp(email.trim(), password, fullName.trim())
                            } else {
                                viewModel.login(email.trim(), password)
                            }
                        }
                    },
                    enabled = !viewModel.isAuthenticating,
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
                                    colors = if (viewModel.isAuthenticating) {
                                        listOf(
                                            PrimaryBlue.copy(alpha = 0.5f),
                                            TertiaryViolet.copy(alpha = 0.5f)
                                        )
                                    } else {
                                        listOf(GradientPrimaryStart, GradientBloomEnd)
                                    }
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (viewModel.isAuthenticating) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(22.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isSignUpMode) stringResource(R.string.creating_account) else stringResource(R.string.signing_in),
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                )
                            }
                        } else {
                            Text(
                                text = if (isSignUpMode) stringResource(R.string.create_account_button) else stringResource(R.string.sign_in_button),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // "Or continue with" divider
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )
                    Text(
                        text = "  " + stringResource(R.string.or_continue_with) + "  ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Google Sign-In Button
                OutlinedButton(
                    onClick = {
                        val activity = context as? Activity
                        if (activity != null) {
                            viewModel.loginWithGoogle(activity)
                        }
                    },
                    enabled = !viewModel.isAuthenticating,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    // Improved Google "G" logo icon using Canvas
                    androidx.compose.foundation.Canvas(
                        modifier = Modifier.size(18.dp)
                    ) {
                        val strokeWidth = size.width * 0.22f
                        val arcSize = size.copy(width = size.width - strokeWidth, height = size.height - strokeWidth)
                        val offset = strokeWidth / 2f
                        
                        // Red (Top)
                        drawArc(
                            color = Color(0xFFEA4335),
                            startAngle = 225f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                        // Yellow (Left)
                        drawArc(
                            color = Color(0xFFFBBC05),
                            startAngle = 135f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                        // Green (Bottom)
                        drawArc(
                            color = Color(0xFF34A853),
                            startAngle = 45f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                        // Blue (Right + Middle Bar)
                        drawArc(
                            color = Color(0xFF4285F4),
                            startAngle = 315f,
                            sweepAngle = 90f,
                            useCenter = false,
                            topLeft = androidx.compose.ui.geometry.Offset(offset, offset),
                            size = arcSize,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth)
                        )
                        // Horizontal bar for the "G"
                        drawLine(
                            color = Color(0xFF4285F4),
                            start = androidx.compose.ui.geometry.Offset(size.width * 0.5f, size.height * 0.5f),
                            end = androidx.compose.ui.geometry.Offset(size.width, size.height * 0.5f),
                            strokeWidth = strokeWidth
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = stringResource(R.string.sign_in_with_google),
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Toggle login / sign up
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isSignUpMode) {
                            stringResource(R.string.already_have_account)
                        } else {
                            stringResource(R.string.dont_have_account)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isSignUpMode) stringResource(R.string.sign_in_button) else stringResource(R.string.sign_up_button),
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.authError = null
                                emailError = null
                                passwordError = null
                                fullNameError = null
                                if (isSignUpMode) {
                                    viewModel.navigateTo(AppScreen.Login)
                                } else {
                                    viewModel.navigateTo(AppScreen.SignUp)
                                }
                            }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer
            Text(
                text = stringResource(R.string.terms_and_privacy),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp)
            )
        }
    }
}
