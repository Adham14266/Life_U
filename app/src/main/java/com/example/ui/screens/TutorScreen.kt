package com.example.ui.screens

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.data.local.StudyNote
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.ChatMode
import com.example.ui.viewmodel.MainViewModel

enum class TutorSubTab {
    Chat, Notes, Vault
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val listState = rememberLazyListState()

    var activeSubTab by remember { mutableStateOf(TutorSubTab.Chat) }

    LaunchedEffect(messages.size, viewModel.isUThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(SurfaceNormal.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(4.dp)
        ) {
            TutorTabItem(
                label = "Chat",
                icon = Icons.Default.ChatBubbleOutline,
                isSelected = activeSubTab == TutorSubTab.Chat,
                onClick = { activeSubTab = TutorSubTab.Chat },
                modifier = Modifier.weight(1f)
            )
            TutorTabItem(
                label = "Notes",
                icon = Icons.Default.Description,
                isSelected = activeSubTab == TutorSubTab.Notes,
                onClick = { activeSubTab = TutorSubTab.Notes },
                modifier = Modifier.weight(1f)
            )
            TutorTabItem(
                label = "Vault",
                icon = Icons.Default.FolderOpen,
                isSelected = activeSubTab == TutorSubTab.Vault,
                onClick = { activeSubTab = TutorSubTab.Vault },
                modifier = Modifier.weight(1f)
            )
        }

        AnimatedContent(
            targetState = activeSubTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tutor_tab_transition"
        ) { targetTab ->
            when (targetTab) {
                TutorSubTab.Chat -> ChatTab(viewModel, listState)
                TutorSubTab.Notes -> NotesTab(viewModel)
                TutorSubTab.Vault -> VaultTab(viewModel, onNavigateToChat = { activeSubTab = TutorSubTab.Chat })
            }
        }
    }
}

@Composable
fun TutorTabItem(label: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = contentColor, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = contentColor)
        }
    }
}

// ── CHAT TAB ────────────────────────────────────────────────

@Composable
fun ChatTab(viewModel: MainViewModel, listState: androidx.compose.foundation.lazy.LazyListState) {
    val messages by viewModel.chatMessages.collectAsState()
    var inputText by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            items(ChatMode.values()) { mode ->
                ChatModeChip(
                    label = when(mode) {
                        ChatMode.General -> "General"
                        ChatMode.ExplainLecture -> "Explain"
                        ChatMode.Quiz -> "Quiz Me"
                        ChatMode.MentalHealth -> "Wellness"
                    },
                    icon = when(mode) {
                        ChatMode.General -> Icons.Default.SmartToy
                        ChatMode.ExplainLecture -> Icons.Default.School
                        ChatMode.Quiz -> Icons.Default.Quiz
                        ChatMode.MentalHealth -> Icons.Default.Favorite
                    },
                    selected = viewModel.chatMode == mode,
                    onClick = { viewModel.chatMode = mode }
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(messages, key = { it.id }) { msg -> EnhancedChatMessage(msg, viewModel) }
            if (viewModel.isUThinking) { item { EnhancedThinkingIndicator() } }
        }

        ChatInputBar(
            value = inputText,
            onValueChange = { inputText = it },
            onSend = {
                if (inputText.isNotBlank()) {
                    viewModel.sendMessageToU(inputText)
                    inputText = ""
                }
            },
            viewModel = viewModel
        )
    }
}

// ── ENHANCED CHAT MESSAGE BUBBLE ────────────────────────────

