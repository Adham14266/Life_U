package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val urgentTasks by viewModel.urgentTasks.collectAsState()
    
    // Filter active (non-dismissed) urgent tasks
    val activeUrgentTasks = urgentTasks.filter { task ->
        !viewModel.dismissedUrgentTaskIds.contains(task.id)
    }

    var showPomodoroHubDialog by remember { mutableStateOf(false) }

    if (showPomodoroHubDialog) {
        PomodoroFocusHubDialog(viewModel = viewModel, onDismiss = { showPomodoroHubDialog = false })
    }

    if (viewModel.showSessionCompletedDialog) {
        SessionCompletedCelebrationDialog(viewModel = viewModel)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        if (activeUrgentTasks.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .background(ErrorRed.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                    .border(1.dp, ErrorRed.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(ErrorRed.copy(alpha = 0.15f), CircleShape)
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Alert",
                            tint = ErrorRed,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Assignment Deadline Alert",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                            color = ErrorRed
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (activeUrgentTasks.size == 1) {
                                "Your assignment \"${activeUrgentTasks.first().title}\" is due within 24 hours!"
                            } else {
                                "You have ${activeUrgentTasks.size} assignments due within 24 hours!"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    IconButton(
                        onClick = {
                            activeUrgentTasks.forEach { task ->
                                viewModel.dismissUrgentTask(task.id)
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Welcome Hero Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryBlue, PrimaryBlueContainer)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
                .padding(24.dp)
        ) {
            // Background blur circle
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .align(Alignment.TopEnd)
                    .offset(x = 30.dp, y = (-30).dp)
                    .blur(40.dp)
                    .alpha(0.2f)
                    .background(SecondaryGreenContainer, CircleShape)
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Exam in 5 days",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = OnPrimaryBlue
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Finals: Advanced Biology",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    ),
                    color = OnPrimaryBlue
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "You've covered 65% of the syllabus. Keep the momentum going!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnPrimaryBlue.copy(alpha = 0.9f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { viewModel.selectTab(MainTab.Tutor) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SecondaryGreenContainer,
                        contentColor = OnSecondaryContainer
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Focused Session", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Grid Bento Row: Focus Timer & Weekly Goal progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Interactive Pomodoro Timer Widget (Upgraded!)
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .background(SurfaceLowest, RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .clickable { showPomodoroHubDialog = true }
                    .padding(12.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (viewModel.pomodoroMode) {
                                MainViewModel.PomodoroMode.WORK -> "FOCUS"
                                MainViewModel.PomodoroMode.SHORT_BREAK -> "SHORT BREAK"
                                MainViewModel.PomodoroMode.LONG_BREAK -> "LONG BREAK"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = when (viewModel.pomodoroMode) {
                                MainViewModel.PomodoroMode.WORK -> PrimaryBlue
                                MainViewModel.PomodoroMode.SHORT_BREAK -> SecondaryGreen
                                MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryNavy
                            }
                        )
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Expand focus hub",
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Circular ring countdown
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(80.dp)
                    ) {
                        val minutes = viewModel.focusTimerLeftSeconds / 60
                        val seconds = viewModel.focusTimerLeftSeconds % 60
                        val timeStr = String.format("%02d:%02d", minutes, seconds)

                        val maxSeconds = when (viewModel.pomodoroMode) {
                            MainViewModel.PomodoroMode.WORK -> viewModel.workDurationMinutes * 60
                            MainViewModel.PomodoroMode.SHORT_BREAK -> viewModel.shortBreakMinutes * 60
                            MainViewModel.PomodoroMode.LONG_BREAK -> viewModel.longBreakMinutes * 60
                        }
                        val sweepPercent = if (maxSeconds > 0) viewModel.focusTimerLeftSeconds.toFloat() / maxSeconds else 1f
                        val activeColor = when (viewModel.pomodoroMode) {
                            MainViewModel.PomodoroMode.WORK -> PrimaryBlue
                            MainViewModel.PomodoroMode.SHORT_BREAK -> SecondaryGreen
                            MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryNavy
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            // Background track
                            drawCircle(
                                color = SurfaceNormal,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                            // Foreground active countdown ring
                            drawArc(
                                color = activeColor,
                                startAngle = -90f,
                                sweepAngle = sweepPercent * 360f,
                                useCenter = false,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
                        }

                        Text(
                            text = timeStr,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = viewModel.selectedStudySubject,
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                if (viewModel.isTimerRunning) {
                                    viewModel.pauseFocusTimer()
                                } else {
                                    viewModel.startFocusTimer()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (viewModel.isTimerRunning) ErrorRed else SecondaryGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                if (viewModel.isTimerRunning) "Pause" else "Start",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }

                        Button(
                            onClick = { viewModel.resetFocusTimer() },
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceNormal),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text(
                                "Reset",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            )
                        }
                    }
                }
            }

            // Stats progress card
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(SurfaceLow, RoundedCornerShape(20.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Weekly Goal",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${viewModel.userStudyHours} of 350 total study hours.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { 0.72f },
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = SurfaceNormal,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "72% Progress",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "7 hours left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Horizontal Subject Cards Catalog
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Your Subjects",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "See all",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { viewModel.selectTab(MainTab.Schedule) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            item {
                SubjectCatalogCard(
                    title = "Biology",
                    topicsLeft = 4,
                    progress = 0.65f,
                    icon = Icons.Default.Biotech,
                    colorBg = SecondaryGreenContainer.copy(alpha = 0.2f),
                    colorAccent = SecondaryGreen
                )
            }
            item {
                SubjectCatalogCard(
                    title = "Calculus",
                    topicsLeft = 2,
                    progress = 0.85f,
                    icon = Icons.Default.Functions,
                    colorBg = PrimaryBlue.copy(alpha = 0.1f),
                    colorAccent = PrimaryBlue
                )
            }
            item {
                SubjectCatalogCard(
                    title = "History",
                    topicsLeft = 8,
                    progress = 0.30f,
                    icon = Icons.Default.HistoryEdu,
                    colorBg = TertiaryContainer.copy(alpha = 0.15f),
                    colorAccent = TertiaryNavy
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Study Plan Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Study Plan",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "View All",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { viewModel.selectTab(MainTab.Schedule) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Today's classes
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            StudyPlanRowItem(
                subjectName = "Calculus III",
                timeRange = "10:00 AM - 11:30 AM",
                icon = Icons.Default.Functions,
                colorAccent = PrimaryBlue
            )
            StudyPlanRowItem(
                subjectName = "Organic Chem Lab",
                timeRange = "01:00 PM - 03:00 PM",
                icon = Icons.Default.Biotech,
                colorAccent = SecondaryGreen
            )
            StudyPlanRowItem(
                subjectName = "History Seminar",
                timeRange = "04:30 PM - 05:30 PM",
                icon = Icons.Default.HistoryEdu,
                colorAccent = TertiaryNavy
            )
        }
    }
}

@Composable
fun SubjectCatalogCard(
    title: String,
    topicsLeft: Int,
    progress: Float,
    icon: ImageVector,
    colorBg: Color,
    colorAccent: Color
) {
    Box(
        modifier = Modifier
            .width(160.dp)
            .background(colorBg, RoundedCornerShape(16.dp))
            .border(1.dp, colorAccent.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(colorAccent, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "$topicsLeft Topics left",
                style = MaterialTheme.typography.labelSmall,
                color = colorAccent
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = colorAccent,
                trackColor = Color.White.copy(alpha = 0.4f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
fun StudyPlanRowItem(
    subjectName: String,
    timeRange: String,
    icon: ImageVector,
    colorAccent: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(colorAccent.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colorAccent)
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subjectName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeRange,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroFocusHubDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val classesList by viewModel.classes.collectAsState()
    val notesList by viewModel.notes.collectAsState()
    
    val subjects = remember(classesList, notesList) {
        val list = mutableListOf<String>()
        classesList.forEach { if (it.name.isNotBlank() && !list.contains(it.name)) list.add(it.name) }
        notesList.forEach { if (it.courseName.isNotBlank() && !list.contains(it.courseName)) list.add(it.courseName) }
        listOf("Advanced Biology", "Intro to Economics", "General Study").forEach {
            if (!list.contains(it)) list.add(it)
        }
        list.distinct()
    }

    var showSettings by remember { mutableStateOf(false) }

    // Sliders state
    var tempWorkDuration by remember { mutableStateOf(viewModel.workDurationMinutes.toFloat()) }
    var tempShortBreak by remember { mutableStateOf(viewModel.shortBreakMinutes.toFloat()) }
    var tempLongBreak by remember { mutableStateOf(viewModel.longBreakMinutes.toFloat()) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Focus Hub",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Pomodoro Study Sessions",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Row {
                        IconButton(
                            onClick = { showSettings = !showSettings },
                            colors = IconButtonDefaults.iconButtonColors(
                                containerColor = if (showSettings) SurfaceNormal else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (showSettings) {
                    // Settings configuration Panel
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Timer Configurations",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Work duration slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Focus Duration", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${tempWorkDuration.toInt()} min", style = MaterialTheme.typography.bodyMedium, color = PrimaryBlue)
                            }
                            Slider(
                                value = tempWorkDuration,
                                onValueChange = { tempWorkDuration = it },
                                valueRange = 5f..60f,
                                steps = 11,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = PrimaryBlue,
                                    thumbColor = PrimaryBlue
                                )
                            )
                        }

                        // Short break duration slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Short Break Duration", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${tempShortBreak.toInt()} min", style = MaterialTheme.typography.bodyMedium, color = SecondaryGreen)
                            }
                            Slider(
                                value = tempShortBreak,
                                onValueChange = { tempShortBreak = it },
                                valueRange = 1f..20f,
                                steps = 19,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = SecondaryGreen,
                                    thumbColor = SecondaryGreen
                                )
                            )
                        }

                        // Long break duration slider
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Long Break Duration", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                Text("${tempLongBreak.toInt()} min", style = MaterialTheme.typography.bodyMedium, color = TertiaryNavy)
                            }
                            Slider(
                                value = tempLongBreak,
                                onValueChange = { tempLongBreak = it },
                                valueRange = 5f..45f,
                                steps = 8,
                                colors = SliderDefaults.colors(
                                    activeTrackColor = TertiaryNavy,
                                    thumbColor = TertiaryNavy
                                )
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = {
                                viewModel.updateDurations(
                                    tempWorkDuration.toInt(),
                                    tempShortBreak.toInt(),
                                    tempLongBreak.toInt()
                                )
                                showSettings = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
                        ) {
                            Text("Save Configurations", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                } else {
                    // Regular Mode Panel
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 1. Mode Pill Toggles
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceLow, RoundedCornerShape(16.dp))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            listOf(
                                MainViewModel.PomodoroMode.WORK to "Focus",
                                MainViewModel.PomodoroMode.SHORT_BREAK to "Short Break",
                                MainViewModel.PomodoroMode.LONG_BREAK to "Long Break"
                            ).forEach { (mode, label) ->
                                val isSelected = viewModel.pomodoroMode == mode
                                val accentColor = when (mode) {
                                    MainViewModel.PomodoroMode.WORK -> PrimaryBlue
                                    MainViewModel.PomodoroMode.SHORT_BREAK -> SecondaryGreen
                                    MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryNavy
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) accentColor else Color.Transparent)
                                        .clickable { viewModel.changePomodoroMode(mode) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium
                                        ),
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // 2. Subject Selection
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "SUBJECT FOCUS CONTEXT",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(subjects) { subject ->
                                    val isSelected = viewModel.selectedStudySubject == subject
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = { viewModel.selectedStudySubject = subject },
                                        label = { Text(subject) },
                                        leadingIcon = if (isSelected) {
                                            {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        } else null,
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = PrimaryBlue.copy(alpha = 0.15f),
                                            selectedLabelColor = PrimaryBlue,
                                            selectedLeadingIconColor = PrimaryBlue
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3. Circular Ring countdown (Larger!)
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(150.dp)
                        ) {
                            val minutes = viewModel.focusTimerLeftSeconds / 60
                            val seconds = viewModel.focusTimerLeftSeconds % 60
                            val timeStr = String.format("%02d:%02d", minutes, seconds)
                            
                            val maxSeconds = when(viewModel.pomodoroMode) {
                                MainViewModel.PomodoroMode.WORK -> viewModel.workDurationMinutes * 60
                                MainViewModel.PomodoroMode.SHORT_BREAK -> viewModel.shortBreakMinutes * 60
                                MainViewModel.PomodoroMode.LONG_BREAK -> viewModel.longBreakMinutes * 60
                            }
                            val sweepPercent = if (maxSeconds > 0) viewModel.focusTimerLeftSeconds.toFloat() / maxSeconds else 1f
                            val activeColor = when (viewModel.pomodoroMode) {
                                MainViewModel.PomodoroMode.WORK -> PrimaryBlue
                                MainViewModel.PomodoroMode.SHORT_BREAK -> SecondaryGreen
                                MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryNavy
                            }

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(
                                    color = SurfaceNormal,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                                drawArc(
                                    color = activeColor,
                                    startAngle = -90f,
                                    sweepAngle = sweepPercent * 360f,
                                    useCenter = false,
                                    style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = timeStr,
                                    style = MaterialTheme.typography.headlineLarge.copy(
                                        fontWeight = FontWeight.Black,
                                        fontSize = 32.sp
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (viewModel.isTimerRunning) "RUNNING" else "PAUSED",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                                    color = if (viewModel.isTimerRunning) SecondaryGreen else ErrorRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (viewModel.pomodoroMode == MainViewModel.PomodoroMode.WORK) {
                                "Focusing on ${viewModel.selectedStudySubject}"
                            } else {
                                "Break Time - Recharge"
                            },
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 4. Timer Controls Row
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Reset
                            OutlinedButton(
                                onClick = { viewModel.resetFocusTimer() },
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Reset")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Reset")
                            }

                            // Start / Pause FAB-style button
                            Button(
                                onClick = {
                                    if (viewModel.isTimerRunning) {
                                        viewModel.pauseFocusTimer()
                                    } else {
                                        viewModel.startFocusTimer()
                                    }
                                },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (viewModel.isTimerRunning) ErrorRed else SecondaryGreen
                                ),
                                modifier = Modifier.height(48.dp)
                            ) {
                                Icon(
                                    imageVector = if (viewModel.isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause"
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (viewModel.isTimerRunning) "Pause" else "Start Session",
                                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 5. Completed Session Log
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "RECENT COMPLETED SESSIONS",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            if (viewModel.focusHistory.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceLow, RoundedCornerShape(12.dp))
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.outline
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "No focus intervals logged today yet.",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                        .background(SurfaceLow, RoundedCornerShape(16.dp))
                                        .padding(8.dp)
                                ) {
                                    LazyColumn(
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
                                        modifier = Modifier.fillMaxSize()
                                    ) {
                                        items(viewModel.focusHistory) { session ->
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White, RoundedCornerShape(10.dp))
                                                    .padding(10.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.CheckCircle,
                                                        contentDescription = null,
                                                        tint = SecondaryGreen,
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Column {
                                                        Text(
                                                            text = session.subject,
                                                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                                        )
                                                        Text(
                                                            text = session.timestamp,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = MaterialTheme.colorScheme.outline
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = "${session.durationMinutes} min",
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                                                    color = PrimaryBlue
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SessionCompletedCelebrationDialog(viewModel: MainViewModel) {
    AlertDialog(
        onDismissRequest = { viewModel.showSessionCompletedDialog = false },
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .background(SecondaryGreen.copy(alpha = 0.15f), CircleShape)
                        .padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = "Celebration",
                        tint = SecondaryGreen,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (viewModel.lastCompletedSessionType.contains("Session")) "Focus Session Complete!" else "Break Complete!",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = if (viewModel.lastCompletedSessionType.contains("Session")) {
                        "Outstanding dedication! You successfully completed your focus interval for ${viewModel.selectedStudySubject}."
                    } else {
                        "Your break is finished! You should feel refreshed and ready to conquer your next objective."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                if (viewModel.lastCompletedSessionType.contains("Session")) {
                    Box(
                        modifier = Modifier
                            .background(SurfaceLow, RoundedCornerShape(12.dp))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                tint = PrimaryBlue,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "+${viewModel.workDurationMinutes} mins added to study history!",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { viewModel.showSessionCompletedDialog = false },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryGreen)
            ) {
                Text("Let's Continue!", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    )
}
