package com.uthtodo.app.ui.screens.profile

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.uthtodo.app.domain.model.WelcomeBrand

@Composable
fun ProfileScreen(user: AppUser, onBack: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFF8F8FC)).padding(20.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Outlined.ArrowBack, "Quay lại") }
            Text("Cá nhân", color = WelcomeBrand.text, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(26.dp))
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(user.displayName.take(2).uppercase(), color = WelcomeBrand.blue, fontSize = 25.sp, fontWeight = FontWeight.Bold,
                modifier = Modifier.size(88.dp).clip(CircleShape).background(WelcomeBrand.paleBlue).padding(top = 27.dp))
            Spacer(Modifier.height(14.dp))
            Text(user.displayName, color = WelcomeBrand.text, fontSize = 23.sp, fontWeight = FontWeight.Bold)
            Text(user.email, color = WelcomeBrand.muted, fontSize = 14.sp)
            Spacer(Modifier.height(16.dp))
            Text("✓  Đã liên kết tài khoản", color = Color(0xFF3B8B66), fontSize = 13.sp,
                modifier = Modifier.clip(RoundedCornerShape(50)).background(Color(0xFFE5F5EC)).padding(horizontal = 12.dp, vertical = 7.dp))
        }
        Spacer(Modifier.height(28.dp))
        Spacer(Modifier.weight(1f))
        Button(onClick = onLogout, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFFB5574F))) {
            Icon(Icons.Outlined.Logout, null)
            Spacer(Modifier.size(8.dp))
            Text("Đăng xuất", fontWeight = FontWeight.Bold)
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
