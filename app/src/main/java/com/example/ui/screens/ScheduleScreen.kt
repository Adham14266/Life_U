package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel
import com.example.data.local.ClassEvent
import com.example.data.local.Task
import com.example.ui.validation.isNonBlank
import com.example.ui.validation.isValidDueDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(viewModel: MainViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val classes by viewModel.classes.collectAsState()
    val scrollState = rememberScrollState()

    var taskTitle by remember { mutableStateOf("") }
    var taskPriority by remember { mutableStateOf("High") }
    var taskCategory by remember { mutableStateOf("Calculus") }
    var taskDueDate by remember { mutableStateOf("") }
    var taskTitleError by remember { mutableStateOf<String?>(null) }
    var taskCategoryError by remember { mutableStateOf<String?>(null) }
    var taskDueDateError by remember { mutableStateOf<String?>(null) }

    // Edit task states
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var editTaskTitle by remember { mutableStateOf("") }
    var editTaskPriority by remember { mutableStateOf("High") }
    var editTaskCategory by remember { mutableStateOf("") }
    var editTaskDueDate by remember { mutableStateOf("") }
    var showEditTaskDialog by remember { mutableStateOf(false) }
    var editTaskTitleError by remember { mutableStateOf<String?>(null) }
    var editTaskCategoryError by remember { mutableStateOf<String?>(null) }
    var editTaskDueDateError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Section 1: Weekly Timetable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLow, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Timetable",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row {
                        Icon(imageVector = Icons.Default.ChevronLeft, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.width(16.dp))
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Grid layout for schedule
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Time axis Column
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Time", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("09:00", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(44.dp))
                        Text("11:00", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    // Mon Column
                    TimetableColumn(
                        day = "Mon",
                        isToday = true,
                        modifier = Modifier.weight(1.2f),
                        events = listOf(
                            TimetableEvent("Calc 101", "09:00", PrimaryBlue)
                        )
                    )

                    // Tue Column
                    TimetableColumn(
                        day = "Tue",
                        isToday = false,
                        modifier = Modifier.weight(1.2f),
                        events = listOf(
                            TimetableEvent("History", "11:00", TertiaryNavy)
                        )
                    )

                    // Wed Column
                    TimetableColumn(
                        day = "Wed",
                        isToday = false,
                        modifier = Modifier.weight(1.2f),
                        events = listOf(
                            TimetableEvent("Bio Lab", "09:00", SecondaryGreen)
                        )
                    )

                    // Thu Column
                    TimetableColumn(
                        day = "Thu",
                        isToday = false,
                        modifier = Modifier.weight(1.2f),
                        events = listOf(
                            TimetableEvent("History", "11:00", TertiaryNavy)
                        )
                    )

                    // Fri Column
                    TimetableColumn(
                        day = "Fri",
                        isToday = false,
                        modifier = Modifier.weight(1.2f),
                        events = listOf(
                            TimetableEvent("Calc 101", "09:00", PrimaryBlue)
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 2: Today's Tasks
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Today's Tasks",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(
                onClick = { viewModel.showAddTaskDialog = true },
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Task", tint = Color.White, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (tasks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No tasks left for today! Enjoy your free time.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.outline)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tasks.forEach { task ->
                    TaskRowItem(
                        task = task,
                        isUrgent = viewModel.isDueWithin24Hours(task.dueDate),
                        onToggle = { viewModel.toggleTaskCompletion(task) },
                        onEdit = {
                            editingTask = task
                            editTaskTitle = task.title
                            editTaskPriority = task.priority
                            editTaskCategory = task.category
                            editTaskDueDate = task.dueDate
                            showEditTaskDialog = true
                        },
                        onDelete = {
                            viewModel.deleteTask(task)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Section 3: Upcoming Exams
        Text(
            text = "Upcoming Exams",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Exam 1
            ExamCard(
                title = "Calculus Midterm",
                dateText = "OCT 12",
                daysLeftText = "3 days left",
                isUrgent = true,
                colorTheme = ErrorRed,
                colorBg = ErrorContainer
            )

            // Exam 2
            ExamCard(
                title = "Molecular Biology Final",
                dateText = "OCT 25",
                daysLeftText = "16 days left",
                isUrgent = false,
                colorTheme = SecondaryGreen,
                colorBg = SecondaryGreenContainer.copy(alpha = 0.2f)
            )
        }
    }

    // Add Task Dialog
    if (viewModel.showAddTaskDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.showAddTaskDialog = false },
            title = { Text("Create New Task", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = taskTitle,
                        onValueChange = {
                            taskTitle = it
                            taskTitleError = null
                        },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = taskTitleError != null,
                        supportingText = taskTitleError?.let { error -> { Text(error) } }
                    )

                    // Priority selector Row
                    Text("Priority", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("High", "Medium", "Low", "Done").forEach { prio ->
                            val isSelected = taskPriority == prio
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                        shape = CircleShape
                                    )
                                    .clickable { taskPriority = prio }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = prio,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    // Category Selector
                    OutlinedTextField(
                        value = taskCategory,
                        onValueChange = {
                            taskCategory = it
                            taskCategoryError = null
                        },
                        label = { Text("Course Category") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = taskCategoryError != null,
                        supportingText = taskCategoryError?.let { error -> { Text(error) } }
                    )

                    // Due Date Selector
                    OutlinedTextField(
                        value = taskDueDate,
                        onValueChange = {
                            taskDueDate = it
                            taskDueDateError = null
                        },
                        label = { Text("Due Date (e.g. Oct 15)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = taskDueDateError != null,
                        supportingText = taskDueDateError?.let { error -> { Text(error) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val validTitle = isNonBlank(taskTitle)
                    val validCategory = isNonBlank(taskCategory)
                    val validDueDate = isValidDueDate(taskDueDate)

                    taskTitleError = if (!validTitle) "Task title is required" else null
                    taskCategoryError = if (!validCategory) "Course category is required" else null
                    taskDueDateError = if (!validDueDate) "Use a short date like Oct 15 or today" else null

                    if (validTitle && validCategory && validDueDate) {
                        viewModel.addTask(taskTitle.trim(), taskPriority, taskCategory.trim(), taskDueDate.trim())
                        taskTitle = ""
                        taskDueDate = ""
                        viewModel.showAddTaskDialog = false
                    }
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddTaskDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Edit Task Dialog
    if (showEditTaskDialog && editingTask != null) {
        AlertDialog(
            onDismissRequest = { showEditTaskDialog = false },
            title = { Text("Edit Task", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = editTaskTitle,
                        onValueChange = {
                            editTaskTitle = it
                            editTaskTitleError = null
                        },
                        label = { Text("Task Title") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = editTaskTitleError != null,
                        supportingText = editTaskTitleError?.let { error -> { Text(error) } }
                    )

                    // Priority selector Row
                    Text("Priority", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("High", "Medium", "Low", "Done").forEach { prio ->
                            val isSelected = editTaskPriority == prio
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                        shape = CircleShape
                                    )
                                    .clickable { editTaskPriority = prio }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = prio,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    // Category Selector
                    OutlinedTextField(
                        value = editTaskCategory,
                        onValueChange = {
                            editTaskCategory = it
                            editTaskCategoryError = null
                        },
                        label = { Text("Course Category") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = editTaskCategoryError != null,
                        supportingText = editTaskCategoryError?.let { error -> { Text(error) } }
                    )

                    // Due Date Selector
                    OutlinedTextField(
                        value = editTaskDueDate,
                        onValueChange = {
                            editTaskDueDate = it
                            editTaskDueDateError = null
                        },
                        label = { Text("Due Date (e.g. Oct 15)") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = editTaskDueDateError != null,
                        supportingText = editTaskDueDateError?.let { error -> { Text(error) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val currentTask = editingTask
                    val validTitle = isNonBlank(editTaskTitle)
                    val validCategory = isNonBlank(editTaskCategory)
                    val validDueDate = isValidDueDate(editTaskDueDate)

                    editTaskTitleError = if (!validTitle) "Task title is required" else null
                    editTaskCategoryError = if (!validCategory) "Course category is required" else null
                    editTaskDueDateError = if (!validDueDate) "Use a short date like Oct 15 or today" else null

                    if (currentTask != null && validTitle && validCategory && validDueDate) {
                        viewModel.updateTask(
                            currentTask.copy(
                                title = editTaskTitle.trim(),
                                priority = editTaskPriority,
                                category = editTaskCategory.trim(),
                                dueDate = editTaskDueDate.trim(),
                                isCompleted = editTaskPriority == "Done"
                            )
                        )
                        showEditTaskDialog = false
                        editingTask = null
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditTaskDialog = false
                    editingTask = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TimetableColumn(
    day: String,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    events: List<TimetableEvent>
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isToday) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Time slot blocks (representing 09:00 and 11:00)
        // slot 09:00
        val event09 = events.find { it.time == "09:00" }
        if (event09 != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(event09.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .border(1.dp, event09.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event09.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = event09.color,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Spacer(modifier = Modifier.height(52.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        // slot 11:00
        val event11 = events.find { it.time == "11:00" }
        if (event11 != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(event11.color.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .border(1.dp, event11.color.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = event11.title,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp),
                    color = event11.color,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Spacer(modifier = Modifier.height(52.dp))
        }
    }
}

data class TimetableEvent(val title: String, val time: String, val color: Color)

@Composable
fun TaskRowItem(
    task: Task, 
    isUrgent: Boolean = false,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggle() },
            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                ),
                color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Course Category Chip if present
                if (task.category.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = task.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Custom priority chip
                val (chipColor, label) = when (task.priority) {
                    "High" -> Pair(ErrorRed, "HIGH")
                    "Medium" -> Pair(TertiaryNavy, "MEDIUM")
                    "Low" -> Pair(PrimaryBlue, "LOW")
                    else -> Pair(SecondaryGreen, "DONE")
                }

                Box(
                    modifier = Modifier
                        .background(chipColor.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                        color = chipColor
                    )
                }

                // Due date info
                if (task.dueDate.isNotEmpty()) {
                    val finalColor = if (isUrgent && !task.isCompleted) ErrorRed else MaterialTheme.colorScheme.outline
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = finalColor,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "Due: ${task.dueDate}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                fontWeight = if (isUrgent && !task.isCompleted) FontWeight.Bold else FontWeight.Normal
                            ),
                            color = finalColor
                        )
                        if (isUrgent && !task.isCompleted) {
                            Text(
                                text = "(Urgent)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = ErrorRed
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Task",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = ErrorRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ExamCard(
    title: String,
    dateText: String,
    daysLeftText: String,
    isUrgent: Boolean,
    colorTheme: Color,
    colorBg: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(colorBg, RoundedCornerShape(16.dp))
            .border(1.dp, colorTheme.copy(alpha = 0.15f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        // Exam calendar card
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(colorTheme, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = dateText.split(" ").firstOrNull() ?: "",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
                Text(
                    text = dateText.split(" ").getOrNull(1) ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = daysLeftText,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                color = colorTheme
            )
        }

        Box(
            modifier = Modifier
                .background(colorTheme.copy(alpha = 0.1f), CircleShape)
                .border(1.dp, colorTheme.copy(alpha = 0.2f), CircleShape)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = if (isUrgent) "URGENT" else "UPCOMING",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                color = colorTheme
            )
        }
    }
}
