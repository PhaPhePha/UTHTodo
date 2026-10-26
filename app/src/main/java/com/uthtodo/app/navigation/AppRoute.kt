package com.uthtodo.app.navigation

/** Central place for app destinations so future screens can be added without coupling UI files. */
sealed class AppRoute(val route: String) {
    data object Welcome : AppRoute("welcome")
    data object Home : AppRoute("home")
}