@Composable
fun EnhancedChatMessage(message: ChatMessage, viewModel: MainViewModel) {
    val isUser = message.isUser
    val actionRegex = Regex("\\[ACTION: (ADD_TASK|ADD_CLASS) (.*?)\\]")
    val displayBody = message.text.replace(actionRegex, "").trim()
    val actions = actionRegex.findAll(message.text).map { it.groupValues }.toList()

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    AnimatedVisibility(
        visible = visible,
        enter = slideInHorizontally(initialOffsetX = { if (isUser) 50 else -50 }) + fadeIn(),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!isUser) {
                Box(
                    modifier = Modifier.size(34.dp).align(Alignment.Top).clip(CircleShape)
                        .background(brush = Brush.linearGradient(listOf(SecondaryGreen, AccentTeal))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.SmartToy, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(8.dp))
            }

            Column(modifier = Modifier.fillMaxWidth(0.85f)) {
                Surface(
                    shape = RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = if (isUser) 20.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 20.dp
                    ),
                    color = if (isUser) Color.Transparent else MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                brush = if (isUser) Brush.linearGradient(listOf(GradientPrimaryStart, GradientBloomEnd))
                                else Brush.horizontalGradient(listOf(GlassWhite, GlassWhite.copy(alpha = 0.6f))),
                                shape = RoundedCornerShape(
                                    topStart = 20.dp, topEnd = 20.dp,
                                    bottomStart = if (isUser) 20.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 20.dp
                                )
                            )
                            .border(
                                1.dp,
                                if (isUser) Color.Transparent else GlassWhiteBorder.copy(alpha = 0.3f),
                                RoundedCornerShape(
                                    topStart = 20.dp, topEnd = 20.dp,
                                    bottomStart = if (isUser) 20.dp else 4.dp,
                                    bottomEnd = if (isUser) 4.dp else 20.dp
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            if (displayBody.isNotBlank()) {
                                EnhancedMarkdownText(
                                    text = displayBody,
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            if (actions.isNotEmpty()) {
                                Spacer(Modifier.height(12.dp))
                                actions.forEach { (_, type, details) ->
                                    SmartActionChip(type, details, viewModel)
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        message.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.7f)
                    )
                    if (!isUser && viewModel != null) {
                        Spacer(Modifier.width(8.dp))
                        Icon(
                            imageVector = if (viewModel.isSpeaking) Icons.Default.StopCircle else Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Read Aloud",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                            modifier = Modifier.size(14.dp).clickable {
                                if (viewModel.isSpeaking) viewModel.stopSpeaking()
                                else viewModel.speakText(message.text)
                            }
                        )
                    }
                }
            }
        }
    }
}

// ── ENHANCED MARKDOWN ───────────────────────────────────────

@Composable
fun EnhancedMarkdownText(text: String, color: Color) {
    val annotated = remember(text) { parseRichMarkdown(text) }
    Text(
        text = annotated,
        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 24.sp),
        color = color
    )
}

fun parseRichMarkdown(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        var inCodeBlock = false
        val codeBuffer = StringBuilder()
        val codeLang = StringBuilder()

        for (line in lines) {
            if (line.trimStart().startsWith("```")) {
                if (inCodeBlock) {
                    val code = codeBuffer.toString().trimEnd()
                    // render code block with background
                    append("\n")
                    withStyle(SpanStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, background = Color(0x1A1E293B), fontSize = 13.sp)) {
                        append(code)
                    }
                    append("\n")
                    codeBuffer.clear()
                    codeLang.clear()
                    inCodeBlock = false
                } else {
                    inCodeBlock = true
                    codeLang.append(line.trimStart().drop(3).trim())
                }
                append("\n")
                continue
            }
            if (inCodeBlock) {
                codeBuffer.append(line).append("\n")
                continue
            }

            // headers
            val headerMatch = Regex("^(#{1,3})\\s+(.*)").find(line)
            if (headerMatch != null) {
                val level = headerMatch.groupValues[1].length
                val headerText = headerMatch.groupValues[2]
                val size = when (level) { 1 -> 22.sp; 2 -> 19.sp; else -> 17.sp }
                append("\n")
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontSize = size)) {
                    append(headerText)
                }
                append("\n")
                continue
            }

            // bullet points
            val bulletMatch = Regex("^\\s*[*-]\\s+(.*)").find(line)
            if (bulletMatch != null) {
                append("\n • ${bulletMatch.groupValues[1]}")
                continue
            }

            // numbered lists
            val numMatch = Regex("^\\s*\\d+\\.\\s+(.*)").find(line)
            if (numMatch != null) {
                append("\n ${numMatch.groupValues[0]}")
                continue
            }

            // horizontal rule
            if (line.trim().matches(Regex("^-{3,}$|^\\*{3,}$|^_{3,}$"))) {
                append("\n────────────────\n")
                continue
            }

            // inline formatting
            var processed = line
            val result = StringBuilder()
            var i = 0
            while (i < processed.length) {
                when {
                    // bold + italic ***text***
                    processed.startsWith("***", i) -> {
                        val end = processed.indexOf("***", i + 3)
                        if (end != -1) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, fontStyle = FontStyle.Italic)) {
                                append(processed.substring(i + 3, end))
                            }
                            i = end + 3
                        } else { result.append(processed[i]); i++ }
                    }
                    // bold **text**
                    processed.startsWith("**", i) -> {
                        val end = processed.indexOf("**", i + 2)
                        if (end != -1 && !(end + 2 < processed.length && processed[end + 2] == '*')) {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                append(processed.substring(i + 2, end))
                            }
                            i = end + 2
                        } else { result.append(processed[i]); i++ }
                    }
                    // italic *text*
                    processed.startsWith("*", i) && !processed.startsWith("**", i) -> {
                        val end = processed.indexOf("*", i + 1)
                        if (end != -1) {
                            withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                                append(processed.substring(i + 1, end))
                            }
                            i = end + 1
                        } else { result.append(processed[i]); i++ }
                    }
                    // inline code `text`
                    processed.startsWith("`", i) && !processed.startsWith("```", i) -> {
                        val end = processed.indexOf("`", i + 1)
                        if (end != -1) {
                            withStyle(SpanStyle(fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace, background = Color(0x1A1E293B))) {
                                append(processed.substring(i + 1, end))
                            }
                            i = end + 1
                        } else { result.append(processed[i]); i++ }
                    }
                    else -> { result.append(processed[i]); i++ }
                }
            }
            append(result.toString().replace("\\n", "\n"))
            append("\n")
        }
    }
}

