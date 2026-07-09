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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun SplashScreen(viewModel: MainViewModel) {
    var loadingText by remember { mutableStateOf("Optimizing tasks...") }

    val logoScale = remember { Animatable(0f) }
    val logoAlpha = remember { Animatable(0f) }
    val logoRotation = remember { Animatable(-30f) }
    val titleAlpha = remember { Animatable(0f) }
    val titleSlide = remember { Animatable(30f) }
    val subtitleAlpha = remember { Animatable(0f) }
    val loaderAlpha = remember { Animatable(0f) }
    val ringScale = remember { Animatable(0.6f) }
    val bgGradientProgress = remember { Animatable(0f) }

    val phase1Done = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        coroutineScope {
            launch {
                logoScale.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                )
            }
            launch {
                logoAlpha.animateTo(1f, animationSpec = tween(600, easing = EaseOutCubic))
            }
            launch {
                logoRotation.animateTo(0f, animationSpec = tween(800, easing = EaseOutBack))
            }
            launch {
                ringScale.animateTo(
                    1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessVeryLow
                    )
                )
            }
            launch {
                bgGradientProgress.animateTo(
                    1f,
                    animationSpec = tween(2000, easing = EaseInOutCubic)
                )
            }

            launch {
                delay(400)
                phase1Done.value = true

                launch {
                    titleAlpha.animateTo(1f, animationSpec = tween(500, easing = EaseOutCubic))
                }
                launch {
                    titleSlide.animateTo(0f, animationSpec = tween(500, easing = EaseOutCubic))
                }

                delay(250)
                subtitleAlpha.animateTo(1f, animationSpec = tween(400, easing = EaseOutCubic))

                delay(200)
                loaderAlpha.animateTo(1f, animationSpec = tween(300))

                delay(600)
                loadingText = "Updating courses..."
                delay(800)
                loadingText = "Ready to study!"
                delay(600)
                // Only navigate if still on Splash (ViewModel may have already navigated)
                if (viewModel.currentScreen == AppScreen.Splash) {
                    viewModel.completeOnboarding()
                    viewModel.navigateTo(AppScreen.OnboardingStage1)
                }
            }
        }
    }

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

    val ringPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ringPulse"
    )

    val glowRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "glowRotation"
    )

    val logoBreath by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoBreath"
    )

    val spinnerAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spinnerAngle"
    )

    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    val particles = remember {
        (1..30).map {
            Particle(
                x = Random.nextFloat(),
                y = Random.nextFloat(),
                size = Random.nextFloat() * 4f + 2f,
                speed = Random.nextFloat() * 0.003f + 0.001f,
                drift = (Random.nextFloat() - 0.5f) * 0.005f,
                alpha = Random.nextFloat() * 0.5f + 0.1f,
                hue = Random.nextFloat()
            )
        }
    }

    val particlePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particlePhase"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val bgGradient = Brush.radialGradient(
                colors = listOf(
                    PrimaryBlue.copy(alpha = 0.06f * bgGradientProgress.value),
                    SecondaryGreen.copy(alpha = 0.03f * bgGradientProgress.value),
                    Color.Transparent
                ),
                radius = size.maxDimension * 0.8f,
                center = Offset(
                    size.width * (0.5f + 0.2f * cos(bgGradientProgress.value * 6.28f)),
                    size.height * (0.5f + 0.2f * sin(bgGradientProgress.value * 4.71f))
                )
            )
            drawCircle(
                brush = bgGradient,
                radius = size.maxDimension,
                style = Fill
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val columns = 15
            val rows = 25
            val spacingX = size.width / columns
            val spacingY = size.height / rows
            for (c in 0..columns) {
                for (r in 0..rows) {
                    val ripple = sin(particlePhase * 6.28f + c * 0.5f + r * 0.3f) * 0.02f + 0.03f
                    drawCircle(
                        color = PrimaryBlue.copy(alpha = ripple),
                        radius = 2.dp.toPx(),
                        center = Offset(c * spacingX, r * spacingY)
                    )
                }
            }
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            particles.forEach { p ->
                val x = (p.x + sin(particlePhase * 6.28f + p.hue * 10f) * p.drift * 100f) * size.width
                val y = (p.y + particlePhase * p.speed * 1000f) % 1.2f * size.height - 0.1f * size.height
                val flicker = sin(particlePhase * 12.56f + p.hue * 20f) * 0.3f + 0.7f
                drawCircle(
                    color = Color.White.copy(alpha = p.alpha * flicker * bgGradientProgress.value),
                    radius = p.size.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }

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

        Icon(
            imageVector = Icons.Default.School,
            contentDescription = null,
            tint = PrimaryBlue.copy(alpha = 0.08f),
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.TopStart)
                .offset(x = 40.dp, y = (100 + floatAnim).dp)
                .rotate(spinnerAngle * 0.1f)
        )
        Icon(
            imageVector = Icons.Default.Book,
            contentDescription = null,
            tint = SecondaryGreen.copy(alpha = 0.08f),
            modifier = Modifier
                .size(80.dp)
                .align(Alignment.CenterStart)
                .offset(x = 20.dp, y = (220 - floatAnim).dp)
                .rotate(-spinnerAngle * 0.15f)
        )
        Icon(
            imageVector = Icons.Default.Psychology,
            contentDescription = null,
            tint = PrimaryBlue.copy(alpha = 0.08f),
            modifier = Modifier
                .size(70.dp)
                .align(Alignment.CenterEnd)
                .offset(x = (-30).dp, y = (-50 + floatAnim).dp)
                .rotate(spinnerAngle * 0.2f)
        )
        Icon(
            imageVector = Icons.Default.CalendarToday,
            contentDescription = null,
            tint = SecondaryGreen.copy(alpha = 0.08f),
            modifier = Modifier
                .size(90.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-40).dp, y = (-120 - floatAnim).dp)
                .rotate(-spinnerAngle * 0.12f)
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(160.dp)
                    .graphicsLayer {
                        scaleX = logoScale.value * logoBreath
                        scaleY = logoScale.value * logoBreath
                        alpha = logoAlpha.value
                        rotationZ = logoRotation.value
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .scale(ringScale.value * ringPulse)
                        .rotate(glowRotation)
                        .blur(24.dp)
                        .alpha(0.18f)
                        .background(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    GradientPrimaryStart,
                                    GradientCoolStart,
                                    GradientPrimaryEnd,
                                    GradientNatureStart,
                                    GradientPrimaryStart
                                )
                            ),
                            CircleShape
                        )
                )

                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(SurfaceLowest, SurfaceLow)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clip(RoundedCornerShape(24.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Life U Emblem",
                        modifier = Modifier.fillMaxSize()
                    )
                }

                if (phase1Done.value) {
                    Canvas(
                        modifier = Modifier
                            .size(180.dp)
                            .alpha(0.3f)
                    ) {
                        val arcProgress = (sin(particlePhase * 6.28f) * 0.5f + 0.5f) * 270f + 45f
                        drawArc(
                            color = PrimaryBlue.copy(alpha = 0.4f),
                            startAngle = spinnerAngle + 0f,
                            sweepAngle = arcProgress * 0.3f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2.dp.toPx()
                            ),
                            topLeft = Offset(
                                (size.width - size.width * 0.9f) / 2f,
                                (size.height - size.height * 0.9f) / 2f
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                size.width * 0.9f,
                                size.height * 0.9f
                            )
                        )
                        drawArc(
                            color = SecondaryGreen.copy(alpha = 0.4f),
                            startAngle = spinnerAngle + 180f,
                            sweepAngle = arcProgress * 0.3f,
                            useCenter = false,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2.dp.toPx()
                            ),
                            topLeft = Offset(
                                (size.width - size.width * 0.9f) / 2f,
                                (size.height - size.height * 0.9f) / 2f
                            ),
                            size = androidx.compose.ui.geometry.Size(
                                size.width * 0.9f,
                                size.height * 0.9f
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Life U",
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = (-0.5).sp
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .graphicsLayer {
                        alpha = titleAlpha.value
                        translationY = titleSlide.value
                    }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Study Smarter. Live Better.",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.alpha(subtitleAlpha.value)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
                .alpha(loaderAlpha.value)
        ) {
            AnimatedDotsLoader()

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = loadingText.uppercase(),
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(
                    alpha = dotAlpha + 0.4f
                )
            )
        }
    }
}

@Composable
private fun AnimatedDotsLoader() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")

    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 0
                1f at 300
                0f at 600
            },
            repeatMode = RepeatMode.Restart
        ), label = "dot1"
    )
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 200
                1f at 500
                0f at 800
            },
            repeatMode = RepeatMode.Restart
        ), label = "dot2"
    )
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 1200
                0f at 400
                1f at 700
                0f at 1000
            },
            repeatMode = RepeatMode.Restart
        ), label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        listOf(dot1, dot2, dot3).forEach { anim ->
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .scale(0.6f + anim * 0.4f)
                    .alpha(0.4f + anim * 0.6f)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
        }
    }
}

private data class Particle(
    val x: Float,
    val y: Float,
    val size: Float,
    val speed: Float,
    val drift: Float,
    val alpha: Float,
    val hue: Float
)
