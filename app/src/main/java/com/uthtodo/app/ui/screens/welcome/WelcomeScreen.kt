package com.uthtodo.app.ui.screens.welcome

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.OnboardingPage
import com.uthtodo.app.domain.model.WelcomeBrand
import com.uthtodo.app.R
import com.uthtodo.app.ui.components.BrandMark
import com.uthtodo.app.ui.components.WelcomeIllustration
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(onFinished: () -> Unit, previewPage: Int? = null) {
    var showSplash by remember { androidx.compose.runtime.mutableStateOf(previewPage == null) }
    var pageIndex by remember { mutableIntStateOf(previewPage ?: 0) }
    val pages = listOf(OnboardingPage.AllInOne, OnboardingPage.Categories, OnboardingPage.Reminder)

    LaunchedEffect(previewPage) {
        if (previewPage == null) {
            delay(1200)
            showSplash = false
        }
    }

    if (showSplash) {
        WelcomeSplash()
        return
    }

    Box(Modifier.fillMaxSize().background(Color.White)) {
        AnimatedContent(
            targetState = pageIndex,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "welcome page"
        ) { index ->
            val page = pages[index]
            Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    BrandMark()
                    Spacer(Modifier.weight(1f))
                    Text("Bỏ qua", color = WelcomeBrand.muted, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(50.dp))

                WelcomeIllustration(page.illustration, Modifier.fillMaxWidth().height(220.dp))
                Spacer(Modifier.height(240.dp))
                Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(page.title, color = WelcomeBrand.text, fontSize = 30.sp,
                        lineHeight = 28.sp, fontWeight = FontWeight.Bold)
                    Text(page.description, color = WelcomeBrand.muted, fontSize = 16.sp,
                        lineHeight = 28.sp)
                }
                Spacer(Modifier.weight(1f))
                PageIndicator(pageIndex, pages.size)
                Spacer(Modifier.height(20.dp))
                Button(
                    onClick = { if (pageIndex < pages.lastIndex) pageIndex++ else onFinished() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WelcomeBrand.blue)
                ) { Text(page.actionLabel, fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
            }
        }
    }
}

@Composable
private fun WelcomeSplash() {
    val loadingProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        loadingProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200)
        )
    }

    Box(Modifier.fillMaxSize().background(WelcomeBrand.blue), contentAlignment = Alignment.Center) {
        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom, horizontalAlignment = Alignment.CenterHorizontally) {
            Image(painterResource(R.drawable.ic_uth_todo_logo), "UTH Todo", Modifier.size(86.dp))
            Spacer(Modifier.height(18.dp))
            Text("UTH Todo", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text("Việc học, việc nhà – gọn gàng mỗi ngày", color = Color.White.copy(alpha = .75f), fontSize = 13.sp)
            Spacer(Modifier.height(370.dp))
            Box(
                Modifier.size(width = 84.dp, height = 3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = .28f))
            ) {
                Box(
                    Modifier.fillMaxWidth(loadingProgress.value)
                        .height(3.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color.White)
                )
            }
            Spacer(Modifier.height(12.dp))
            Text("Phiên bản 2.1.0", color = Color.White.copy(alpha = .6f), fontSize = 11.sp)
        }
    }
}

@Composable
private fun PageIndicator(selected: Int, count: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { index ->
            Box(Modifier.size(if (index == selected) 18.dp else 6.dp, 6.dp)
                .clip(RoundedCornerShape(50)).background(if (index == selected) WelcomeBrand.blue else WelcomeBrand.muted.copy(alpha = .35f)))
        }
    }
}

@Preview(name = "Onboarding 1 - Mọi việc", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WelcomePageOnePreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeScreen(onFinished = {}, previewPage = 0)
    }
}

@Preview(name = "Onboarding 2 - Phân loại", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WelcomePageTwoPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeScreen(onFinished = {}, previewPage = 1)
    }
}

@Preview(name = "Onboarding 3 - Nhắc đúng lúc", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun WelcomePageThreePreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeScreen(onFinished = {}, previewPage = 2)
    }
}

@Preview(name = "Splash Start", showBackground = true, backgroundColor = 0xFF3F5BE0)
@Composable
private fun WelcomeSplashPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeSplash()
    }
}
