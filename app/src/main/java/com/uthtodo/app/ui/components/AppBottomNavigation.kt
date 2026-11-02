package com.uthtodo.app.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import com.uthtodo.app.domain.model.WelcomeBrand

enum class MainTab { HOME, TASKS, NOTIFICATIONS, PROFILE }

@Composable
fun AppBottomNavigation(
    selected: MainTab,
    onHome: () -> Unit,
    onTasks: () -> Unit,
    onAdd: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit,
    showAddButton: Boolean = false,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected == MainTab.HOME, onHome, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Trang chủ") })
                NavigationBarItem(selected == MainTab.TASKS, onTasks, icon = { Icon(Icons.Outlined.List, null) }, label = { Text("Danh sách") })
                NavigationBarItem(selected == MainTab.NOTIFICATIONS, onNotifications, icon = { Icon(Icons.Outlined.NotificationsNone, null) }, label = { Text("Thông báo") })
                NavigationBarItem(selected == MainTab.PROFILE, onProfile, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Cá nhân") })
            }
        },
        floatingActionButton = {
            if (showAddButton) {
                FloatingActionButton(onClick = onAdd, containerColor = WelcomeBrand.blue, contentColor = Color.White) {
                    Text("+", fontSize = 28.sp)
                }
            }
        },
        floatingActionButtonPosition = androidx.compose.material3.FabPosition.Center
    ) { padding -> content(padding) }
}
