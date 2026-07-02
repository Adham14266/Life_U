package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.School
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
import com.example.data.local.CourseGrade
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel
import com.example.ui.validation.isNonBlank
import java.util.Locale

enum class ProfileSubTab {
    Overview, Grades
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf(viewModel.userName) }
    var tempUni by remember { mutableStateOf(viewModel.userUniversity) }
    var tempNameError by remember { mutableStateOf<String?>(null) }
    var tempUniError by remember { mutableStateOf<String?>(null) }

    val grades by viewModel.grades.collectAsState()
    var activeSubTab by remember { mutableStateOf(ProfileSubTab.Overview) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Profile Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceLow, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // High resolution student image
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer)
                ) {
                    AsyncImage(
                        model = "https://lh3.googleusercontent.com/aida-public/AB6AXuAkgLf4qN7Wz5RHGIrbPgN2_XM2DuyYkDATavqweaeei7y1a2n0bYnPUEeEh73c9OaiQeFg1umORKjDC0DcklFC-lIZNrDF4nh1hS_3J48NmGnby3vPcwQagnfpWmOimsWas4mXQjsU1PWqVZ_VKWFk1XJVAKHnDUWO5kCdebmBxlSsjESGkiExfILgMrVHFHG9qRLMKK-DD--y-vGzFm-__W2Cc_AybpNNrGeT0Ak_DrHJxe63G_WgMj3q_PLD3RF13oUX6d8uVLmI",
                        contentDescription = "Alex Johnson Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = viewModel.userName,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 20.sp),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = viewModel.userUniversity,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = {
                        tempName = viewModel.userName
                        tempUni = viewModel.userUniversity
                        showEditProfileDialog = true
                    },
                    modifier = Modifier
                        .size(40.dp)
                        .background(SurfaceNormal, CircleShape)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Profile", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sliding Sub-Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceNormal, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == ProfileSubTab.Overview) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = ProfileSubTab.Overview }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = if (activeSubTab == ProfileSubTab.Overview) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Overview",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == ProfileSubTab.Overview) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (activeSubTab == ProfileSubTab.Grades) MaterialTheme.colorScheme.primary else Color.Transparent)
                    .clickable { activeSubTab = ProfileSubTab.Grades }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Grade,
                        contentDescription = null,
                        tint = if (activeSubTab == ProfileSubTab.Grades) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Grades & GPA",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (activeSubTab == ProfileSubTab.Grades) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (activeSubTab == ProfileSubTab.Grades) {
            GradesAndGpaTab(viewModel = viewModel, grades = grades)
        } else {
            // Milestones Dean's List Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SecondaryGreenContainer.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                    .border(1.dp, SecondaryGreen.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(SecondaryGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Dean's List Milestone",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = SecondaryGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { viewModel.deansListProgress },
                        color = SecondaryGreen,
                        trackColor = Color.White.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "85% Progress. Keep up your GPA of ${viewModel.userGpa}!",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Academic performance Bento Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ProfileBentoCard(
                    title = "GPA",
                    value = viewModel.userGpa,
                    subText = "Top 5% of class",
                    icon = Icons.Default.Grade,
                    colorAccent = PrimaryBlue,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeSubTab = ProfileSubTab.Grades }
                )

                ProfileBentoCard(
                    title = "Study Hours",
                    value = viewModel.userStudyHours,
                    subText = "This semester",
                    icon = Icons.Default.Timer,
                    colorAccent = SecondaryGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings items List
            Text(
                text = "Settings",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, fontSize = 18.sp),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Dark Mode switch item
                SettingItemRow(
                    icon = Icons.Default.DarkMode,
                    title = "Dark Mode Toggle",
                    description = "Enable modern dark visual theme",
                    trailingContent = {
                        Switch(
                            checked = viewModel.isDarkTheme,
                            onCheckedChange = { viewModel.isDarkTheme = it }
                        )
                    }
                )

                // University Sync account status item
                SettingItemRow(
                    icon = Icons.Default.Sync,
                    title = "University Sync",
                    description = "Stanford central databases",
                    trailingContent = {
                        Box(
                            modifier = Modifier
                                .background(SecondaryGreen.copy(alpha = 0.1f), CircleShape)
                                .border(1.dp, SecondaryGreen.copy(alpha = 0.2f), CircleShape)
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "CONNECTED",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                color = SecondaryGreen
                            )
                        }
                    }
                )

                // Logout item
                SettingItemRow(
                    icon = Icons.Default.Logout,
                    title = "Log Out",
                    description = "Securely sign out of Life U",
                    modifier = Modifier.clickable {
                        viewModel.navigateTo(AppScreen.Login)
                    }
                )
            }
        }
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Student Profile", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = {
                            tempName = it
                            tempNameError = null
                        },
                        label = { Text("Student Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempNameError != null,
                        supportingText = tempNameError?.let { error -> { Text(error) } }
                    )

                    OutlinedTextField(
                        value = tempUni,
                        onValueChange = {
                            tempUni = it
                            tempUniError = null
                        },
                        label = { Text("University / College") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempUniError != null,
                        supportingText = tempUniError?.let { error -> { Text(error) } }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val validName = isNonBlank(tempName)
                    val validUni = isNonBlank(tempUni)

                    tempNameError = if (!validName) "Name is required" else null
                    tempUniError = if (!validUni) "University is required" else null

                    if (validName && validUni) {
                        viewModel.userName = tempName.trim()
                        viewModel.userUniversity = tempUni.trim()
                        showEditProfileDialog = false
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Add Grade Dialog
    if (viewModel.showAddGradeDialog) {
        var tempCourseName by remember { mutableStateOf("") }
        var tempGrade by remember { mutableStateOf("A") }
        var tempCredits by remember { mutableStateOf(3) }
        var tempTerm by remember { mutableStateOf("Winter 2026") }
        var tempCourseNameError by remember { mutableStateOf<String?>(null) }
        var tempTermError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { viewModel.showAddGradeDialog = false },
            title = { Text("Add Course Grade", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = tempCourseName,
                        onValueChange = {
                            tempCourseName = it
                            tempCourseNameError = null
                        },
                        label = { Text("Course Name / Code") },
                        placeholder = { Text("e.g. Intro to Economics") },
                        modifier = Modifier.fillMaxWidth(),
                        isError = tempCourseNameError != null,
                        supportingText = tempCourseNameError?.let { error -> { Text(error) } }
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Credit Hours", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        val creditsOptions = listOf(1, 2, 3, 4, 5)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            creditsOptions.forEach { credits ->
                                val isSelected = tempCredits == credits
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { tempCredits = credits }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "$credits",
                                        style = MaterialTheme.typography.labelMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Grade Earned", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        val gradeLetters = listOf("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F")
                        gradeLetters.chunked(4).forEach { rowList ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                rowList.forEach { letter ->
                                    val isSelected = tempGrade == letter
                                    val color = when {
                                        letter.startsWith("A") -> SecondaryGreen
                                        letter.startsWith("B") -> PrimaryBlue
                                        letter.startsWith("C") -> Color(0xFFFF9800)
                                        else -> ErrorRed
                                    }
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .background(
                                                if (isSelected) color else SurfaceNormal,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                1.dp,
                                                if (isSelected) color else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .clickable { tempGrade = letter }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = letter,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Term Suggestion", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        val termSuggestions = listOf("Fall 2025", "Winter 2026", "Spring 2026")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            termSuggestions.forEach { termOpt ->
                                val isSelected = tempTerm == termOpt
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.primary else SurfaceNormal,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { tempTerm = termOpt }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = termOpt,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = tempTerm,
                            onValueChange = {
                                tempTerm = it
                                tempTermError = null
                            },
                            label = { Text("Term (e.g. Fall 2025)") },
                            modifier = Modifier.fillMaxWidth(),
                            isError = tempTermError != null,
                            supportingText = tempTermError?.let { error -> { Text(error) } }
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val validCourse = isNonBlank(tempCourseName)
                        val validTerm = isNonBlank(tempTerm)

                        tempCourseNameError = if (!validCourse) "Course name is required" else null
                        tempTermError = if (!validTerm) "Term is required" else null

                        if (validCourse && validTerm) {
                            viewModel.addGrade(tempCourseName.trim(), tempGrade, tempCredits, tempTerm.trim())
                            viewModel.showAddGradeDialog = false
                        }
                    }
                ) {
                    Text("Save Grade")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.showAddGradeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesAndGpaTab(viewModel: MainViewModel, grades: List<CourseGrade>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero GPA Summary Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryBlue, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Cumulative GPA",
                        style = MaterialTheme.typography.labelLarge.copy(color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.userGpa,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val totalCredits = grades.sumOf { it.creditHours }
                    val totalCourses = grades.size
                    Text(
                        text = "$totalCourses courses • $totalCredits total credits",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.9f))
                    )
                }

                Button(
                    onClick = { viewModel.showAddGradeDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }

        if (grades.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.School,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    modifier = Modifier.size(64.dp)
                )
                Text(
                    text = "No Grades Recorded Yet",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = "Add your classes and grades to calculate cumulative and term GPA automatically!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
        } else {
            GpaTrendBarChart(grades = grades, viewModel = viewModel)

            // Group by term
            val gradesByTerm = grades.groupBy { it.term }
            
            gradesByTerm.forEach { (term, termGrades) ->
                // Calculate Term GPA
                var termPoints = 0.0
                var termCredits = 0
                for (g in termGrades) {
                    termPoints += viewModel.getGradePoints(g.gradeLetter) * g.creditHours
                    termCredits += g.creditHours
                }
                val termGpa = if (termCredits > 0) termPoints / termCredits else 0.0
                val formattedTermGpa = String.format(Locale.US, "%.2f", termGpa)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceLow)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Term Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = term,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            
                            Box(
                                modifier = Modifier
                                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "Term GPA: $formattedTermGpa • $termCredits Credits",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Individual Courses in Term
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            termGrades.forEach { grade ->
                                val gradeColor = when {
                                    grade.gradeLetter.startsWith("A") -> SecondaryGreen
                                    grade.gradeLetter.startsWith("B") -> PrimaryBlue
                                    grade.gradeLetter.startsWith("C") -> Color(0xFFFF9800)
                                    else -> ErrorRed
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceLowest, RoundedCornerShape(12.dp))
                                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Custom visual: left border accent
                                    Box(
                                        modifier = Modifier
                                            .width(4.dp)
                                            .height(36.dp)
                                            .background(gradeColor, RoundedCornerShape(2.dp))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = grade.courseName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = "${grade.creditHours} credit hours",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    // Grade Badge
                                    Box(
                                        modifier = Modifier
                                            .background(gradeColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                            .border(1.dp, gradeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Text(
                                            text = grade.gradeLetter,
                                            style = MaterialTheme.typography.titleMedium.copy(
                                                fontWeight = FontWeight.Black,
                                                color = gradeColor
                                            )
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    IconButton(
                                        onClick = { viewModel.deleteGrade(grade) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete course grade",
                                            tint = ErrorRed.copy(alpha = 0.8f),
                                            modifier = Modifier.size(16.dp)
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

@Composable
fun ProfileBentoCard(
    title: String,
    value: String,
    subText: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    colorAccent: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Icon(imageVector = icon, contentDescription = null, tint = colorAccent, modifier = Modifier.size(18.dp))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.displayLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.ExtraBold),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SettingItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceLowest, RoundedCornerShape(16.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SurfaceLow, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (trailingContent != null) {
            trailingContent()
        } else {
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}

// Custom GPA trend data model and Recharts-style Interactive Bar Chart
data class TermGpaData(
    val term: String,
    val gpa: Double,
    val credits: Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpaTrendBarChart(grades: List<CourseGrade>, viewModel: MainViewModel) {
    val gradesByTerm = grades.groupBy { it.term }
    val rawTermData = gradesByTerm.map { (term, termGrades) ->
        var termPoints = 0.0
        var termCredits = 0
        for (g in termGrades) {
            termPoints += viewModel.getGradePoints(g.gradeLetter) * g.creditHours
            termCredits += g.creditHours
        }
        val gpa = if (termCredits > 0) termPoints / termCredits else 0.0
        TermGpaData(term = term, gpa = gpa, credits = termCredits)
    }

    // Chronological custom sorting helper
    val termData = rawTermData.sortedWith { a, b ->
        val aParts = a.term.split(" ")
        val bParts = b.term.split(" ")
        if (aParts.size == 2 && bParts.size == 2) {
            val aYear = aParts[1].toIntOrNull() ?: 0
            val bYear = bParts[1].toIntOrNull() ?: 0
            if (aYear != bYear) {
                aYear.compareTo(bYear)
            } else {
                val aSeason = aParts[0].lowercase()
                val bSeason = bParts[0].lowercase()
                val aSeasonWeight = when (aSeason) {
                    "fall" -> 1
                    "winter" -> 2
                    "spring" -> 3
                    "summer" -> 4
                    else -> 0
                }
                val bSeasonWeight = when (bSeason) {
                    "fall" -> 1
                    "winter" -> 2
                    "spring" -> 3
                    "summer" -> 4
                    else -> 0
                }
                aSeasonWeight.compareTo(bSeasonWeight)
            }
        } else {
            a.term.compareTo(b.term)
        }
    }

    if (termData.isEmpty()) return

    var selectedBarIndex by remember { mutableStateOf<Int?>(null) }
    val outlineVariantColor = MaterialTheme.colorScheme.outlineVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceLow, RoundedCornerShape(20.dp))
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "GPA Trend",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visual tracking of term-based GPAs",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedBarIndex == null) Color.Transparent else SecondaryGreen.copy(alpha = 0.12f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (selectedBarIndex == null) "Tap bar to inspect" else "Selected: ${termData[selectedBarIndex!!].term}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (selectedBarIndex == null) MaterialTheme.colorScheme.outline else SecondaryGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                val width = maxWidth
                val height = maxHeight
                
                Row(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(end = 8.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("4.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("3.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("2.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("1.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                        Text("0.00", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        val density = androidx.compose.ui.platform.LocalDensity.current
                        
                        Canvas(
                            modifier = Modifier
                                .fillMaxSize()
                                .pointerInput(termData) {
                                    detectTapGestures { offset ->
                                        val w = size.width.toFloat()
                                        val colW = w / termData.size
                                        var foundIndex: Int? = null
                                        for (i in termData.indices) {
                                            val hitLeft = i * colW
                                            val hitRight = hitLeft + colW
                                            if (offset.x in hitLeft..hitRight) {
                                                foundIndex = i
                                                break
                                            }
                                        }
                                        selectedBarIndex = if (selectedBarIndex == foundIndex) null else foundIndex
                                    }
                                }
                        ) {
                            val w = size.width
                            val h = size.height
                            val colW = w / termData.size
                            val barW = colW * 0.45f
                            
                            val gridLines = listOf(0.0f, 0.25f, 0.5f, 0.75f, 1.0f)
                            gridLines.forEach { ratio ->
                                val y = h * ratio
                                drawLine(
                                    color = outlineVariantColor.copy(alpha = 0.3f),
                                    start = Offset(0f, y),
                                    end = Offset(w, y),
                                    strokeWidth = 1.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }

                            for (i in termData.indices) {
                                val data = termData[i]
                                val isSelected = i == selectedBarIndex
                                val barHeight = (data.gpa / 4.0) * h
                                val finalBarHeight = if (barHeight <= 0) 4f else barHeight.toFloat()
                                
                                val barCenterX = (i * colW) + (colW / 2)
                                val actualBarWidth = minOf(barW, 36.dp.toPx())
                                val barLeft = barCenterX - (actualBarWidth / 2)
                                val barTop = h - finalBarHeight

                                val barBrush = if (selectedBarIndex == null) {
                                    Brush.verticalGradient(
                                        colors = listOf(PrimaryBlue, PrimaryBlue.copy(alpha = 0.6f))
                                    )
                                } else if (isSelected) {
                                    Brush.verticalGradient(
                                        colors = listOf(SecondaryGreen, SecondaryGreen.copy(alpha = 0.7f))
                                    )
                                } else {
                                    Brush.verticalGradient(
                                        colors = listOf(PrimaryBlue.copy(alpha = 0.25f), PrimaryBlue.copy(alpha = 0.1f))
                                    )
                                }

                                drawRoundRect(
                                    brush = barBrush,
                                    topLeft = Offset(barLeft, barTop),
                                    size = Size(actualBarWidth, finalBarHeight),
                                    cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                                )
                            }
                        }

                        if (selectedBarIndex != null && selectedBarIndex!! in termData.indices) {
                            val idx = selectedBarIndex!!
                            val data = termData[idx]
                            
                            val colW = width / termData.size.toFloat()
                            val barCenterX = (colW * idx) + (colW / 2f)
                            
                            val tooltipWidth = 130.dp
                            val tooltipHeight = 72.dp
                            
                            val tooltipOffsetDp = with(density) {
                                val xPx = barCenterX.toPx() - (tooltipWidth.toPx() / 2)
                                val maxX = width.toPx() - tooltipWidth.toPx()
                                val clampedXPx = xPx.coerceIn(0f, maxX)
                                clampedXPx.toDp()
                            }
                            
                            val barHeightDp = height * (data.gpa / 4.0).toFloat()
                            val barTopDp = height - barHeightDp
                            val tooltipYOffset = (barTopDp - tooltipHeight - 8.dp).coerceAtLeast(4.dp)

                            Card(
                                modifier = Modifier
                                    .offset(x = tooltipOffsetDp, y = tooltipYOffset)
                                    .size(width = tooltipWidth, height = tooltipHeight),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.inverseOnSurface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = data.term,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.inverseSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "GPA: ${String.format(Locale.US, "%.2f", data.gpa)}",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Black),
                                        color = SecondaryGreen
                                    )
                                    Text(
                                        text = "${data.credits} Credits",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 45.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                termData.forEachIndexed { index, data ->
                    val isSelected = index == selectedBarIndex
                    Text(
                        text = data.term,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = if (isSelected) SecondaryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedBarIndex = if (selectedBarIndex == index) null else index },
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}
