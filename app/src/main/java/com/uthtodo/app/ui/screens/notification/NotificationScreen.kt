package com.uthtodo.app.ui.screens.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import java.util.Calendar

@Composable
fun NotificationScreen(
    tasks: List<TodoTask>,
    onTaskClick: (TodoTask) -> Unit,
    onAddTask: () -> Unit,
    onHome: () -> Unit,
    onTasks: () -> Unit,
    onProfile: () -> Unit
) {
    val today = Calendar.getInstance()
    val upcoming = tasks.filter { task -> task.dueAt?.let { isSameDay(it, today) } == true }.sortedBy { it.dueAt }
    AppBottomNavigation(MainTab.NOTIFICATIONS, onHome, onTasks, onAddTask, {}, onProfile) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).padding(horizontal = 20.dp)) {
            Text("Việc đến hạn hôm nay", color = WelcomeBrand.text, fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp))
            Text("Theo dõi các nhiệm vụ cần hoàn thành trong ngày.", color = WelcomeBrand.muted, fontSize = 14.sp, modifier = Modifier.padding(bottom = 18.dp))
            if (upcoming.isEmpty()) {
                Text("Hôm nay không có nhiệm vụ đến hạn.", color = WelcomeBrand.muted, fontSize = 14.sp)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 12.dp)) {
                    items(upcoming, key = { it.id }) { task -> TodoTaskCard(task, { onTaskClick(task) }, {}) }
                }
            }
        }
    }
}

private fun isSameDay(millis: Long, today: Calendar): Boolean {
    val date = Calendar.getInstance().apply { timeInMillis = millis }
    return date.get(Calendar.YEAR) == today.get(Calendar.YEAR) && date.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)
}
