package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(viewModel: MainViewModel) {
    var loadingText by remember { mutableStateOf("Optimizing tasks...") }

    // Simulated loading text transition
    LaunchedEffect(Unit) {
        delay(1000)
        loadingText = "Updating courses..."
        delay(1000)
        loadingText = "Ready to study!"
        delay(1000)
        viewModel.navigateTo(AppScreen.OnboardingStage1)
    }

    // Floating animations for background decorations
    val infiniteTransition = rememberInfiniteTransition(label = "splash")
    val floatAnim by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "float"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Geometric Dot Grid Background (3% opacity)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 15
            val rows = 25
            val spacingX = size.width / columns
            val spacingY = size.height / rows
            for (c in 0..columns) {
                for (r in 0..rows) {
                    drawCircle(
                        color = PrimaryBlue.copy(alpha = 0.03f),
                        radius = 2.dp.toPx(),
                        center = Offset(c * spacingX, r * spacingY)
                    )
                }
            }
        }

        // Ambient Background Glows
        Box(
            modifier = Modifier
                .size(300.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .blur(80.dp)
                .alpha(0.08f)
                .background(PrimaryBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-80).dp, y = 80.dp)
                .blur(100.dp)
                .alpha(0.06f)
                .background(SecondaryGreen, CircleShape)
        )

        // Floating Icons (Simulating the academic environment)
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = PrimaryBlue.copy(alpha = 0.08f),
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopStart)
                .offset(x = 40.dp, y = (100 + floatAnim).dp)
        )
        Icon(
            imageVector = Icons.Default.Book,
            contentDescription = null,
            tint = SecondaryGreen.copy(alpha = 0.08f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterStart)
                .offset(x = 20.dp, y = (220 - floatAnim).dp)
        )
        Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = PrimaryBlue.copy(alpha = 0.08f),
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-30).dp, y = (-50 + floatAnim).dp)
        )
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = SecondaryGreen.copy(alpha = 0.08f),
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-40).dp, y = (-120 - floatAnim).dp)
        )

        // Main Content Area
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo Outer Glow & Book/Circuit Emblem
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .scale(1.1f)
            ) {
                // Outer ring glowing background
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .blur(24.dp)
                        .alpha(0.12f)
                        .background(PrimaryBlue, CircleShape)
                )

                // Emblem
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(SurfaceLowest, SurfaceLow)
                            ),
                            shape = MaterialTheme.shapes.large
                        )
                        .clip(MaterialTheme.shapes.large),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Life U Emblem",
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Typography
            Text(
                text = "Life U",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Study Smarter. Live Better.",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Loading and Ready state indicator
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = loadingText.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
