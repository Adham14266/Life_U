package com.example.chang

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = "onboarding1"
            ) {
                composable("onboarding1") { OnboardingScreen1(navController) }
                composable("onboarding2") { OnboardingScreen2(navController) }
                composable("onboarding3") { OnboardingScreen3(navController) }
                composable("onboarding4") { OnboardingScreen4(navController) }
            }
        }
    }
}

// =============================================
// Reusable Dots Component
// =============================================
@Composable
fun OnboardingDots(activeDot: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..4) {
            if (i == activeDot) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xFF7C3AED))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFCBD5E1))
                )
            }
        }
    }
}

// =============================================
// Screen 1 - Track Your Spending
// =============================================
@Composable
fun OnboardingScreen1(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الأيقونة
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF38BDF8)),
            contentAlignment = Alignment.Center
        ) {
            Text("💳", fontSize = 32.sp)
        }

        // الصورة
        Image(
            painter = painterResource(id = R.drawable.onboarding),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // النصوص
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Track Your Spending",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Manage your budget effortlessly\nand know where your money goes",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // الـ Dots
        OnboardingDots(activeDot = 1)

        // الزرار
        Button(
            onClick = { navController.navigate("onboarding2") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
            Text("Next →", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Skip",
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =============================================
// Screen 2 - Plan Your Studies
// =============================================
@Composable
fun OnboardingScreen2(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الأيقونة
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFD946EF), Color(0xFF9333EA))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("📚", fontSize = 32.sp)
        }

        // الصورة
        Image(
            painter = painterResource(id = R.drawable.onboarding2),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // النصوص
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Plan Your Studies",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Organize your schedule and\nnever miss a deadline again",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // الـ Dots
        OnboardingDots(activeDot = 2)

        // الزرار
        Button(
            onClick = { navController.navigate("onboarding3") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
            Text("Next →", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Skip",
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =============================================
// Screen 3 - Deep Focus Sessions
// =============================================
@Composable
fun OnboardingScreen3(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الأيقونة
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF22C55E)),
            contentAlignment = Alignment.Center
        ) {
            Text("🎯", fontSize = 32.sp)
        }

        // الصورة
        Image(
            painter = painterResource(id = R.drawable.onboarding3),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // النصوص
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Deep Focus Sessions",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Stay productive with Pomodoro timers\nand distraction-free work",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // الـ Dots
        OnboardingDots(activeDot = 3)

        // الزرار
        Button(
            onClick = { navController.navigate("onboarding4") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED))
        ) {
            Text("Next →", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Text(
            text = "Skip",
            color = Color(0xFF64748B),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// =============================================
// Screen 4 - Connect & Compete
// =============================================
@Composable
fun OnboardingScreen4(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 24.dp, vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // الأيقونة
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFFFF6B35), Color(0xFFFF4500))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("👥", fontSize = 32.sp)
        }

        // الصورة
        Image(
            painter = painterResource(id = R.drawable.onboarding4),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(20.dp)),
            contentScale = ContentScale.Crop
        )

        // النصوص
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Connect & Compete",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Text(
                text = "Challenge friends and stay motivated\nwith leaderboards",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )
        }

        // الـ Dots
        OnboardingDots(activeDot = 4)

        // الزرار - Get Started (مش مربوط لشاشة دلوقتي)
        Button(
            onClick = { /* TODO: ربطه بالشاشة الرئيسية بعدين */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF7C3AED)
            )
        ) {
            Text(
                "Get Started →",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Spacer بدل الـ Skip في الشاشة الأخيرة
        Spacer(modifier = Modifier.height(8.dp))
    }
}
