package com.uthtodo.app

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.uthtodo.app.data.auth.FirebaseAuthRepository
import com.uthtodo.app.data.task.FirestoreTaskRepository
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.navigation.AppRoute
import com.uthtodo.app.ui.screens.auth.AuthViewModel
import com.uthtodo.app.ui.screens.auth.LoginScreen
import com.uthtodo.app.ui.screens.home.FirestoreHomeScreen
import com.uthtodo.app.ui.screens.notification.NotificationScreen
import com.uthtodo.app.ui.screens.profile.AccountProfileScreen
import com.uthtodo.app.ui.screens.task.TaskEditorScreen
import com.uthtodo.app.ui.screens.task.TaskDetailScreen
import com.uthtodo.app.ui.screens.task.TaskListScreen
import com.uthtodo.app.ui.screens.task.TaskViewModel
import com.uthtodo.app.ui.screens.welcome.WelcomeScreen
import com.uthtodo.app.ui.screens.splash.AppSplashScreen
import kotlinx.coroutines.delay

@Composable
fun WorkflowAppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val activity = context as? Activity
    val authRepository = remember { FirebaseAuthRepository(context.applicationContext) }
    val taskRepository = remember { FirestoreTaskRepository(context.applicationContext) }
    val authViewModel: AuthViewModel = viewModel(factory = AuthViewModel.Factory(authRepository))
    val taskViewModel: TaskViewModel = viewModel(factory = TaskViewModel.Factory(context, taskRepository))
    val authState = authViewModel.uiState
    val taskState = taskViewModel.uiState
    val currentUser = authState.user
    var editingTask by remember { mutableStateOf<TodoTask?>(null) }
    var minimumSplashTimeElapsed by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(900)
        minimumSplashTimeElapsed = true
    }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            runCatching {
                GoogleSignIn.getSignedInAccountFromIntent(result.data).getResult(ApiException::class.java)
            }.onSuccess { account -> authViewModel.signInWithGoogle(account.idToken.orEmpty()) }
                .onFailure { authViewModel.showError("Không thể đăng nhập bằng Google. Vui lòng thử lại.") }
        }
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { }

    LaunchedEffect(currentUser?.id) {
        if (currentUser == null) {
            taskViewModel.clearUser()
        } else {
            taskViewModel.observeUser(currentUser.id)
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                FirebaseFirestore.getInstance().collection("users").document(currentUser.id)
                    .set(mapOf("uid" to currentUser.id, "email" to currentUser.email, "displayName" to currentUser.displayName, "fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
    LaunchedEffect(taskState.operationMessage, taskState.errorMessage) {
        val message = taskState.operationMessage ?: taskState.errorMessage
        if (!message.isNullOrBlank()) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            delay(1200)
            taskViewModel.clearMessages()
        }
    }

    val dataReady = currentUser == null || taskState.hasLoadedOnce
    if (!minimumSplashTimeElapsed || !dataReady) {
        AppSplashScreen()
        return
    }

    val startDestination = remember { if (currentUser == null) AppRoute.Welcome.route else AppRoute.Home.route }
    NavHost(navController, startDestination = startDestination, modifier = Modifier.fillMaxSize()) {
        composable(AppRoute.Welcome.route) {
            WelcomeScreen(onFinished = {
                navController.navigate(AppRoute.Login.route) { popUpTo(AppRoute.Welcome.route) { inclusive = true } }
            }, includeSplash = false)
        }
        composable(AppRoute.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onGoogleSignIn = { activity?.let { googleLauncher.launch(authRepository.googleSignInIntent(it)) } },
                onAuthenticated = {
                    Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    navController.navigate(AppRoute.Home.route) { popUpTo(AppRoute.Login.route) { inclusive = true } }
                }
            )
        }
        composable(AppRoute.Home.route) {
            currentUser?.let { user ->
                FirestoreHomeScreen(user, taskState.tasks, taskState.isLoading, taskState.errorMessage,
                    onTaskClick = { editingTask = it; navController.navigate(AppRoute.TaskDetail.route) },
                    onToggleTask = taskViewModel::toggle,
                    onAddTask = { editingTask = null; navController.navigate(AppRoute.TaskEditor.route) },
                    onOpenTasks = { navController.navigate(AppRoute.Tasks.route) },
                    onOpenNotifications = { navController.navigate(AppRoute.Notifications.route) },
                    onOpenProfile = { navController.navigate(AppRoute.Profile.route) })
            }
        }
        composable(AppRoute.Tasks.route) {
            TaskListScreen(taskState.tasks,
                onTaskClick = { editingTask = it; navController.navigate(AppRoute.TaskDetail.route) },
                onToggleTask = taskViewModel::toggle,
                onAddTask = { editingTask = null; navController.navigate(AppRoute.TaskEditor.route) },
                onHome = { navController.navigate(AppRoute.Home.route) { popUpTo(AppRoute.Home.route) } },
                onNotifications = { navController.navigate(AppRoute.Notifications.route) },
                onProfile = { navController.navigate(AppRoute.Profile.route) })
        }
        composable(AppRoute.Notifications.route) {
            NotificationScreen(taskState.tasks,
                onTaskClick = { editingTask = it; navController.navigate(AppRoute.TaskDetail.route) },
                onAddTask = { editingTask = null; navController.navigate(AppRoute.TaskEditor.route) },
                onHome = { navController.navigate(AppRoute.Home.route) { popUpTo(AppRoute.Home.route) } },
                onTasks = { navController.navigate(AppRoute.Tasks.route) },
                onProfile = { navController.navigate(AppRoute.Profile.route) })
        }
        composable(AppRoute.Profile.route) {
            currentUser?.let { user ->
                AccountProfileScreen(user, taskState.tasks, authViewModel,
                    onHome = { navController.navigate(AppRoute.Home.route) { popUpTo(AppRoute.Home.route) } },
                    onTasks = { navController.navigate(AppRoute.Tasks.route) },
                    onAddTask = { editingTask = null; navController.navigate(AppRoute.TaskEditor.route) },
                    onNotifications = { navController.navigate(AppRoute.Notifications.route) },
                    onLogout = {
                        taskViewModel.clearUser()
                        authViewModel.signOut()
                        navController.navigate(AppRoute.Welcome.route) { popUpTo(0) { inclusive = true } }
                    })
            }
        }
        composable(AppRoute.TaskEditor.route) {
            currentUser?.let { user ->
                TaskEditorScreen(user.id, editingTask, onBack = { navController.popBackStack() },
                    onSave = { task -> taskViewModel.save(task) { if (it) navController.popBackStack() } },
                    onDelete = { task -> taskViewModel.delete(task) { if (it) navController.popBackStack() } })
            }
        }
        composable(AppRoute.TaskDetail.route) {
            val selected = editingTask?.let { item -> taskState.tasks.firstOrNull { it.id == item.id } } ?: editingTask
            selected?.let { task ->
                TaskDetailScreen(
                    task = task,
                    onBack = { navController.popBackStack() },
                    onEdit = { editingTask = task; navController.navigate(AppRoute.TaskEditor.route) },
                    onToggleCompleted = { taskViewModel.toggle(task) },
                    onDelete = { taskViewModel.delete(task) { if (it) navController.popBackStack() } }
                )
            }
        }
    }
}
