package com.uthtodo.app.navigation

/** Central place for app destinations so future screens can be added without coupling UI files. */
sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Login : AppRoute("login")
    data object Home : AppRoute("home")
    data object Tasks : AppRoute("tasks")
    data object Notifications : AppRoute("notifications")
    data object Profile : AppRoute("profile")
    data object TaskEditor : AppRoute("task-editor")
    data object TaskDetail : AppRoute("task-detail")
}
