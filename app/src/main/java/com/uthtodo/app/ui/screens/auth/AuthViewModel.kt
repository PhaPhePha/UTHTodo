package com.uthtodo.app.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.uthtodo.app.domain.auth.AppUser
import com.uthtodo.app.domain.auth.AuthRepository
import com.uthtodo.app.domain.auth.AuthResult

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null,
    val user: AppUser? = null
)

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {
    var uiState by mutableStateOf(AuthUiState(user = repository.currentUser()))
        private set

    fun updateEmail(value: String) { uiState = uiState.copy(email = value, errorMessage = null) }
    fun updatePassword(value: String) { uiState = uiState.copy(password = value, errorMessage = null) }
    fun updateConfirmPassword(value: String) { uiState = uiState.copy(confirmPassword = value, errorMessage = null) }
    fun toggleRegisterMode() {
        uiState = uiState.copy(
            isRegisterMode = !uiState.isRegisterMode,
            errorMessage = null,
            infoMessage = null,
            password = "",
            confirmPassword = ""
        )
    }

    fun signIn() {
        if (!validateCredentials()) return
        uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
        repository.signInWithEmail(uiState.email.trim(), uiState.password) { result -> finish(result) }
    }

    fun register() {
        if (!validateCredentials() || uiState.password != uiState.confirmPassword) {
            if (uiState.password != uiState.confirmPassword) {
                uiState = uiState.copy(errorMessage = "Mật khẩu xác nhận không khớp.")
            }
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
        repository.registerWithEmail(uiState.email.trim(), uiState.password) { result -> finish(result) }
    }

    fun resetPassword() {
        val email = uiState.email.trim()
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            uiState = uiState.copy(errorMessage = "Nhập email hợp lệ để nhận liên kết đặt lại mật khẩu.")
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
        repository.sendPasswordReset(email) { result ->
            uiState = uiState.copy(isLoading = false, infoMessage = when (result) {
                is AuthResult.Failure -> result.message
                is AuthResult.Success -> "Đã gửi email đặt lại mật khẩu."
            }) }
        }

    fun signInWithGoogle(idToken: String) {
        if (idToken.isBlank()) {
            uiState = uiState.copy(errorMessage = "Không nhận được thông tin từ Google.")
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
        repository.signInWithGoogleIdToken(idToken) { result -> finish(result) }
    }

    fun updateDisplayName(displayName: String) {
        val normalized = displayName.trim()
        if (normalized.length < 2) {
            uiState = uiState.copy(errorMessage = "Tên cần có ít nhất 2 ký tự.")
            return
        }
        uiState = uiState.copy(isLoading = true, errorMessage = null, infoMessage = null)
        repository.updateDisplayName(normalized) { result -> finish(result, "Đã cập nhật tên hiển thị.") }
    }

    fun signOut() {
        repository.signOut()
        uiState = uiState.copy(user = null, errorMessage = null, infoMessage = null)
    }

    fun clearMessages() { uiState = uiState.copy(errorMessage = null, infoMessage = null) }
    fun showError(message: String) { uiState = uiState.copy(isLoading = false, errorMessage = message) }

    private fun validateCredentials(): Boolean {
        val emailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(uiState.email.trim()).matches()
        return when {
            !emailValid -> {
                uiState = uiState.copy(errorMessage = "Vui lòng nhập email hợp lệ.")
                false
            }
            uiState.password.length < 6 -> {
                uiState = uiState.copy(errorMessage = "Mật khẩu cần có ít nhất 6 ký tự.")
                false
            }
            else -> true
        }
    }

    private fun finish(result: AuthResult, successMessage: String? = null) {
        uiState = when (result) {
            is AuthResult.Success -> uiState.copy(isLoading = false, user = result.user, infoMessage = successMessage)
            is AuthResult.Failure -> uiState.copy(isLoading = false, errorMessage = result.message)
        }
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AuthViewModel(repository) as T
    }
}
