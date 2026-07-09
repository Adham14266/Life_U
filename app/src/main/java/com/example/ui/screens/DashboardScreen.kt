package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import com.example.ui.components.getSubjectIcon
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.util.Calendar

@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    val urgentTasks by viewModel.urgentTasks.collectAsState()
    val classesList by viewModel.classes.collectAsState()
    val subjectsList by viewModel.subjects.collectAsState()
    
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

    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingText = when {
        hour < 12 -> "Good Morning"
        hour < 17 -> "Good Afternoon"
        else -> "Good Evening"
    }
    val greetingEmoji = when {
        hour < 12 -> "☀️"
        hour < 17 -> "🌤️"
        else -> "🌙"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        var startAnimations by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(100)
            startAnimations = true
        }

        AnimatedVisibility(
            visible = activeUrgentTasks.isNotEmpty(),
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            if (activeUrgentTasks.isNotEmpty()) {
                val firstUrgentTask = activeUrgentTasks.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp)
                        .shadow(8.dp, RoundedCornerShape(24.dp), spotColor = ShadowColor)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(GradientPrimaryStart, GradientPrimaryEnd)
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .padding(24.dp)
                ) {
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
                                text = "Priority Task",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = OnPrimaryBlue
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = firstUrgentTask.title,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 24.sp
                            ),
                            color = OnPrimaryBlue
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Due: ${firstUrgentTask.dueDate} - Don't forget to submit on time!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnPrimaryBlue.copy(alpha = 0.9f)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { 
                                viewModel.selectedStudySubject = if (firstUrgentTask.category.isNotBlank()) firstUrgentTask.category else firstUrgentTask.title
                                showPomodoroHubDialog = true 
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(20.dp),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Start Focused Session", style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = ShadowColorGlow.copy(alpha = 0.08f))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhite,
                                GlassWhite.copy(alpha = 0.75f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhiteBorder,
                                GlassWhiteBorder.copy(alpha = 0.2f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
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
                                MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryViolet
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
                            MainViewModel.PomodoroMode.LONG_BREAK -> TertiaryViolet
                        }

                        Canvas(modifier = Modifier.fillMaxSize()) {
                            drawCircle(
                                color = SurfaceNormal,
                                style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                            )
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

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                if (viewModel.isTimerRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer()
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

            Box(
                modifier = Modifier
                    .weight(1f)
                    .shadow(8.dp, RoundedCornerShape(20.dp), spotColor = GradientNatureStart.copy(alpha = 0.05f))
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhite,
                                GlassWhite.copy(alpha = 0.75f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
                    .border(
                        1.dp,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                GlassWhiteBorder.copy(alpha = 0.8f),
                                GlassWhiteBorder.copy(alpha = 0.2f)
                            )
                        ),
                        RoundedCornerShape(20.dp)
                    )
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
                        text = "${viewModel.userStudyHours} of 40 weekly study hours.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    val progressPercent = (viewModel.userStudyHours.toFloatOrNull() ?: 0f) / 40f
                    LinearProgressIndicator(
                        progress = { progressPercent.coerceIn(0f, 1f) },
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
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.primaryContainer,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${(progressPercent * 100).toInt()}% Done",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }
                        val remaining = (40 - (viewModel.userStudyHours.toIntOrNull() ?: 0)).coerceAtLeast(0)
                        Text(
                            text = "${remaining}h left",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

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
                text = "Manage",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.SubjectManagement) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (subjectsList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .width(170.dp)
                            .height(140.dp)
                            .shadow(4.dp, RoundedCornerShape(20.dp), spotColor = ShadowColorLight)
                            .background(SurfaceLowest, RoundedCornerShape(20.dp))
                            .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(20.dp))
                            .clickable { viewModel.navigateTo(com.example.ui.viewmodel.AppScreen.SubjectManagement) }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Add Subject",
                                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            } else {
                items(subjectsList) { subject ->
                    val colorAccent = try { Color(subject.color.toColorInt()) } catch (_: Exception) { MaterialTheme.colorScheme.primary }
                    SubjectCatalogCard(
                        title = subject.name,
                        topicsLeft = 0,
                        progress = 0f,
                        icon = getSubjectIcon(subject.icon),
                        colorBg = colorAccent.copy(alpha = 0.2f),
                        colorAccent = colorAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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

        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (classesList.isEmpty()) {
                StudyPlanRowItem(
                    subjectName = "No classes today",
                    timeRange = "Enjoy your free time!",
                    icon = Icons.Default.Info,
                    colorAccent = MaterialTheme.colorScheme.outline
                )
            } else {
                classesList.take(3).forEach { classEvent ->
                    StudyPlanRowItem(
                        subjectName = classEvent.name,
                        timeRange = classEvent.timeRange,
                        icon = Icons.Default.Book,
                        colorAccent = PrimaryBlue
                    )
                }
            }
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
            .width(170.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = ShadowColorLight)
            .background(SurfaceLowest, RoundedCornerShape(20.dp))
            .border(1.dp, colorAccent.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(colorAccent, colorAccent.copy(alpha = 0.7f))
                        ),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "$topicsLeft Topics left",
                style = MaterialTheme.typography.labelSmall,
                color = colorAccent
            )

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = { progress },
                color = colorAccent,
                trackColor = colorAccent.copy(alpha = 0.12f),
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
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = ShadowColorLight)
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(colorAccent.copy(alpha = 0.12f), colorAccent.copy(alpha = 0.06f))
                    ),
                    RoundedCornerShape(14.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = colorAccent, modifier = Modifier.size(22.dp))
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = subjectName,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = timeRange,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PomodoroFocusHubDialog(viewModel: MainViewModel, onDismiss: () -> Unit) {
    val subjectsList by viewModel.subjects.collectAsState()
    val userEmail = viewModel.currentUser?.email ?: ""
    
    val subjects = remember(subjectsList) {
        if (subjectsList.isEmpty()) listOf(com.example.data.local.Subject(name = "General Study", userEmail = userEmail)) else subjectsList
    }

    var showSettings by remember { mutableStateOf(false) }

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
                    }

                    Row {
                        IconButton(onClick = { showSettings = !showSettings }) {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.outline)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (showSettings) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("Timer Configurations", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)

                        Column {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Focus Duration")
                                Text("${tempWorkDuration.toInt()} min", color = PrimaryBlue)
                            }
                            Slider(value = tempWorkDuration, onValueChange = { tempWorkDuration = it }, valueRange = 5f..60f)
                        }

                        Button(
                            onClick = {
                                viewModel.updateDurations(tempWorkDuration.toInt(), tempShortBreak.toInt(), tempLongBreak.toInt())
                                showSettings = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save")
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(SurfaceLow, RoundedCornerShape(16.dp))
                                .padding(4.dp)
                        ) {
                            listOf(MainViewModel.PomodoroMode.WORK to "Focus", MainViewModel.PomodoroMode.SHORT_BREAK to "Short", MainViewModel.PomodoroMode.LONG_BREAK to "Long").forEach { (mode, label) ->
                                val isSelected = viewModel.pomodoroMode == mode
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) PrimaryBlue else Color.Transparent)
                                        .clickable { viewModel.changePomodoroMode(mode) }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = label, color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(150.dp)) {
                            val minutes = viewModel.focusTimerLeftSeconds / 60
                            val seconds = viewModel.focusTimerLeftSeconds % 60
                            val timeStr = String.format("%02d:%02d", minutes, seconds)
                            
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                drawCircle(color = SurfaceNormal, style = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round))
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = timeStr, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black))
                                Text(text = if (viewModel.isTimerRunning) "RUNNING" else "PAUSED", style = MaterialTheme.typography.labelSmall, color = if (viewModel.isTimerRunning) SecondaryGreen else ErrorRed)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(text = "Focusing on ${viewModel.selectedStudySubject}", fontWeight = FontWeight.Bold)

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            OutlinedButton(onClick = { viewModel.resetFocusTimer() }) { Text("Reset") }
                            Button(
                                onClick = { if (viewModel.isTimerRunning) viewModel.pauseFocusTimer() else viewModel.startFocusTimer() },
                                colors = ButtonDefaults.buttonColors(containerColor = if (viewModel.isTimerRunning) ErrorRed else SecondaryGreen)
                            ) {
                                Text(if (viewModel.isTimerRunning) "Pause" else "Start Session")
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
        title = { Text("Focus Session Complete!", fontWeight = FontWeight.Bold) },
        text = { Text("Outstanding dedication! You successfully completed your focus interval.") },
        confirmButton = {
            Button(onClick = { viewModel.showSessionCompletedDialog = false }) {
                Text("Let's Continue!")
            }
        }
    )
}
