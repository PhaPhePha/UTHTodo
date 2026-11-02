package com.uthtodo.app.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.uthtodo.app.ui.screens.auth.AuthViewModel
import java.util.Calendar

@Composable
fun AccountProfileScreen(
    user: AppUser,
    tasks: List<TodoTask>,
    authViewModel: AuthViewModel,
    onHome: () -> Unit,
    onTasks: () -> Unit,
    onAddTask: () -> Unit,
    onNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val authState = authViewModel.uiState
    var name by remember(user.id) { mutableStateOf(user.displayName) }
    var editingName by remember(user.id) { mutableStateOf(false) }
    LaunchedEffect(authState.user?.displayName) {
        authState.user?.displayName?.let { name = it }
    }

    AppBottomNavigation(MainTab.PROFILE, onHome, onTasks, onAddTask, onNotifications, {}) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).padding(horizontal = 20.dp)) {
            Spacer(Modifier.height(24.dp))
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(user.displayName.take(2).uppercase(), color = WelcomeBrand.blue, fontSize = 25.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.size(88.dp).clip(CircleShape).background(WelcomeBrand.paleBlue).padding(top = 27.dp))
                Spacer(Modifier.height(14.dp))
                Text(user.email, color = WelcomeBrand.muted, fontSize = 14.sp)
            }
            Spacer(Modifier.height(24.dp))
            OutlinedButton(onClick = { editingName = true }, modifier = Modifier.fillMaxWidth().height(48.dp), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Outlined.Edit, null)
                Spacer(Modifier.size(8.dp))
                Text("Chỉnh sửa hồ sơ", fontWeight = FontWeight.SemiBold)
            }
            if (editingName) {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Tên hiển thị", color = WelcomeBrand.text, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), placeholder = { Text("Nhập tên của bạn") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { editingName = false }) { Text("Hủy") }
                            Button(onClick = { authViewModel.updateDisplayName(name); editingName = false }, enabled = !authState.isLoading) {
                                if (authState.isLoading) CircularProgressIndicator(Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                                else Text("Lưu")
                            }
                        }
                    }
                }
            }
            authState.errorMessage?.let { Text(it, color = Color(0xFFB3261E), fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp)) }
            authState.infoMessage?.let { Text(it, color = Color(0xFF3B8B66), fontSize = 13.sp, modifier = Modifier.padding(top = 8.dp)) }
            Spacer(Modifier.height(24.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatCard(tasks.count { it.completed }.toString(), "Việc đã xong", Modifier.weight(1f))
                StatCard(tasks.count { !it.completed }.toString(), "Đang chờ", Modifier.weight(1f))
                StatCard(if (tasks.isEmpty()) "0%" else "${tasks.count { it.completed } * 100 / tasks.size}%", "Hoàn thành", Modifier.weight(1f))
            }
            WeeklyProgressChart(tasks)
            Spacer(Modifier.weight(1f))
            Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFB5574F))) {
                Icon(Icons.Outlined.Logout, null)
                Spacer(Modifier.size(8.dp))
                Text("Đăng xuất", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(14.dp)).background(Color.White).padding(vertical = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = WelcomeBrand.text, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(label, color = WelcomeBrand.muted, fontSize = 11.sp)
    }
}

@Composable
private fun WeeklyProgressChart(tasks: List<TodoTask>) {
    val now = Calendar.getInstance()
    val dayValues = listOf(
        Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
        Calendar.THURSDAY, Calendar.FRIDAY, Calendar.SATURDAY, Calendar.SUNDAY
    ).map { day ->
        tasks.count { task ->
            task.completedAt?.let { millis ->
                Calendar.getInstance().apply { timeInMillis = millis }.let { completedAt ->
                    completedAt.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                        completedAt.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR) &&
                        completedAt.get(Calendar.DAY_OF_WEEK) == day
                }
            } ?: false
        }
    }
    val maxValue = dayValues.maxOrNull()?.coerceAtLeast(1) ?: 1
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("Tuần này", color = WelcomeBrand.text, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("Việc hoàn thành mỗi ngày", color = WelcomeBrand.muted, fontSize = 11.sp)
            }
            Spacer(Modifier.height(12.dp))
            Row(Modifier.fillMaxWidth().height(125.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.Bottom) {
                dayValues.forEachIndexed { index, value ->
                    Column(Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(value.toString(), color = WelcomeBrand.muted, fontSize = 10.sp)
                        Spacer(Modifier.height(4.dp))
                        androidx.compose.foundation.layout.Box(
                            Modifier.width(18.dp).height((80f * value / maxValue).coerceAtLeast(5f).dp)
                                .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                .background(if (value > 0) WelcomeBrand.blue else Color(0xFFDDE2F8))
                        )
                        Spacer(Modifier.height(5.dp))
                        Text(listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")[index], color = WelcomeBrand.muted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}
