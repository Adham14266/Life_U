package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppContainer(viewModel: MainViewModel) {
    val urgentTasks by viewModel.urgentTasks.collectAsState()
    var showNotificationsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // High quality student avatar image from the HTML
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable { viewModel.selectTab(MainTab.Profile) }
                        ) {
                            AsyncImage(
                                model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAkgLf4qN7Wz5RHGIrbPgN2_XM2DuyYkDATavqweaeei7y1a2n0bYnPUEeEh73c9OaiQeFg1umORKjDC0DcklFC-lIZNrDF4nh1hS_3J48NmGnby3vPcwQagnfpWmOimsWas4mXQjsU1PWqVZ_VKWFk1XJVAKHnDUWO5kCdebmBxlSsjESGkiExfILgMrVHFHG9qRLMKK-DD--y-vGzFm-__W2Cc_AybpNNrGeT0Ak_DrHJxe63G_WgMj3q_PLD3RF13oUX6d8uVLmI",
                                contentDescription = "User Avatar",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column {
                            Text(
                                text = "Life U",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 18.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                            Text(
                                text = "Hi, ${viewModel.userName.split(" ").firstOrNull() ?: "Student"}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { showNotificationsDialog = true }) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            if (urgentTasks.isNotEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(ErrorRed, CircleShape)
                                        .align(Alignment.TopEnd)
                                )
                            }
                        }
                    }
                    IconButton(onClick = { viewModel.selectTab(MainTab.Tutor) }) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = "AI Tutor",
                            tint = if (viewModel.currentTab == MainTab.Tutor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                // Dashboard Tab
                NavigationBarItem(
                    selected = viewModel.currentTab == MainTab.Dashboard,
                    onClick = { viewModel.selectTab(MainTab.Dashboard) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == MainTab.Dashboard) Icons.Default.Dashboard else Icons.Outlined.Dashboard,
                            contentDescription = "Dashboard"
                        )
                    },
                    label = { Text("Home", style = MaterialTheme.typography.labelSmall) }
                )

                // Tutor Tab
                NavigationBarItem(
                    selected = viewModel.currentTab == MainTab.Tutor,
                    onClick = { viewModel.selectTab(MainTab.Tutor) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == MainTab.Tutor) Icons.Default.SmartToy else Icons.Outlined.SmartToy,
                            contentDescription = "Tutor"
                        )
                    },
                    label = { Text("Tutor", style = MaterialTheme.typography.labelSmall) }
                )

                // Schedule Tab
                NavigationBarItem(
                    selected = viewModel.currentTab == MainTab.Schedule,
                    onClick = { viewModel.selectTab(MainTab.Schedule) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == MainTab.Schedule) Icons.Default.CalendarMonth else Icons.Outlined.CalendarMonth,
                            contentDescription = "Schedule"
                        )
                    },
                    label = { Text("Schedule", style = MaterialTheme.typography.labelSmall) }
                )

                // Finances Tab
                NavigationBarItem(
                    selected = viewModel.currentTab == MainTab.Finances,
                    onClick = { viewModel.selectTab(MainTab.Finances) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == MainTab.Finances) Icons.Default.Payments else Icons.Outlined.Payments,
                            contentDescription = "Finances"
                        )
                    },
                    label = { Text("Finances", style = MaterialTheme.typography.labelSmall) }
                )

                // Profile Tab
                NavigationBarItem(
                    selected = viewModel.currentTab == MainTab.Profile,
                    onClick = { viewModel.selectTab(MainTab.Profile) },
                    icon = {
                        Icon(
                            imageVector = if (viewModel.currentTab == MainTab.Profile) Icons.Default.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", style = MaterialTheme.typography.labelSmall) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (viewModel.currentTab) {
                MainTab.Dashboard -> DashboardScreen(viewModel)
                MainTab.Tutor -> TutorScreen(viewModel)
                MainTab.Schedule -> ScheduleScreen(viewModel)
                MainTab.Finances -> FinancesScreen(viewModel)
                MainTab.Profile -> ProfileScreen(viewModel)
            }
        }
    }

    if (showNotificationsDialog) {
        AlertDialog(
            onDismissRequest = { showNotificationsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = if (urgentTasks.isNotEmpty()) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        tint = if (urgentTasks.isNotEmpty()) ErrorRed else PrimaryBlue,
                        contentDescription = null
                    )
                    Text(
                        text = "Assignment Alerts",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "These academic assignments are due within the next 24 hours.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (urgentTasks.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SecondaryGreen,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "All caught up!",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "No tasks due within the next 24 hours.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    } else {
                        // List urgent tasks
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            urgentTasks.forEach { task ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .border(1.dp, ErrorRed.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(ErrorRed.copy(alpha = 0.1f), CircleShape)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "DUE SOON",
                                                    style = MaterialTheme.typography.labelSmall.copy(
                                                        color = ErrorRed,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 8.sp
                                                    )
                                                )
                                            }
                                            if (task.category.isNotEmpty()) {
                                                Text(
                                                    text = task.category.uppercase(),
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = task.title,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Text(
                                            text = "Due: ${task.dueDate}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Button(
                                        onClick = {
                                            viewModel.toggleTaskCompletion(task)
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = SecondaryGreen,
                                            contentColor = Color.White
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(32.dp)
                                    ) {
                                        Text("Done", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                                    }
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotificationsDialog = false }) {
                    Text("Close")
                }
            }
        )
    }
}
