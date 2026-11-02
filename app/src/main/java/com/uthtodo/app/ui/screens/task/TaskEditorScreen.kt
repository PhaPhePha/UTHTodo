package com.uthtodo.app.ui.screens.task

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.model.WelcomeBrand
import com.uthtodo.app.ui.components.formatDueAt
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditorScreen(
    userId: String,
    task: TodoTask?,
    onBack: () -> Unit,
    onSave: (TodoTask) -> Unit,
    onDelete: ((TodoTask) -> Unit)?
) {
    val context = LocalContext.current
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var description by remember(task?.id) { mutableStateOf(task?.description.orEmpty()) }
    var category by remember(task?.id) { mutableStateOf(task?.category ?: "Cá nhân") }
    var dueAt by remember(task?.id) { mutableLongStateOf(task?.dueAt ?: 0L) }
    var error by remember(task?.id) { mutableStateOf<String?>(null) }

    fun pickDate() {
        val calendar = Calendar.getInstance().apply { if (dueAt > 0) timeInMillis = dueAt }
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            dueAt = calendar.timeInMillis
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    fun pickTime() {
        val calendar = Calendar.getInstance().apply { if (dueAt > 0) timeInMillis = dueAt }
        TimePickerDialog(context, { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
            calendar.set(Calendar.SECOND, 0)
            dueAt = calendar.timeInMillis
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (task == null) "Thêm nhiệm vụ" else "Chỉnh sửa nhiệm vụ") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Quay lại") } },
                actions = {
                    val currentTask = task
                    val deleteAction = onDelete
                    if (currentTask != null && deleteAction != null) IconButton(onClick = { deleteAction(currentTask) }) { Icon(Icons.Outlined.Delete, "Xóa nhiệm vụ", tint = Color(0xFFB3261E)) }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(padding).verticalScroll(rememberScrollState()).padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            OutlinedTextField(title, { title = it; error = null }, Modifier.fillMaxWidth(), label = { Text("Tên nhiệm vụ") }, singleLine = true, shape = RoundedCornerShape(12.dp))
            OutlinedTextField(description, { description = it }, Modifier.fillMaxWidth(), label = { Text("Mô tả (không bắt buộc)") }, minLines = 3, shape = RoundedCornerShape(12.dp))
            Text("Danh mục", color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Học tập", "Cuộc sống", "Cá nhân").forEach { item ->
                    if (category == item) Button(onClick = { category = item }, colors = ButtonDefaults.buttonColors(containerColor = WelcomeBrand.blue), content = { Text(item) })
                    else OutlinedButton(onClick = { category = item }, content = { Text(item) })
                }
            }
            Text("Thời gian nhắc", color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = ::pickDate, modifier = Modifier.weight(1f)) { Text(if (dueAt == 0L) "Chọn ngày" else formatDueAt(dueAt).substringBefore(" •")) }
                OutlinedButton(onClick = ::pickTime, modifier = Modifier.weight(1f)) { Text(if (dueAt == 0L) "Chọn giờ" else formatDueAt(dueAt).substringAfter("• ").trim()) }
            }
            if (dueAt > 0L) TextButton(onClick = { dueAt = 0L }) { Text("Bỏ lịch nhắc", color = Color(0xFFB3261E)) }
            error?.let { Text(it, color = Color(0xFFB3261E), fontSize = 13.sp) }
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                if (title.trim().length < 2) {
                    error = "Tên nhiệm vụ cần có ít nhất 2 ký tự."
                } else {
                    onSave((task ?: TodoTask(userId = userId)).copy(title = title.trim(), description = description.trim(), category = category, dueAt = dueAt.takeIf { it > 0L }))
                }
            }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = WelcomeBrand.blue)) {
                Text("Lưu nhiệm vụ", fontWeight = FontWeight.Bold)
            }
        }
    }
}
