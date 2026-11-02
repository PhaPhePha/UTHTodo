package com.uthtodo.app.ui.screens.task

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uthtodo.app.domain.model.TodoTask
import com.uthtodo.app.domain.task.ListenerHandle
import com.uthtodo.app.domain.task.TaskRepository
import com.uthtodo.app.data.reminder.ReminderScheduler

data class TaskUiState(
    val tasks: List<TodoTask> = emptyList(),
    val isLoading: Boolean = false,
    val hasLoadedOnce: Boolean = false,
    val errorMessage: String? = null,
    val operationMessage: String? = null
)

class TaskViewModel(context: Context, private val repository: TaskRepository) : ViewModel() {
    private val appContext = context.applicationContext
    var uiState by mutableStateOf(TaskUiState())
        private set

    private var listener: ListenerHandle? = null
    private var currentUserId: String? = null

    fun observeUser(userId: String) {
        if (currentUserId == userId && listener != null) return
        listener?.remove()
        currentUserId = userId
        uiState = TaskUiState(isLoading = true)
        listener = repository.observeTasks(userId, { tasks ->
            tasks.forEach { task ->
                if (task.completed) ReminderScheduler.cancel(appContext, task)
                else ReminderScheduler.schedule(appContext, task)
            }
            uiState = uiState.copy(tasks = tasks, isLoading = false, hasLoadedOnce = true, errorMessage = null)
        }, { error ->
            uiState = uiState.copy(isLoading = false, hasLoadedOnce = true, errorMessage = error)
        })
    }

    fun clearUser() {
        listener?.remove()
        listener = null
        currentUserId = null
        uiState = TaskUiState()
    }

    fun save(task: TodoTask, onDone: (Boolean) -> Unit = {}) {
        repository.saveTask(task) { result ->
            result.onSuccess { saved ->
                ReminderScheduler.cancel(appContext, saved)
                ReminderScheduler.schedule(appContext, saved)
                uiState = uiState.copy(operationMessage = if (task.id.isBlank()) "Đã thêm nhiệm vụ." else "Đã cập nhật nhiệm vụ.")
                onDone(true)
            }.onFailure {
                uiState = uiState.copy(errorMessage = it.localizedMessage ?: "Không thể lưu nhiệm vụ.")
                onDone(false)
            }
        }
    }

    fun delete(task: TodoTask, onDone: (Boolean) -> Unit = {}) {
        repository.deleteTask(task) { result ->
            result.onSuccess {
                ReminderScheduler.cancel(appContext, task)
                uiState = uiState.copy(operationMessage = "Đã xóa nhiệm vụ.")
                onDone(true)
            }.onFailure {
                uiState = uiState.copy(errorMessage = it.localizedMessage ?: "Không thể xóa nhiệm vụ.")
                onDone(false)
            }
        }
    }

    fun toggle(task: TodoTask) {
        repository.setCompleted(task, !task.completed) { result ->
            result.onSuccess {
                val updated = task.copy(completed = !task.completed)
                if (updated.completed) ReminderScheduler.cancel(appContext, updated)
                else ReminderScheduler.schedule(appContext, updated)
            }
            result.onFailure {
                uiState = uiState.copy(errorMessage = it.localizedMessage ?: "Không thể cập nhật nhiệm vụ.")
            }
        }
    }

    fun clearMessages() { uiState = uiState.copy(errorMessage = null, operationMessage = null) }

    override fun onCleared() {
        listener?.remove()
        super.onCleared()
    }

    class Factory(private val context: Context, private val repository: TaskRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = TaskViewModel(context, repository) as T
    }
}