// ── SMART ACTION CHIP ────────────────────────────────────────

@Composable
fun SmartActionChip(type: String, details: String, viewModel: MainViewModel) {
    val context = LocalContext.current
    Surface(
        onClick = {
            try {
                if (type == "ADD_TASK") {
                    viewModel.addTask(
                        details.substringAfter("Title: ").substringBefore(",").trim(),
                        "Medium",
                        details.substringAfter("Category: ").substringBefore(",").trim(),
                        details.substringAfter("Due: ").substringBefore(",").trim()
                    )
                } else {
                    viewModel.addClass(
                        details.substringAfter("Name: ").substringBefore(",").trim(),
                        details.substringAfter("Time: ").substringBefore(",").trim(),
                        details.substringAfter("Day: ").trim(),
                        "Lecture"
                    )
                }
                Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show()
            } catch (_: Exception) { }
        },
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(28.dp).background(MaterialTheme.colorScheme.primary, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (type == "ADD_TASK") Icons.Default.AddTask else Icons.Default.Event, null, tint = Color.White, modifier = Modifier.size(16.dp))
            }
            Spacer(Modifier.width(10.dp))
            Text(if (type == "ADD_TASK") "Add Task" else "Add to Schedule", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onPrimaryContainer)
        }
    }
}

// ── THINKING INDICATOR ──────────────────────────────────────

@Composable
fun EnhancedThinkingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "thinkDot")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(800, easing = LinearEasing), RepeatMode.Reverse),
        label = "alpha"
    )
    Row(modifier = Modifier.padding(start = 50.dp, top = 4.dp, bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(brush = Brush.linearGradient(listOf(SecondaryGreen, AccentTeal))), contentAlignment = Alignment.Center) {
            Icon(Icons.Default.SmartToy, null, tint = Color.White, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(10.dp))
        Text("U is thinking...", style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic, color = MaterialTheme.colorScheme.outline.copy(alpha = dotAlpha)))
    }
}

// ── CHAT INPUT BAR ──────────────────────────────────────────

