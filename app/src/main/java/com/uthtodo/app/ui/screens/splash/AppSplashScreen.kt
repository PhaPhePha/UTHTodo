package com.uthtodo.app.ui.screens.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.R
import com.uthtodo.app.domain.model.WelcomeBrand

@Composable
fun AppSplashScreen() {
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        progress.animateTo(1f, animationSpec = tween(900))
    }

    Box(Modifier.fillMaxSize().background(WelcomeBrand.blue), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.ic_uth_todo_logo), "UTH Todo", Modifier.size(88.dp))
            Spacer(Modifier.height(18.dp))
            androidx.compose.material3.Text("UTH Todo", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            androidx.compose.material3.Text("Việc học, việc nhà – gọn gàng mỗi ngày", color = Color.White.copy(alpha = .78f), fontSize = 13.sp)
            Spacer(Modifier.height(42.dp))
            Box(Modifier.size(width = 84.dp, height = 3.dp).clip(RoundedCornerShape(50)).background(Color.White.copy(alpha = .3f))) {
                Box(Modifier.fillMaxWidth(progress.value).height(3.dp).clip(RoundedCornerShape(50)).background(Color.White))
            }
        }
    }
}
