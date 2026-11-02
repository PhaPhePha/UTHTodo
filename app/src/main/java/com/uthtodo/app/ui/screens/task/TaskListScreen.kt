package com.uthtodo.app.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.model.WelcomeBrand
import com.uthtodo.app.ui.components.AppBottomNavigation
import com.uthtodo.app.ui.components.MainTab
import com.uthtodo.app.ui.components.TodoTaskCard

@Composable
fun TaskListScreen(
    tasks: List<TodoTask>,
    onTaskClick: (TodoTask) -> Unit,
    onToggleTask: (TodoTask) -> Unit,
    onAddTask: () -> Unit,
    onHome: () -> Unit,
    onNotifications: () -> Unit,
    onProfile: () -> Unit
) {
    AppBottomNavigation(MainTab.TASKS, onHome, {}, onAddTask, onNotifications, onProfile) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).padding(horizontal = 20.dp)) {
            Text("Danh sách nhiệm vụ", color = WelcomeBrand.text, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp, bottom = 18.dp))
            if (tasks.isEmpty()) {
                Text("Bạn chưa có nhiệm vụ nào. Nhấn dấu + để tạo nhiệm vụ mới.", color = WelcomeBrand.muted, fontSize = 14.sp)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
                    items(tasks.sortedWith(compareBy<TodoTask> { it.completed }.thenBy { it.dueAt ?: Long.MAX_VALUE }), key = { it.id }) { task ->
                        TodoTaskCard(task, { onTaskClick(task) }, { onToggleTask(task) })
                    }
                }
            }
        }
    }
}
