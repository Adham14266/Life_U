package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.MainViewModel

/**
 * Modern Animated Chat Bubble with Markdown-lite support
 */
@Composable
fun EnhancedChatMessageBubble(
    message: ChatMessage,
    viewModel: MainViewModel? = null,
    onAddSchedule: (title: String, dueDate: String) -> Unit = { _, _ -> }
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { if (message.isUser) 50 else -50 }) + fadeIn(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isUser) {
                BotAvatar()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
                modifier = Modifier.fillMaxWidth(0.88f)
            ) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (message.isUser) 20.dp else 4.dp,
                        bottomEnd = if (message.isUser) 4.dp else 20.dp
                    ),
                    color = if (message.isUser) MaterialTheme.colorScheme.primary else SurfaceNormal,
                    tonalElevation = 2.dp,
                    shadowElevation = 1.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        MarkdownText(
                            text = message.text,
                            color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface
                        )

                        // Actionable Schedule Suggestions
                        if (!message.isUser) {
                            val smartActions = extractSmartActions(message.text)
                            if (smartActions.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    smartActions.forEach { action ->
                                        ScheduleActionChip(action, onAddSchedule)
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = message.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    if (!message.isUser && viewModel != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = if (viewModel.isSpeaking) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read Aloud",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp).clickable {
                                if (viewModel.isSpeaking) viewModel.stopSpeaking() else viewModel.speakText(message.text)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedThinkingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinking")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dotAlpha"
    )

    Row(
        modifier = Modifier
            .background(SurfaceNormal, RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BotAvatar()
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = "U is thinking...",
            style = MaterialTheme.typography.bodyMedium.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = dotAlpha)
        )
    }
}

@Composable
fun BotAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(SecondaryGreen, SecondaryGreen.copy(alpha = 0.7f))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun MarkdownText(text: String, color: Color) {
    val annotatedString = parseMarkdown(text)
    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
        color = color
    )
}

/**
 * Simple Regex-based Markdown Parser for Bold, Italic, Bullets, and Headers
 */
fun parseMarkdown(text: String): AnnotatedString {
    return buildAnnotatedString {
        var cursor = 0
        
        // Match bold **text**
        val boldRegex = Regex("\\*\\*(.*?)\\*\\*")
        // Match bullet points
        val bulletRegex = Regex("(?m)^\\s*[*-]\\s+(.*)$")
        // Match headers ## Header
        val headerRegex = Regex("(?m)^#{1,3}\\s+(.*)$")
        
        val matches = (boldRegex.findAll(text) + bulletRegex.findAll(text) + headerRegex.findAll(text))
            .sortedBy { it.range.first }
        
        matches.forEach { match ->
            if (match.range.first > cursor) {
                append(text.substring(cursor, match.range.first))
            }
            
            val value = match.value
            when {
                value.startsWith("**") -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = Color.Unspecified)) {
                        append(match.groupValues[1])
                    }
                }
                value.startsWith("#") -> {
                    val headerText = match.groupValues[1]
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Black, fontSize = 18.sp)) {
                        append("\n$headerText\n")
                    }
                }
                else -> {
                    // Bullet point
                    append("\n • ")
                    append(match.groupValues[1])
                }
            }
            
            cursor = match.range.last + 1
        }
        
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }
}

data class SmartAction(val title: String, val date: String, val isTask: Boolean = true, val time: String = "09:00 AM - 10:30 AM")

fun extractSmartActions(text: String): List<SmartAction> {
    val actions = mutableListOf<SmartAction>()
    
    // 1. Look for 'Task: "Name", Due: "Day"'
    val taskRegex = Regex("Task: \"(.*?)\", Due: \"(.*?)\"", RegexOption.IGNORE_CASE)
    taskRegex.findAll(text).forEach { match ->
        actions.add(SmartAction(match.groupValues[1], match.groupValues[2], isTask = true))
    }
    
    // 2. Look for 'Class: Name, Day: Monday, Time: 10:00 AM - 11:30 AM'
    val classRegex = Regex("Class: (.*?), Day: (.*?), Time: (.*?)", RegexOption.IGNORE_CASE)
    classRegex.findAll(text).forEach { match ->
        actions.add(SmartAction(match.groupValues[1], match.groupValues[2], isTask = false, time = match.groupValues[3]))
    }
    
    return actions
}

@Composable
fun ScheduleActionChip(action: SmartAction, onAdd: (String, String) -> Unit) {
    Surface(
        onClick = { onAdd(action.title, action.date) },
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (action.isTask) Icons.AutoMirrored.Filled.Assignment else Icons.Default.Event,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = action.title,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (action.isTask) "Due: ${action.date}" else "${action.date} • ${action.time}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