@Composable
fun ChatInputBar(value: String, onValueChange: (String) -> Unit, onSend: () -> Unit, viewModel: MainViewModel) {
    val context = LocalContext.current
    val documentPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it) ?: "application/octet-stream"
            val fileName = context.contentResolver.query(it, null, null, null, null)?.use { c ->
                val nameIndex = c.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                if (c.moveToFirst()) c.getString(nameIndex) else "file"
            } ?: "file"
            viewModel.attachFile(it, fileName, mimeType)
        }
    }

    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = GlassWhite,
        border = BorderStroke(1.dp, GlassWhiteBorder.copy(alpha = 0.4f))
    ) {
        Column {
            // attached file preview
            AnimatedVisibility(visible = viewModel.attachedFileName != null) {
                viewModel.attachedFileName?.let { name ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.AttachFile, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text(name, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        IconButton(onClick = { viewModel.clearAttachedFile() }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) {
                IconButton(onClick = {
                    documentPicker.launch(arrayOf(
                        "application/pdf", "image/*",
                        "text/plain", "application/msword",
                        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
                    ))
                }) {
                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                }
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = { Text("Ask U anything...") },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = { onSend() })
                )
                IconButton(
                    onClick = onSend,
                    enabled = value.isNotBlank() || viewModel.attachedFileName != null,
                    modifier = Modifier.size(40.dp).clip(CircleShape).background(
                        if (value.isNotBlank() || viewModel.attachedFileName != null) MaterialTheme.colorScheme.primary else Color.Transparent
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send, null,
                        tint = if (value.isNotBlank() || viewModel.attachedFileName != null) Color.White else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

// ── CHAT MODE CHIP ──────────────────────────────────────────

@Composable
fun ChatModeChip(label: String, icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (selected) MaterialTheme.colorScheme.primary else SurfaceNormal.copy(alpha = 0.4f),
        contentColor = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        Row(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(label, style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
        }
    }
}

// ── NOTES TAB ────────────────────────────────────────────────

@Composable
fun NotesTab(viewModel: MainViewModel) {
    val notes by viewModel.notes.collectAsState()
    val subjectNames by viewModel.subjectNames.collectAsState()
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newCourse by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = GlassWhite,
            title = { Text("New Note", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = newTitle, onValueChange = { newTitle = it }, label = { Text("Title") }, singleLine = true, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newCourse, onValueChange = { newCourse = it }, label = { Text("Course") }, singleLine = true, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newContent, onValueChange = { newContent = it }, label = { Text("Content") }, shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth().height(200.dp))
                }
            },
            confirmButton = {
                Button(onClick = { if (newTitle.isNotBlank()) { viewModel.addNote(newTitle.trim(), newContent.trim(), newCourse.trim()); newTitle = ""; newContent = ""; newCourse = ""; showDialog = false } }, enabled = newTitle.isNotBlank(), shape = RoundedCornerShape(16.dp)) { Text("Save") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancel") } }
        )
    }

    val filteredNotes = remember(notes, selectedFilter, searchQuery) {
        notes.filter { note ->
            (selectedFilter == null || note.courseName == selectedFilter) &&
            (searchQuery.isBlank() || note.title.contains(searchQuery, ignoreCase = true) || note.content.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Your Materials", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                Button(onClick = { showDialog = true }) { Icon(Icons.Default.Add, null); Text("New") }
            }
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search notes...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassWhite,
                    unfocusedContainerColor = GlassWhite.copy(alpha = 0.5f)
                )
            )
            Spacer(Modifier.height(8.dp))
            if (subjectNames.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(null) + subjectNames) { subject ->
                        FilterChip(
                            selected = selectedFilter == subject,
                            onClick = { selectedFilter = if (selectedFilter == subject) null else subject },
                            label = { Text(subject ?: "All") }
                        )
                    }
                }
            }
        }

        if (filteredNotes.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Description, null, tint = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(48.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("No notes yet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }

        items(filteredNotes, key = { it.id }) { note ->
            StudyNoteCard(note = note, viewModel = viewModel)
        }
    }
}

@Composable
fun StudyNoteCard(note: StudyNote, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        border = BorderStroke(1.dp, GlassWhiteBorder.copy(alpha = 0.4f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(40.dp).background(Brush.linearGradient(listOf(GradientPrimaryStart, GradientBloomEnd)), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Description, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(note.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.courseName.isNotBlank()) {
                            Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(note.courseName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(note.dateCreated, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    }
                }
                IconButton(onClick = { viewModel.deleteNote(note) }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f), modifier = Modifier.size(18.dp))
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) {
                        Text(
                            note.content.ifBlank { "No content" },
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = {
                        viewModel.selectedNoteContext = note
                        viewModel.chatMode = ChatMode.ExplainLecture
                    }) {
                        Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Explain with AI")
                    }
                }
            }
        }
    }
}

// ── VAULT TAB ────────────────────────────────────────────────

@Composable
fun VaultTab(viewModel: MainViewModel, onNavigateToChat: () -> Unit = {}) {
    val notes by viewModel.notes.collectAsState()
    val subjectNames by viewModel.subjectNames.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var selectedNote by remember { mutableStateOf<StudyNote?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newTitle by remember { mutableStateOf("") }
    var newContent by remember { mutableStateOf("") }
    var newCourse by remember { mutableStateOf("") }

    val filteredNotes = remember(notes, selectedSubject, searchQuery) {
        notes.filter { n ->
            (selectedSubject == null || n.courseName == selectedSubject) &&
            (searchQuery.isBlank() || n.title.contains(searchQuery, ignoreCase = true) || n.content.contains(searchQuery, ignoreCase = true))
        }
    }

    // Note detail dialog
    if (showDetailDialog && selectedNote != null) {
        AlertDialog(
            onDismissRequest = { showDetailDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = GlassWhite,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(selectedNote!!.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.deleteNote(selectedNote!!); showDetailDialog = false }) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            },
            text = {
                Column {
                    if (selectedNote!!.courseName.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(selectedNote!!.courseName, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)) {
                        Text(
                            selectedNote!!.content.ifBlank { "No content" },
                            style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = {
                            viewModel.selectedNoteContext = selectedNote
                            viewModel.chatMode = ChatMode.ExplainLecture
                            showDetailDialog = false
                        }) {
                            Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Explain")
                        }
                        OutlinedButton(onClick = {
                            viewModel.selectedNoteContext = selectedNote
                            viewModel.chatMode = ChatMode.Quiz
                            showDetailDialog = false
                        }) {
                            Icon(Icons.Default.Quiz, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Quiz")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDetailDialog = false }) { Text("Close") }
            }
        )
    }

    // Add note dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            shape = RoundedCornerShape(28.dp),
            containerColor = GlassWhite,
            title = { Text("Add to Vault", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newTitle,
                        onValueChange = { newTitle = it },
                        label = { Text("Title") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newCourse,
                        onValueChange = { newCourse = it },
                        label = { Text("Subject / Course") },
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newContent,
                        onValueChange = { newContent = it },
                        label = { Text("Content") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().height(200.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            viewModel.addNote(newTitle.trim(), newContent.trim(), newCourse.trim())
                            newTitle = ""; newContent = ""; newCourse = ""
                            showAddDialog = false
                        }
                    },
                    enabled = newTitle.isNotBlank(),
                    shape = RoundedCornerShape(16.dp)
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Vault", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    Text("${notes.size} notes", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)
                }
                Button(onClick = { showAddDialog = true }) { Icon(Icons.Default.Add, null); Text("Add") }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search vault...") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = GlassWhite,
                    unfocusedContainerColor = GlassWhite.copy(alpha = 0.5f)
                )
            )
            Spacer(Modifier.height(8.dp))

            // Subject filters
            if (subjectNames.isNotEmpty()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf(null) + subjectNames) { subj ->
                        FilterChip(
                            selected = selectedSubject == subj,
                            onClick = { selectedSubject = if (selectedSubject == subj) null else subj },
                            label = { Text(subj ?: "All") }
                        )
                    }
                }
            }
        }

        if (filteredNotes.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(vertical = 60.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.FolderOpen, null, tint = MaterialTheme.colorScheme.outlineVariant, modifier = Modifier.size(64.dp))
                        Spacer(Modifier.height(12.dp))
                        Text("Your vault is empty", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.outline)
                        Spacer(Modifier.height(8.dp))
                        Text("Add notes and they'll appear here", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
                    }
                }
            }
        }

        items(filteredNotes, key = { it.id }) { note ->
            VaultNoteCard(
                note = note,
                onClick = { selectedNote = note; showDetailDialog = true },
                onExplain = {
                    viewModel.selectedNoteContext = note
                    viewModel.chatMode = ChatMode.ExplainLecture
                },
                onQuiz = {
                    viewModel.selectedNoteContext = note
                    viewModel.chatMode = ChatMode.Quiz
                },
                onSendToChat = {
                    val msg = "I'm studying this note:\n\n**${note.title}**\n${if (note.courseName.isNotBlank()) "📚 ${note.courseName}\n" else ""}\n─\n${note.content}\n─\n\nPlease help me understand this material."
                    viewModel.sendMessageToU(msg)
                    onNavigateToChat()
                }
            )
        }
    }
}

@Composable
fun VaultNoteCard(note: StudyNote, onClick: () -> Unit, onExplain: () -> Unit, onQuiz: () -> Unit, onSendToChat: () -> Unit = {}) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GlassWhite),
        border = BorderStroke(1.dp, GlassWhiteBorder.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).background(
                        brush = Brush.linearGradient(listOf(GradientPrimaryStart, GradientBloomEnd)),
                        shape = RoundedCornerShape(14.dp)
                    ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Description, null, tint = Color.White, modifier = Modifier.size(22.dp))
                }
                Spacer(Modifier.width(14.dp))
                Column(Modifier.weight(1f)) {
                    Text(note.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (note.courseName.isNotBlank()) {
                            Icon(Icons.Default.Book, null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), modifier = Modifier.size(12.dp))
                            Spacer(Modifier.width(3.dp))
                            Text(note.courseName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                            Spacer(Modifier.width(8.dp))
                        }
                        Text(note.dateCreated, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            if (note.content.isNotBlank()) {
                Text(
                    note.content,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(12.dp))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(
                    onClick = onExplain,
                    label = { Text("Explain") },
                    leadingIcon = { Icon(Icons.Default.School, null, modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
                AssistChip(
                    onClick = onQuiz,
                    label = { Text("Quiz") },
                    leadingIcon = { Icon(Icons.Default.Quiz, null, modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
                AssistChip(
                    onClick = onSendToChat,
                    label = { Text("Chat") },
                    leadingIcon = { Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(16.dp)) },
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    }
}
