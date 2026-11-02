package com.uthtodo.app.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.auth.AppUser
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.model.WelcomeBrand
import com.uthtodo.app.ui.components.AppBottomNavigation
import com.uthtodo.app.ui.components.MainTab
import com.uthtodo.app.ui.components.TodoTaskCard

@Composable
fun FirestoreHomeScreen(
    user: AppUser,
    tasks: List<TodoTask>,
    isLoading: Boolean,
    errorMessage: String?,
    onTaskClick: (TodoTask) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onAddTask: () -> Unit,
    onOpenTasks: () -> Unit,
    onOpenNotifications: () -> Unit,
    onOpenProfile: () -> Unit
) {
    val todayTasks = tasks.sortedWith(compareBy<TodoTask> { it.completed }.thenBy { it.dueAt ?: Long.MAX_VALUE }).take(5)
    val completed = tasks.count { it.completed }
    val percent = if (tasks.isEmpty()) 0 else completed * 100 / tasks.size

    AppBottomNavigation(MainTab.HOME, {}, onOpenTasks, onAddTask, onOpenNotifications, onOpenProfile, showAddButton = true) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    InitialAvatar(user.displayName, onOpenProfile)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Hôm nay", color = WelcomeBrand.muted, fontSize = 13.sp)
                        Text("Chào ${user.displayName}!", color = WelcomeBrand.text, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = onOpenNotifications) { Icon(Icons.Outlined.NotificationsNone, "Thông báo") }
                }
            }
            item { ProgressCard(completed, tasks.size, percent) }
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    CategoryCard("Học tập", tasks.count { it.category == "Học tập" && !it.completed }, WelcomeBrand.blue, Modifier.weight(1f))
                    CategoryCard("Cuộc sống", tasks.count { it.category == "Cuộc sống" && !it.completed }, Color(0xFFE19A53), Modifier.weight(1f))
                }
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Việc của bạn", color = WelcomeBrand.text, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    androidx.compose.material3.TextButton(onClick = onOpenTasks) { Text("Xem tất cả", color = WelcomeBrand.blue, fontSize = 13.sp) }
                }
            }
            if (isLoading) {
                item { Box(Modifier.fillMaxWidth().padding(28.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = WelcomeBrand.blue) } }
            } else if (todayTasks.isEmpty()) {
                item { EmptyTasks(onAddTask) }
            } else {
                items(todayTasks, key = { it.id }) { task -> TodoTaskCard(task, { onTaskClick(task) }, { onToggleTask(task) }) }
            }
            errorMessage?.let { item { Text(it, color = Color(0xFFB3261E), fontSize = 13.sp) } }
            item { Spacer(Modifier.height(8.dp)) }
        }
    }
}

@Composable
private fun InitialAvatar(name: String, onClick: () -> Unit) {
    IconButton(onClick = onClick, modifier = Modifier.size(48.dp).clip(CircleShape).background(WelcomeBrand.paleBlue)) {
        Text(name.take(2).uppercase(), color = WelcomeBrand.blue, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ProgressCard(completed: Int, total: Int, percent: Int) {
    Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(WelcomeBrand.blue).padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("Tiến độ hiện tại", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
            Text("$completed/$total việc", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(if (percent >= 50) "Đã hơn nửa chặng, cố lên!" else "Bắt đầu ngày mới nhé!", color = Color.White.copy(alpha = .85f), fontSize = 13.sp)
        }
        Box(Modifier.size(62.dp).clip(CircleShape).background(Color.White.copy(alpha = .2f)), contentAlignment = Alignment.Center) {
            Text("$percent%", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CategoryCard(title: String, remaining: Int, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Color.White).padding(14.dp)) {
        Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.List, null, tint = color, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, color = WelcomeBrand.text, fontWeight = FontWeight.Bold)
        Text("$remaining việc còn lại", color = WelcomeBrand.muted, fontSize = 12.sp)
    }
}

@Composable
private fun EmptyTasks(onAddTask: () -> Unit) {
    Column(Modifier.fillMaxWidth().padding(vertical = 26.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Chưa có nhiệm vụ nào", color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
        androidx.compose.material3.TextButton(onClick = onAddTask) { Text("Thêm nhiệm vụ đầu tiên", color = WelcomeBrand.blue) }
    }
}
