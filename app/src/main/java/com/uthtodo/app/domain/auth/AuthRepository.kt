package com.uthtodo.app.domain.auth

import android.app.Activity
import android.content.Intent

/** Abstraction boundary for authentication providers. */
interface AuthRepository {
    fun currentUser(): AppUser?
    fun signInWithEmail(email: String, password: String, callback: (AuthResult) -> Unit)
    fun registerWithEmail(email: String, password: String, callback: (AuthResult) -> Unit)
    fun sendPasswordReset(email: String, callback: (AuthResult) -> Unit)
    fun signInWithGoogleIdToken(idToken: String, callback: (AuthResult) -> Unit)
    fun googleSignInIntent(activity: Activity): Intent
    fun updateDisplayName(displayName: String, callback: (AuthResult) -> Unit)
    fun signOut()
}
