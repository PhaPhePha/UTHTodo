package com.uthtodo.app.ui.screens.task

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.CalendarToday
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.model.WelcomeBrand
import com.uthtodo.app.ui.components.formatDueAt

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TaskDetailScreen(
    task: TodoTask,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleCompleted: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember(task.id) { mutableStateOf(false) }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Xóa nhiệm vụ?") },
            text = { Text("Nhiệm vụ này sẽ bị xóa vĩnh viễn khỏi tài khoản của bạn.") },
            confirmButton = {
                Button(onClick = { showDeleteConfirm = false; onDelete() }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB3261E))) {
                    Text("Xóa")
                }
            },
            dismissButton = { androidx.compose.material3.TextButton(onClick = { showDeleteConfirm = false }) { Text("Hủy") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết nhiệm vụ") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Quay lại") } },
                actions = { IconButton(onClick = { showDeleteConfirm = true }) { Icon(Icons.Outlined.Delete, "Xóa nhiệm vụ", tint = Color(0xFFB3261E)) } }
            )
        }
    ) { padding ->
        Column(
            Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text(task.category, color = WelcomeBrand.blue, fontSize = 13.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(WelcomeBrand.paleBlue, RoundedCornerShape(50)).padding(horizontal = 12.dp, vertical = 7.dp))
                Spacer(Modifier.weight(1f))
                Text(if (task.completed) "Đã hoàn thành" else "Đang thực hiện", color = if (task.completed) Color(0xFF3B8B66) else Color(0xFFB2782D), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(task.title, color = WelcomeBrand.text, fontSize = 27.sp, fontWeight = FontWeight.Bold, lineHeight = 32.sp)
            Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    task.dueAt?.let {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Outlined.CalendarToday, null, tint = WelcomeBrand.blue, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.size(10.dp))
                            Column {
                                Text("Hạn hoàn thành", color = WelcomeBrand.muted, fontSize = 12.sp)
                                Text(formatDueAt(it), color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.Schedule, null, tint = WelcomeBrand.blue, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.size(10.dp))
                        Column {
                            Text("Trạng thái", color = WelcomeBrand.muted, fontSize = 12.sp)
                            Text(if (task.completed) "Đã xong" else "Chưa hoàn thành", color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            if (task.description.isNotBlank()) {
                Text("Mô tả", color = WelcomeBrand.text, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(task.description, color = WelcomeBrand.muted, fontSize = 15.sp, lineHeight = 22.sp)
            }
            Spacer(Modifier.weight(1f))
            OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                Icon(Icons.Outlined.Edit, null)
                Spacer(Modifier.size(8.dp))
                Text("Chỉnh sửa nhiệm vụ", fontWeight = FontWeight.SemiBold)
            }
            Button(onClick = onToggleCompleted, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(14.dp), colors = ButtonDefaults.buttonColors(containerColor = if (task.completed) Color(0xFF5E6472) else WelcomeBrand.blue)) {
                Icon(Icons.Outlined.CheckCircle, null)
                Spacer(Modifier.size(10.dp))
                Text(if (task.completed) "Đánh dấu chưa hoàn thành" else "Đánh dấu đã hoàn thành", fontWeight = FontWeight.Bold)
            }
        }
    }
}
