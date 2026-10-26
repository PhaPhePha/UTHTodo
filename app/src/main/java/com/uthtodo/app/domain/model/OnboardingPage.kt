package com.uthtodo.app.domain.model

import androidx.compose.ui.graphics.Color

/** OOP model describing one onboarding state and its visual/content contract. */
sealed class OnboardingPage(
    val title: String,
    val description: String,
    val actionLabel: String,
    val illustration: Illustration
) {
    data object AllInOne : OnboardingPage(
        "Mọi việc, gọn trong một chỗ",
        "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
        "Tiếp tục",
        Illustration.Tasks
    )

    data object Categories : OnboardingPage(
        "Chia theo Học tập và Cuộc sống",
        "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần lo gì.",
        "Tiếp tục",
        Illustration.Categories
    )

    data object Reminder : OnboardingPage(
        "Nhắc đúng lúc",
        "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
        "Bắt đầu ngay",
        Illustration.Reminder
    )
}

enum class Illustration { Tasks, Categories, Reminder }

object WelcomeBrand {
    val blue = Color(0xFF3F5BE0)
    val paleBlue = Color(0xFFF0F2FC)
    val text = Color(0xFF171A27)
    val muted = Color(0xFF737582)
}
