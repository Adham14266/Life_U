package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.data.local.Subject
import com.example.ui.components.getSubjectIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectManagementScreen(viewModel: MainViewModel, onBack: () -> Unit) {
    val subjects by viewModel.subjects.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Manage Subjects", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Subject")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Personalize your academic portfolio by managing your subjects here.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            if (subjects.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "No subjects added yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(subjects, key = { it.id }) { subject ->
                        AnimatedVisibility(
                            visible = true,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            SubjectItemCard(
                                subject = subject,
                                onDelete = { viewModel.deleteSubject(subject) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddSubjectDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color, icon ->
                viewModel.addSubject(name, color, icon)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun SubjectItemCard(subject: Subject, onDelete: () -> Unit) {
    val color = try { Color(subject.color.toColorInt()) } catch (_: Exception) { PrimaryBlue }

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceLowest),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color.copy(alpha = 0.1f), CircleShape)
                        .border(1.dp, color.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = getSubjectIcon(subject.icon),
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Active Course",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete",
                    tint = ErrorRed
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSubjectDialog(onDismiss: () -> Unit, onConfirm: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#3B82F6") }
    var selectedIcon by remember { mutableStateOf("School") }

    val colors = listOf("#3B82F6", "#10B981", "#6366F1", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899")
    val icons = listOf("School", "MenuBook", "Functions", "Biotech", "HistoryEdu", "Public", "Language")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Subject", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Subject Name") },
                    placeholder = { Text("e.g. Theoretical Physics") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Column {
                    Text("Select Theme Color", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        colors.forEach { hex ->
                            val color = Color(hex.toColorInt())
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (selectedColor == hex) 2.dp else 0.dp,
                                        color = if (selectedColor == hex) Color.White else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColor = hex }
                            )
                        }
                    }
                }

                Column {
                    Text("Select Icon", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        icons.take(4).forEach { iconName ->
                            IconButton(
                                onClick = { selectedIcon = iconName },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (selectedIcon == iconName) MaterialTheme.colorScheme.primaryContainer else SurfaceNormal,
                                        RoundedCornerShape(8.dp)
                                    )
                            ) {
                                Icon(
                                    imageVector = getSubjectIcon(iconName),
                                    contentDescription = null,
                                    tint = if (selectedIcon == iconName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onConfirm(name, selectedColor, selectedIcon) },
                enabled = name.isNotBlank()
            ) {
                Text("Add Subject")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
