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
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
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
import com.uthtodo.app.domain.model.WelcomeBrand

private data class HomeTask(val title: String, val category: String, val time: String, val done: Boolean)

@Composable
fun HomeScreen(user: AppUser, onProfileClick: () -> Unit, onLogout: () -> Unit) {
    val tasks = emptyList<HomeTask>()
    Scaffold(
        containerColor = Color(0xFFF8F8FC),
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Trang chủ") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.List, null) }, label = { Text("Danh sách") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Outlined.NotificationsNone, null) }, label = { Text("Thông báo") })
                NavigationBarItem(selected = false, onClick = onProfileClick, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("Cá nhân") })
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    InitialAvatar(user.displayName, onProfileClick)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Hôm nay", color = WelcomeBrand.muted, fontSize = 13.sp)
                        Text("Chào ${user.displayName}!", color = WelcomeBrand.text, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                    }
                    IconButton(onClick = {}) { Icon(Icons.Outlined.NotificationsNone, "Thông báo") }
                }
            }
            item {
                ProgressCard(completed = tasks.count { it.done }, total = tasks.size)
            }
            item {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Text("Việc hôm nay", color = WelcomeBrand.text, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                    Text("Xem tất cả", color = WelcomeBrand.blue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            items(tasks) { task -> TaskRow(task) }
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
private fun ProgressCard(completed: Int, total: Int) {
    val percent = if (total == 0) 0 else completed * 100 / total
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).background(WelcomeBrand.blue).padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text("Tiến độ hôm nay", color = Color.White.copy(alpha = .8f), fontSize = 13.sp)
            Text("$completed/$total việc", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Bold)
            Text(if (percent >= 50) "Đã hơn nửa chặng, cố lên!" else "Bắt đầu ngày mới nhé!", color = Color.White.copy(alpha = .85f), fontSize = 13.sp)
        }
        Box(Modifier.size(62.dp).clip(CircleShape).background(Color.White.copy(alpha = .2f)), contentAlignment = Alignment.Center) {
            Text("$percent%", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CategoryCard(title: String, subtitle: String, color: Color, modifier: Modifier) {
    Column(modifier.clip(RoundedCornerShape(18.dp)).background(Color.White).padding(14.dp)) {
        Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(alpha = .12f)), contentAlignment = Alignment.Center) {
            Icon(Icons.Outlined.List, null, tint = color, modifier = Modifier.size(19.dp))
        }
        Spacer(Modifier.height(12.dp))
        Text(title, color = WelcomeBrand.text, fontWeight = FontWeight.Bold)
        Text(subtitle, color = WelcomeBrand.muted, fontSize = 12.sp)
    }
}

@Composable
private fun TaskRow(task: HomeTask) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(Modifier.size(24.dp).clip(CircleShape).background(if (task.done) WelcomeBrand.blue else Color.Transparent), contentAlignment = Alignment.Center) {
            if (task.done) Icon(Icons.Outlined.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
            else Box(Modifier.size(22.dp).clip(CircleShape).background(Color.Transparent))
        }
        Spacer(Modifier.size(12.dp))
        Column(Modifier.weight(1f)) {
            Text(task.title, color = if (task.done) WelcomeBrand.muted else WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(task.category, color = WelcomeBrand.blue, fontSize = 12.sp)
                Text(task.time, color = WelcomeBrand.muted, fontSize = 12.sp)
            }
        }
        Text("›", color = WelcomeBrand.muted, fontSize = 24.sp)
    }
}
