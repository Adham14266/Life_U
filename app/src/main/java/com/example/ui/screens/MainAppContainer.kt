package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
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
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.surface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                                        )
                                    )
                                    .clickable { viewModel.selectTab(MainTab.Profile) }
                            ) {
                                if (viewModel.userAvatarUrl.isNotEmpty()) {
                                    AsyncImage(
                                        model = viewModel.userAvatarUrl,
                                        contentDescription = "User Avatar",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "User Avatar",
                                        tint = Color.White.copy(alpha = 0.8f),
                                        modifier = Modifier.fillMaxSize().padding(8.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "Life U",
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                )
                                Text(
                                    text = "Hi, ${viewModel.userName.split(" ").firstOrNull() ?: "Student"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { showNotificationsDialog = true }) {
                            Box(contentAlignment = Alignment.TopEnd) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.surfaceVariant,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Notifications,
                                        contentDescription = "Notifications",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                if (urgentTasks.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(AccentRose, CircleShape)
                                            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                                            .align(Alignment.TopEnd)
                                            .offset(x = 2.dp, y = (-2).dp)
                                    )
                                }
                            }
                        }
                        IconButton(onClick = { viewModel.selectTab(MainTab.Tutor) }) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (viewModel.currentTab == MainTab.Tutor)
                                            MaterialTheme.colorScheme.primaryContainer
                                        else
                                            MaterialTheme.colorScheme.surfaceVariant,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartToy,
                                    contentDescription = "AI Tutor",
                                    tint = if (viewModel.currentTab == MainTab.Tutor) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .shadow(16.dp, spotColor = ShadowColor)
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
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
                        label = { Text("Home", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
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
                        label = { Text("Tutor", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
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
                        label = { Text("Schedule", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
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
                        label = { Text("Finances", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
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
                        label = { Text("Profile", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            AnimatedContent(
                targetState = viewModel.currentTab,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "TabTransition"
            ) { targetTab ->
                when (targetTab) {
                    MainTab.Dashboard -> DashboardScreen(viewModel)
                    MainTab.Tutor -> TutorScreen(viewModel)
                    MainTab.Schedule -> ScheduleScreen(viewModel)
                    MainTab.Finances -> FinancesScreen(viewModel)
                    MainTab.Profile -> ProfileScreen(viewModel)
                }
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
