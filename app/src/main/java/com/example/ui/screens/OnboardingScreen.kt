package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

@Composable
fun OnboardingScreen(viewModel: MainViewModel, stage: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Ambient background decorations
        Box(
            modifier = Modifier
                .size(240.dp)
                .align(Alignment.TopStart)
                .offset(x = (-50).dp, y = (-50).dp)
                .blur(80.dp)
                .alpha(0.06f)
                .background(PrimaryBlue, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(260.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = 60.dp)
                .blur(100.dp)
                .alpha(0.08f)
                .background(SecondaryGreen, CircleShape)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Header: Identity only, Skip button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "Life U Logo",
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Life U",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )
                }

                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { viewModel.navigateTo(AppScreen.Login) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Illustration Card (Asymmetric Bento style with subtle animations)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(SurfaceLow, SurfaceNormal)
                        ),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f), RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive inner decorations based on stage
                when (stage) {
                    1 -> OnboardingIllustration1()
                    2 -> OnboardingIllustration2()
                    3 -> OnboardingIllustration3()
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Text Content Section (staggered animations)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (stage) {
                    1 -> {
                        Text(
                            text = "Welcome to Life U",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Manage your studies, schedule, assignments, and daily tasks all in one place. Your academic journey, simplified.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                    2 -> {
                        Text(
                            text = "Learn with AI",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Upload your lecture files and ask the AI to explain topics, summarize lessons, generate quizzes, and create flashcards.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Modern chips pattern
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OnboardingChip(icon = Icons.Default.Summarize, label = "Summaries", color = PrimaryBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            OnboardingChip(icon = Icons.Default.Quiz, label = "Quizzes", color = SecondaryGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            OnboardingChip(icon = Icons.Default.Style, label = "Flashcards", color = PrimaryBlue)
                        }
                    }
                    3 -> {
                        Text(
                            text = "Track Your Progress",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Monitor your study hours, expenses, savings, habits, and personal goals with beautiful analytics in Life U.",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 24.sp
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Chips
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OnboardingChip(icon = Icons.Default.Timer, label = "Study Hours", color = PrimaryBlue)
                            Spacer(modifier = Modifier.width(8.dp))
                            OnboardingChip(icon = Icons.Default.Payments, label = "Expenses", color = TertiaryNavy)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OnboardingChip(icon = Icons.Default.TrendingUp, label = "Habits", color = SecondaryGreen, isOutline = true)
                            Spacer(modifier = Modifier.width(8.dp))
                            OnboardingChip(icon = Icons.Default.Stars, label = "Goals", color = TertiaryNavy)
                        }
                    }
                }
            }

            // Sticky Bottom navigation area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Step Indicator Dots
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 16.dp)
                ) {
                    StepDot(isActive = stage == 1, isExtended = stage == 1)
                    Spacer(modifier = Modifier.width(8.dp))
                    StepDot(isActive = stage == 2, isExtended = stage == 2)
                    Spacer(modifier = Modifier.width(8.dp))
                    StepDot(isActive = stage == 3, isExtended = stage == 3)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (stage > 1) Arrangement.SpaceBetween else Arrangement.Center
                ) {
                    if (stage > 1) {
                        Button(
                            onClick = {
                                when (stage) {
                                    2 -> viewModel.navigateTo(AppScreen.OnboardingStage1)
                                    3 -> viewModel.navigateTo(AppScreen.OnboardingStage2)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Back", style = MaterialTheme.typography.labelLarge)
                        }
                    }

                    Button(
                        onClick = {
                            when (stage) {
                                1 -> viewModel.navigateTo(AppScreen.OnboardingStage2)
                                2 -> viewModel.navigateTo(AppScreen.OnboardingStage3)
                                3 -> viewModel.navigateTo(AppScreen.Login)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(56.dp)
                            .fillMaxWidth(if (stage == 1) 1f else 0.65f),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Text(
                            text = if (stage == 3) "Get Started" else "Next",
                            style = MaterialTheme.typography.titleLarge.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Step $stage of 3: Personalized Setup",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun OnboardingIllustration1() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Study Smarter",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue)
        )
    }
}

@Composable
fun OnboardingIllustration2() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(SecondaryGreen.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SmartToy,
                contentDescription = null,
                tint = SecondaryGreen,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "AI-Driven Insights",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = SecondaryGreen)
        )
    }
}

@Composable
fun OnboardingIllustration3() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Timeline,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Track Every Milestone",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = PrimaryBlue)
        )
    }
}

@Composable
fun OnboardingChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, color: Color, isOutline: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                if (isOutline) Color.Transparent else color.copy(alpha = 0.1f),
                shape = CircleShape
            )
            .border(
                1.dp,
                if (isOutline) color.copy(alpha = 0.4f) else Color.Transparent,
                CircleShape
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge.copy(color = color, fontWeight = FontWeight.Bold))
    }
}

@Composable
fun StepDot(isActive: Boolean, isExtended: Boolean) {
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(if (isExtended) 24.dp else 8.dp)
            .background(
                color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = CircleShape
            )
    )
}
