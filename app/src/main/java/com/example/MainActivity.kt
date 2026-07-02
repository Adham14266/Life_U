package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                        AppScreen.Login -> LoginScreen(viewModel, isSignUpMode = false)
                        AppScreen.SignUp -> LoginScreen(viewModel, isSignUpMode = true)
                        AppScreen.Main -> MainAppContainer(viewModel)
                    }
                }
            }
        }
    }
}
