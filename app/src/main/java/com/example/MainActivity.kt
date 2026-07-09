package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            viewModel.scheduleWellnessReminder()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        checkAndRequestNotifications()

        setContent {
            MyApplicationTheme(darkTheme = viewModel.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (viewModel.currentScreen) {
                        AppScreen.Splash -> SplashScreen(viewModel)
                        AppScreen.OnboardingStage1 -> OnboardingScreen(viewModel, stage = 1)
                        AppScreen.OnboardingStage2 -> OnboardingScreen(viewModel, stage = 2)
                        AppScreen.OnboardingStage3 -> OnboardingScreen(viewModel, stage = 3)
                        AppScreen.OnboardingStage4 -> OnboardingScreen(viewModel, stage = 4)
                        AppScreen.Login -> LoginScreen(viewModel, isSignUpMode = false)
                        AppScreen.SignUp -> LoginScreen(viewModel, isSignUpMode = true)
                        AppScreen.ForgotPassword -> ForgotPasswordScreen(viewModel)
                        AppScreen.SubjectManagement -> SubjectManagementScreen(viewModel, onBack = { viewModel.navigateTo(AppScreen.Main) })
                        AppScreen.Main -> MainAppContainer(viewModel)
                    }
                }
            }
        }
    }

    private fun checkAndRequestNotifications() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                viewModel.scheduleWellnessReminder()
            }
        } else {
            viewModel.scheduleWellnessReminder()
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
