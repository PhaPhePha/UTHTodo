package com.uthtodo.app.data.messaging

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.uthtodo.app.data.reminder.ReminderScheduler

class UthTodoMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        FirebaseFirestore.getInstance().collection("users").document(user.uid)
            .set(mapOf("fcmToken" to token), com.google.firebase.firestore.SetOptions.merge())
    }

    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: message.data["title"] ?: "UTH Todo"
        val body = message.notification?.body ?: message.data["body"] ?: "Bạn có thông báo mới."
        ReminderScheduler.showNotification(this, "$title: $body", message.data["taskId"].orEmpty())
    }
}
