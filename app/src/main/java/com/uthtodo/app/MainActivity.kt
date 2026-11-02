package com.uthtodo.app

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.uthtodo.app.data.auth.FirebaseAuthRepository
import com.uthtodo.app.domain.auth.AppUser
import com.uthtodo.app.navigation.AppRoute
import com.uthtodo.app.ui.screens.auth.AuthViewModel
import com.uthtodo.app.ui.screens.auth.LoginScreen
import com.uthtodo.app.ui.screens.home.HomeScreen
import com.uthtodo.app.ui.screens.profile.ProfileScreen
import com.uthtodo.app.ui.screens.welcome.WelcomeScreen
import com.uthtodo.app.ui.theme.UTHTodoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UTHTodoTheme {
                WorkflowAppNavigation()
            }
        }
    }
}
@Composable
private fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    val repository = remember { FirebaseAuthRepository(context.applicationContext) }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(repository))
    var activeUser by remember { mutableStateOf(authViewModel.uiState.user) }

    val googleLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
            }.onSuccess { account ->
                authViewModel.signInWithGoogle(account.idToken.orEmpty())
            }.onFailure {
                authViewModel.showError("Không thể đăng nhập bằng Google. Vui lòng thử lại.")
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = AppRoute.Welcome.route,
        modifier = Modifier.fillMaxSize()
    ) {
        composable(AppRoute.Welcome.route) {
            WelcomeScreen(onFinished = {
                navController.navigate(AppRoute.Login.route) {
                    popUpTo(AppRoute.Welcome.route) { inclusive = true }
                }
            })
        }
        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onGoogleSignIn = {
                    activity?.let { googleLauncher.launch(repository.googleSignInIntent(it)) }
                },
                onAuthenticated = { user ->
                    activeUser = user
                    Toast.makeText(
                        context,
                        "Đăng nhập thành công!",
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.navigate(AppRoute.Home.route) {
                        popUpTo(AppRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Home.route) {
            val user = activeUser ?: authViewModel.uiState.user ?: return@composable
            HomeScreen(
                user = user,
                onProfileClick = { navController.navigate(AppRoute.Profile.route) },
                onLogout = {
                    authViewModel.signOut()
                    activeUser = null
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Home.route) { inclusive = true }
                    }
                }
            )
        }
        composable(AppRoute.Profile.route) {
            val user = activeUser ?: authViewModel.uiState.user ?: return@composable
            ProfileScreen(
                user = user,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.signOut()
                    activeUser = null
                    navController.navigate(AppRoute.Login.route) {
                        popUpTo(AppRoute.Profile.route) { inclusive = true }
                    }
                }
            )
        }
    }
}
