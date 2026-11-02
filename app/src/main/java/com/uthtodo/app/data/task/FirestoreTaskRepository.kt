package com.uthtodo.app.data.task

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.task.ListenerHandle
import com.uthtodo.app.domain.task.TaskRepository

class FirestoreTaskRepository(context: Context) : TaskRepository {
    private val firestore: FirebaseFirestore? = runCatching {
        val app = FirebaseApp.getApps(context.applicationContext).firstOrNull()
            ?: FirebaseApp.initializeApp(context.applicationContext)
        app?.let { FirebaseFirestore.getInstance(it) }
    }.getOrNull()

    override fun observeTasks(userId: String, onChange: (List<TodoTask>) -> Unit, onError: (String) -> Unit): ListenerHandle {
        val registration = firestore?.collection("users")?.document(userId)?.collection("tasks")
            ?.orderBy("dueAt", Query.Direction.ASCENDING)
            ?.addSnapshotListener { snapshot, error ->
                if (error != null) {
                    onError(error.message ?: "Không thể tải danh sách nhiệm vụ.")
                    return@addSnapshotListener
                }
                onChange(snapshot?.documents.orEmpty().mapNotNull { document ->
                    document.toTodoTask(userId)
                })
            }
        if (registration == null) onError("Firestore chưa được cấu hình.")
        return ListenerHandle { registration?.remove() }
    }

    override fun saveTask(task: TodoTask, callback: (Result<TodoTask>) -> Unit) {
        val database = firestore
        if (database == null) {
            callback(Result.failure(IllegalStateException("Firestore chưa được cấu hình.")))
            return
        }
        val id = task.id.ifBlank { database.collection("users").document(task.userId).collection("tasks").document().id }
        val saved = task.copy(id = id, updatedAt = System.currentTimeMillis())
        database.collection("users").document(saved.userId).collection("tasks").document(id)
            .set(saved.toMap())
            .addOnSuccessListener { callback(Result.success(saved)) }
            .addOnFailureListener { callback(Result.failure(it)) }
    }

    override fun deleteTask(task: TodoTask, callback: (Result<Unit>) -> Unit) {
        firestore?.collection("users")?.document(task.userId)?.collection("tasks")?.document(task.id)
            ?.delete()
            ?.addOnSuccessListener { callback(Result.success(Unit)) }
            ?.addOnFailureListener { callback(Result.failure(it)) }
            ?: callback(Result.failure(IllegalStateException("Firestore chưa được cấu hình.")))
    }

    override fun setCompleted(task: TodoTask, completed: Boolean, callback: (Result<Unit>) -> Unit) {
        firestore?.collection("users")?.document(task.userId)?.collection("tasks")?.document(task.id)
            ?.update("completed", completed, "completedAt", if (completed) System.currentTimeMillis() else null, "updatedAt", System.currentTimeMillis())
            ?.addOnSuccessListener { callback(Result.success(Unit)) }
            ?.addOnFailureListener { callback(Result.failure(it)) }
            ?: callback(Result.failure(IllegalStateException("Firestore chưa được cấu hình.")))
    }
}

private fun TodoTask.toMap(): Map<String, Any?> = mapOf(
    "userId" to userId,
    "title" to title,
    "description" to description,
    "category" to category,
    "dueAt" to dueAt,
    "completed" to completed,
    "completedAt" to completedAt,
    "createdAt" to createdAt,
    "updatedAt" to updatedAt
)

private fun com.google.firebase.firestore.DocumentSnapshot.toTodoTask(userId: String): TodoTask? {
    val title = getString("title") ?: return null
    return TodoTask(
        id = id,
        userId = getString("userId") ?: userId,
        title = title,
        description = getString("description").orEmpty(),
        category = getString("category") ?: "Cá nhân",
        dueAt = getLong("dueAt"),
        completed = getBoolean("completed") ?: false,
        completedAt = getLong("completedAt"),
        createdAt = getLong("createdAt") ?: System.currentTimeMillis(),
        updatedAt = getLong("updatedAt") ?: System.currentTimeMillis()
    )
}
