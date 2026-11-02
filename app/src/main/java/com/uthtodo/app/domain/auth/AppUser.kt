package com.uthtodo.app.domain.auth

/** Application-level user model. The UI does not depend on FirebaseUser. */
data class AppUser(
    val id: String,
    val displayName: String,
    val email: String,
    val photoUrl: String? = null
)
