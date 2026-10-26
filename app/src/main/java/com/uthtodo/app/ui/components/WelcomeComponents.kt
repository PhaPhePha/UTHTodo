package com.uthtodo.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.uthtodo.app.R
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.domain.model.Illustration
import com.uthtodo.app.domain.model.WelcomeBrand

@Composable
fun BrandMark() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(painterResource(R.drawable.ic_uth_todo_app), "UTH Todo", Modifier.size(25.dp))
        Text("UTH ", Modifier.padding(start = 8.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WelcomeBrand.text)
        Text("Todo", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WelcomeBrand.blue)
    }
}

@Composable
fun WelcomeIllustration(type: Illustration, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Box(Modifier.size(174.dp).clip(RoundedCornerShape(24.dp)).background(WelcomeBrand.paleBlue)) {
            when (type) {
                Illustration.Tasks -> TaskArt()
                Illustration.Categories -> CategoryArt()
                Illustration.Reminder -> ReminderArt()
            }
        }
    }
}

@Preview(name = "Brand Mark", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun BrandMarkPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        BrandMark()
    }
}

@Preview(name = "Task Illustration", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun TaskIllustrationPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeIllustration(Illustration.Tasks, Modifier.size(220.dp))
    }
}

@Preview(name = "Categories Illustration", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun CategoriesIllustrationPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeIllustration(Illustration.Categories, Modifier.size(220.dp))
    }
}

@Preview(name = "Reminder Illustration", showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ReminderIllustrationPreview() {
    com.uthtodo.app.ui.theme.UTHTodoTheme {
        WelcomeIllustration(Illustration.Reminder, Modifier.size(220.dp))
    }
}

@Composable private fun TaskArt() {
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 24.dp), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(9.dp)) {
        repeat(3) { index ->
            Row(Modifier.fillMaxWidth().height(36.dp).clip(RoundedCornerShape(9.dp)).background(Color.White).padding(horizontal = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                TaskCheckbox(checked = index == 0)
                Spacer(Modifier.size(8.dp))
                Box(Modifier.size(if (index == 0) 72.dp else 62.dp, 6.dp).clip(RoundedCornerShape(50)).background(Color(0xFFE2E3E8)))
            }
        }
    }
}

@Composable
private fun TaskCheckbox(checked: Boolean) {
    Canvas(Modifier.size(15.dp)) {
        if (checked) {
            drawCircle(WelcomeBrand.blue)
            drawLine(Color.White, Offset(size.width * .24f, size.height * .52f), Offset(size.width * .44f, size.height * .72f), strokeWidth = 1.5.dp.toPx())
            drawLine(Color.White, Offset(size.width * .43f, size.height * .72f), Offset(size.width * .78f, size.height * .30f), strokeWidth = 1.5.dp.toPx())
        } else {
            drawCircle(Color(0xFFD9DADE), style = Stroke(width = 1.5.dp.toPx()))
        }
    }
}

@Composable private fun CategoryArt() {
    Column(Modifier.fillMaxSize().padding(18.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(10.dp)) {
        CategoryCard("Học tập", WelcomeBrand.blue)
        CategoryCard("Cuộc sống", Color(0xFFE7A966))
    }
}

@Composable private fun CategoryCard(label: String, color: Color) {
    Column(
        Modifier.fillMaxWidth()
            .heightIn(min = 54.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color.White)
            .padding(10.dp)
    ) {
        Box(Modifier.clip(RoundedCornerShape(12.dp)).background(color.copy(alpha = .12f)).padding(horizontal = 7.dp, vertical = 2.dp)) {
            Text(label, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(Modifier.height(7.dp))
        Box(Modifier.size(72.dp, 5.dp).clip(RoundedCornerShape(50)).background(Color(0xFFE5E6EA)))
    }
}

@Composable private fun ReminderArt() {
    Column(Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center)
    {
        Box(Modifier.size(54.dp).clip(RoundedCornerShape(50)).background(Color.White), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "Nhắc việc",
                tint = WelcomeBrand.blue,
                modifier = Modifier.size(50.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.clip(RoundedCornerShape(8.dp)).background(Color.White).padding(horizontal = 13.dp, vertical = 8.dp)) {
            Text("Ôn bài Toán lúc 19:00", color = WelcomeBrand.text, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
