package com.uthtodo.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.uthtodo.app.ui.theme.UTHTodoTheme
import com.uthtodo.app.navigation.AppRoute
import com.uthtodo.app.ui.screens.welcome.WelcomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTHTodoTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
private fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRoute.Welcome.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppRoute.Welcome.route) {
            WelcomeScreen(onFinished = { /* Home screen will be added here later. */ })
        }
    }
}
