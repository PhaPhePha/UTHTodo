package com.uthtodo.app.domain.task

import com.uthtodo.app.domain.model.TodoTask

interface TaskRepository {
    fun observeTasks(userId: String, onChange: (List<TodoTask>) -> Unit, onError: (String) -> Unit): ListenerHandle
    fun saveTask(task: TodoTask, callback: (Result<TodoTask>) -> Unit)
    fun deleteTask(task: TodoTask, callback: (Result<Unit>) -> Unit)
    fun setCompleted(task: TodoTask, completed: Boolean, callback: (Result<Unit>) -> Unit)
}

fun interface ListenerHandle {
    fun remove()
}
