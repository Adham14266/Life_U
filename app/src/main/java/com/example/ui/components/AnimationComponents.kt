package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.example.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Modern animated gradient background for chat interface
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        content()
    }
}

/**
 * Smooth pulsing animation for highlighting important messages
 */
@Composable
fun PulsingHighlight(
    modifier: Modifier = Modifier,
    color: Color = PrimaryBlue,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutQuad),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = modifier
            .background(color.copy(alpha = alpha), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        content()
    }
}

/**
 * Smooth scale animation for button interactions
 */
@Composable
fun AnimatedScaleButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "scale"
    )
    
    Box(
        modifier = modifier
            .graphicsLayer(scaleX = scale, scaleY = scale)
    ) {
        content()
    }
}

/**
 * Slide-in animation for new messages
 */
@Composable
fun SlideInMessage(
    isUserMessage: Boolean,
    content: @Composable () -> Unit
) {
    val offsetX by animateDpAsState(
        targetValue = 0.dp,
        animationSpec = tween(durationMillis = 400, easing = EaseOutQuad),
        label = "slide"
    )
    
    Box(
        modifier = Modifier.offset(
            x = if (isUserMessage) offsetX else -offsetX
        )
    ) {
        content()
    }
}

/**
 * Typing indicator animation (three bouncing dots)
 */
@Composable
fun TypingIndicator(
    modifier: Modifier = Modifier,
    dotColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    val dot1Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3Offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -8f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = dot1Offset.dp)
                .background(dotColor, shape = RoundedCornerShape(4.dp))
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = dot2Offset.dp)
                .background(dotColor, shape = RoundedCornerShape(4.dp))
        )
        Box(
            modifier = Modifier
                .size(8.dp)
                .offset(y = dot3Offset.dp)
                .background(dotColor, shape = RoundedCornerShape(4.dp))
        )
    }
}

/**
 * Shimmer loading animation for skeleton screens
 */
@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    width: Float = 200f,
    height: Float = 20f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerX by infiniteTransition.animateFloat(
        initialValue = -width,
        targetValue = width,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .size(width.dp, height.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                        Color.Transparent
                    ),
                    start = androidx.compose.ui.geometry.Offset(shimmerX, 0f),
                    end = androidx.compose.ui.geometry.Offset(shimmerX + width / 2, 0f)
                )
            )
    )
}

/**
 * Message bubble with fade-in effect
 */
@Composable
fun FadeInMessageBubble(
    modifier: Modifier = Modifier,
    isUserMessage: Boolean,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        visible = true
    }
    
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = EaseIn),
        label = "fade"
    )
    
    Box(
        modifier = modifier.graphicsLayer(alpha = alpha)
    ) {
        content()
    }
}
