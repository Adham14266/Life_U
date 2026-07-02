package com.example.ui.screens

import androidx.compose.animation.*
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.MainViewModel
import com.example.data.local.StudyNote
import com.example.data.local.StudyResource
import com.example.ui.validation.isNonBlank
import com.example.ui.validation.isValidWebUrl
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.platform.testTag
import kotlinx.coroutines.launch

enum class TutorSubTab {
    Chat, Notes, Vault
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorScreen(viewModel: MainViewModel) {
    val messages by viewModel.chatMessages.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val resourcesList by viewModel.resources.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var activeSubTab by remember { mutableStateOf(TutorSubTab.Chat) }
    var tempTitleError by remember { mutableStateOf<String?>(null) }
    var tempContentError by remember { mutableStateOf<String?>(null) }

    // Auto-scroll to bottom on new messages
    LaunchedEffect(messages.size, viewModel.isStitchThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Sliding Sub-Tabs at the top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(SurfaceNormal, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == TutorSubTab.Chat) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = TutorSubTab.Chat }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        tint = if (activeSubTab == TutorSubTab.Chat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == TutorSubTab.Chat) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == TutorSubTab.Notes) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = TutorSubTab.Notes }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = if (activeSubTab == TutorSubTab.Notes) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Notes (${notes.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == TutorSubTab.Notes) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == TutorSubTab.Vault) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = TutorSubTab.Vault }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderSpecial,
                        contentDescription = null,
                        tint = if (activeSubTab == TutorSubTab.Vault) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vault (${resourcesList.size})",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == TutorSubTab.Vault) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // Display Active Sub Tab
        if (activeSubTab == TutorSubTab.Notes) {
            // Study Notes Manager Tab
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Your Course Materials",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Ask Stitch questions about your study notes",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        Button(
                            onClick = { viewModel.showAddNoteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Note", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                if (notes.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 64.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "No study notes created yet",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Create a note to ask Stitch specific questions about your courses!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                } else {
                    items(notes) { note ->
                        StudyNoteCard(
                            note = note,
                            isActiveContext = viewModel.selectedNoteContext?.id == note.id,
                            onSelect = {
                                viewModel.selectedNoteContext = note
                                activeSubTab = TutorSubTab.Chat
                                inputText = "Can you summarize this note for me?"
                            },
                            onDelete = { viewModel.deleteNote(note) }
                        )
                    }
                }
            }
        } else if (activeSubTab == TutorSubTab.Vault) {
            // Resource Vault Tab
            ResourceVaultScreen(
                viewModel = viewModel,
                onNavigateToChat = { prompt ->
                    inputText = prompt
                    activeSubTab = TutorSubTab.Chat
                }
            )
        } else {
            // Chat Tab
            // Active Context Badge
            val selectedNote = viewModel.selectedNoteContext
            if (selectedNote != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .background(SecondaryGreenContainer.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                        .border(1.dp, SecondaryGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Active Study Context:",
                                style = MaterialTheme.typography.labelSmall,
                                color = SecondaryGreen
                            )
                            Text(
                                text = selectedNote.title,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.selectedNoteContext = null },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear context",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Chat messages scrollable area
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Welcome Header (Only show if messages have basic elements)
                if (messages.isEmpty()) {
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .background(PrimaryBlueContainer, RoundedCornerShape(20.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "Stitch",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Hi, I'm Stitch",
                                style = MaterialTheme.typography.headlineLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 26.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Your intelligent study companion. Type a general academic question below, or head over to the \"My Notes\" tab to select a study guide context!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 32.dp)
                            )
                        }
                    }
                }

                items(messages, key = { it.id }) { msg ->
                    ChatMessageRow(msg)
                }

                // Thinking State Indicator
                if (viewModel.isStitchThinking) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(SecondaryGreenContainer.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = null,
                                    tint = SecondaryGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(SurfaceNormal, RoundedCornerShape(16.dp))
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Stitch is studying your notes...",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontStyle = FontStyle.Italic),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        strokeWidth = 2.dp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Fixed Controls at the bottom
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 12.dp)
            ) {
                // Horizontal sliding Suggested Prompts
                val suggestedPrompts = if (selectedNote != null) {
                    listOf(
                        "Explain this simply",
                        "Generate study guide",
                        "Quiz me on this",
                        "Create 5 flashcards"
                    )
                } else {
                    listOf(
                        "Explain photosynthesis",
                        "Explain derivative rules",
                        "Mitosis vs Meiosis",
                        "Calculus tips"
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    items(suggestedPrompts) { prompt ->
                        Box(
                            modifier = Modifier
                                .background(SurfaceLowest, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                .clickable {
                                    viewModel.sendMessageToStitch(prompt)
                                }
                                .padding(horizontal = 16.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = prompt,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }

                // Input Bar Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .background(SurfaceNormal, RoundedCornerShape(32.dp))
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f), RoundedCornerShape(32.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    // Quick Attach note indicator badge
                    IconButton(onClick = {
                        activeSubTab = TutorSubTab.Notes
                    }) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = "Attach Saved Note",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    TextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = {
                            Text(
                                if (selectedNote != null) "Ask about '${selectedNote.title}'..." else "Ask Stitch anything...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (inputText.trim().isNotEmpty()) {
                                    viewModel.sendMessageToStitch(inputText)
                                    inputText = ""
                                }
                            }
                        )
                    )

                    // Voice mic quick prompt helper
                    IconButton(onClick = {
                        inputText = if (selectedNote != null) {
                            "Explain the most important concepts in this note."
                        } else {
                            "Give me a quick motivation speech for studying!"
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic helper",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Send Icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable {
                                if (inputText.trim().isNotEmpty()) {
                                    viewModel.sendMessageToStitch(inputText)
                                    inputText = ""
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Note Dialog
    if (viewModel.showAddNoteDialog) {
        var tempTitle by remember { mutableStateOf("") }
        var tempContent by remember { mutableStateOf("") }
        var tempCourse by remember { mutableStateOf("Biology") }

        AlertDialog(
            onDismissRequest = { viewModel.showAddNoteDialog = false },
            title = { Text("Add Study Note", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = {
                            tempTitle = it
                            tempTitleError = null
                        },
                        label = { Text("Note Title") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempTitleError != null,
                        supportingText = tempTitleError?.let { error -> { Text(error) } }
                    )

                    OutlinedTextField(
                        value = tempContent,
                        onValueChange = {
                            tempContent = it
                            tempContentError = null
                        },
                        label = { Text("Content / Body") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4,
                        maxLines = 8,
                        isError = tempContentError != null,
                        supportingText = tempContentError?.let { error -> { Text(error) } }
                    )

                    Text("Course/Subject", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("Biology", "Calculus", "History", "General").forEach { course ->
                            val isSelected = tempCourse == course
                            val color = when (course) {
                                "Biology" -> SecondaryGreen
                                "Calculus" -> PrimaryBlue
                                "History" -> TertiaryNavy
                                else -> Color(0xFFFF9800)
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) color else SurfaceNormal,
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable { tempCourse = course }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = course,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val validTitle = isNonBlank(tempTitle)
                        val validContent = isNonBlank(tempContent)

                        tempTitleError = if (!validTitle) "Note title is required" else null
                        tempContentError = if (!validContent) "Note content is required" else null

                        if (validTitle && validContent) {
                            viewModel.addNote(tempTitle.trim(), tempContent.trim(), tempCourse)
                            viewModel.showAddNoteDialog = false
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddNoteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudyNoteCard(
    note: StudyNote,
    isActiveContext: Boolean,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    val courseColor = when (note.courseName) {
        "Biology" -> SecondaryGreen
        "Calculus" -> PrimaryBlue
        "History" -> TertiaryNavy
        else -> Color(0xFFFF9800)
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isActiveContext) 2.dp else 1.dp,
                color = if (isActiveContext) SecondaryGreen else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Course/Subject Badge
                Box(
                    modifier = Modifier
                        .background(courseColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = note.courseName,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = courseColor
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = note.dateCreated,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Note",
                            tint = ErrorRed.copy(alpha = 0.8f),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isActiveContext) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = SecondaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Active Context",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = SecondaryGreen
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, courseColor),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = courseColor,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Study with Stitch",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = courseColor)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageRow(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!message.isUser) {
            // Stitch profile avatar thumbnail on left
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(SecondaryGreenContainer.copy(alpha = 0.2f), CircleShape)
                    .align(Alignment.Top),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = null,
                    tint = SecondaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = if (message.isUser) MaterialTheme.colorScheme.primary else SurfaceNormal,
                        shape = RoundedCornerShape(
                            topStart = 20.dp,
                            topEnd = 20.dp,
                            bottomStart = if (message.isUser) 20.dp else 0.dp,
                            bottomEnd = if (message.isUser) 0.dp else 20.dp
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 22.sp),
                        color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurface
                    )

                    // Optional attached file card
                    message.attachedFile?.let { file ->
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = if (message.isUser) Color.White else MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = file,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (message.isUser) Color.White else MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = message.time,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceVaultScreen(
    viewModel: MainViewModel,
    onNavigateToChat: (String) -> Unit
) {
    val resourcesList by viewModel.resources.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Textbooks", "Study Materials", "Academic Articles", "Other")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Search & Add row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search title, subject, notes...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search icon") },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Button(
                onClick = { viewModel.showAddResourceDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
                modifier = Modifier.testTag("add_resource_button")
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Resource")
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }

        // Category Pills
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            contentPadding = PaddingValues(end = 16.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    label = { Text(category, style = MaterialTheme.typography.labelMedium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White,
                        containerColor = SurfaceNormal,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = Color.Transparent,
                        selectedBorderColor = Color.Transparent
                    ),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.testTag("category_pill_${category.lowercase().replace(" ", "_")}")
                )
            }
        }

        // Filtered resource list
        val filteredList = resourcesList.filter { res ->
            val matchesCategory = selectedCategory == "All" || res.category == selectedCategory
            val matchesSearch = res.title.contains(searchQuery, ignoreCase = true) ||
                    res.url.contains(searchQuery, ignoreCase = true) ||
                    res.courseName.contains(searchQuery, ignoreCase = true) ||
                    res.notes.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }

        if (filteredList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        imageVector = if (searchQuery.isNotEmpty()) Icons.Default.SearchOff else Icons.Default.FolderOpen,
                        contentDescription = "No items",
                        tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No matches found" else "Vault is empty",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isNotEmpty()) {
                            "Try adjusting your search terms or filter to find what you're looking for."
                        } else {
                            "Save study guides, textbook links, or academic reference articles. Access them instantly anytime."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    if (searchQuery.isEmpty() && resourcesList.isEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.showAddResourceDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Save Your First Resource")
                        }
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredList) { resource ->
                    ResourceCard(
                        resource = resource,
                        onDelete = { viewModel.deleteResource(resource) },
                        onAskStitch = {
                            val prompt = "I'm studying the resource: ${resource.title}. Url: ${resource.url}. Notes: ${resource.notes}. Can you give me a quick quiz or outline some core questions to help me study this material?"
                            onNavigateToChat(prompt)
                        }
                    )
                }
            }
        }
    }

    if (viewModel.showAddResourceDialog) {
        AddResourceDialog(viewModel = viewModel)
    }
}

@Composable
fun ResourceCard(
    resource: StudyResource,
    onDelete: () -> Unit,
    onAskStitch: () -> Unit
) {
    val context = LocalContext.current
    val uriHandler = LocalUriHandler.current
    val clipboardManager = LocalClipboardManager.current

    val tagBgColor = when (resource.category) {
        "Textbooks" -> MaterialTheme.colorScheme.primaryContainer
        "Study Materials" -> SecondaryGreenContainer
        "Academic Articles" -> ErrorContainer
        else -> MaterialTheme.colorScheme.secondaryContainer
    }

    val tagTextColor = when (resource.category) {
        "Textbooks" -> MaterialTheme.colorScheme.onPrimaryContainer
        "Study Materials" -> OnSecondaryContainer
        "Academic Articles" -> OnErrorContainer
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("resource_card_${resource.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, OutlineVariantSlate.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Category, Subject and date added header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Tag
                    Box(
                        modifier = Modifier
                            .background(tagBgColor, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = resource.category,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = tagTextColor
                        )
                    }

                    // Course Name Tag
                    if (resource.courseName.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = resource.courseName,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Date Added
                Text(
                    text = resource.dateAdded,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Title
            Text(
                text = resource.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            // Notes / Description
            if (resource.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = resource.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Divider
            HorizontalDivider(color = OutlineVariantSlate.copy(alpha = 0.3f))

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left action links
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Open URL button
                    FilledTonalButton(
                        onClick = {
                            try {
                                var formattedUrl = resource.url.trim()
                                if (!formattedUrl.startsWith("http://") && !formattedUrl.startsWith("https://")) {
                                    formattedUrl = "https://$formattedUrl"
                                }
                                uriHandler.openUri(formattedUrl)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open URL: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Link",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Open", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                    }

                    // Copy link button
                    IconButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(resource.url))
                            Toast.makeText(context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Link",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Ask Stitch button
                    IconButton(
                        onClick = {
                            onAskStitch()
                            Toast.makeText(context, "Stitch prompt created! Switching to Study Chat.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "Ask Stitch about resource",
                            tint = SecondaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Delete resource button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Delete Resource",
                        tint = ErrorRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddResourceDialog(viewModel: MainViewModel) {
    var title by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Textbooks") }
    var notes by remember { mutableStateOf("") }
    var courseName by remember { mutableStateOf("") }
    var titleError by remember { mutableStateOf<String?>(null) }
    var urlError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Textbooks", "Study Materials", "Academic Articles", "Other")
    var isDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { viewModel.showAddResourceDialog = false },
        title = { Text("Save to Resource Vault", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        titleError = null
                    },
                    label = { Text("Resource Title *") },
                    placeholder = { Text("e.g. Campbell Biology 12th Ed") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_title_input"),
                    singleLine = true,
                    isError = titleError != null,
                    supportingText = titleError?.let { error -> { Text(error) } }
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = {
                        url = it
                        urlError = null
                    },
                    label = { Text("URL / Link *") },
                    placeholder = { Text("e.g. openstax.org/biology") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_url_input"),
                    singleLine = true,
                    isError = urlError != null,
                    supportingText = urlError?.let { error -> { Text(error) } }
                )

                // Category Selection Dropdown
                ExposedDropdownMenuBox(
                    expanded = isDropdownExpanded,
                    onExpandedChange = { isDropdownExpanded = !isDropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                            .testTag("dialog_category_input")
                    )
                    ExposedDropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false }
                    ) {
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    isDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = courseName,
                    onValueChange = { courseName = it },
                    label = { Text("Course Name / Subject (Optional)") },
                    placeholder = { Text("e.g. Biology") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_course_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Description (Optional)") },
                    placeholder = { Text("e.g. Chapters 1-10 study guides") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_notes_input"),
                    minLines = 2,
                    maxLines = 4
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val validTitle = isNonBlank(title)
                    val validUrl = isValidWebUrl(url)

                    titleError = if (!validTitle) "Resource title is required" else null
                    urlError = if (!validUrl) "Enter a valid URL" else null

                    if (validTitle && validUrl) {
                        viewModel.addResource(
                            title = title.trim(),
                            url = url.trim(),
                            category = category,
                            notes = notes.trim(),
                            courseName = courseName.trim()
                        )
                        viewModel.showAddResourceDialog = false
                    }
                },
                enabled = title.isNotBlank() && url.isNotBlank(),
                modifier = Modifier.testTag("save_resource_button")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = { viewModel.showAddResourceDialog = false }
            ) {
                Text("Cancel")
            }
        }
    )
}
