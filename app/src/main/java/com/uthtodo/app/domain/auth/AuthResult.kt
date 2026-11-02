package com.uthtodo.app.domain.auth

sealed interface AuthResult {
    data class Success(val user: AppUser) : AuthResult
    data class Failure(val message: String) : AuthResult
}
