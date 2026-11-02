package com.uthtodo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.model.WelcomeBrand
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoTaskCard(task: TodoTask, onClick: () -> Unit, onToggle: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)
            .clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onToggle, modifier = Modifier.size(42.dp)) {
            Box(
                Modifier.size(31.dp)
                    .clip(CircleShape)
                    .then(if (task.completed) Modifier.background(WelcomeBrand.blue) else Modifier.border(2.dp, Color(0xFFD4D6DE), CircleShape)),
                contentAlignment = Alignment.Center
            ) {
                if (task.completed) Icon(Icons.Outlined.Check, "Đã hoàn thành", tint = Color.White, modifier = Modifier.size(16.dp))
            }
        }
        Spacer(Modifier.size(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                color = if (task.completed) WelcomeBrand.muted else WelcomeBrand.text,
                fontWeight = if (task.completed) FontWeight.Normal else FontWeight.SemiBold,
                textDecoration = if (task.completed) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(task.category, color = WelcomeBrand.blue, fontSize = 12.sp, modifier = Modifier.background(WelcomeBrand.paleBlue, RoundedCornerShape(5.dp)).padding(horizontal = 7.dp, vertical = 2.dp))
                task.dueAt?.let { Text(formatDueAt(it), color = WelcomeBrand.muted, fontSize = 12.sp) }
            }
        }
        Text("›", color = WelcomeBrand.muted, fontSize = 24.sp)
    }
}

fun formatDueAt(millis: Long): String = SimpleDateFormat("dd/MM • HH:mm", Locale.getDefault()).format(Date(millis))
