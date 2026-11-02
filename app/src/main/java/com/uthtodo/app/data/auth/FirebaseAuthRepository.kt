package com.uthtodo.app.data.auth

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.uthtodo.app.R
import com.uthtodo.app.domain.auth.AppUser
import com.uthtodo.app.domain.auth.AuthRepository
import com.uthtodo.app.domain.auth.AuthResult

/** Firebase implementation kept behind AuthRepository for testability and clean UI code. */
class FirebaseAuthRepository(private val context: Context) : AuthRepository {
    private val appContext = context.applicationContext

    override fun currentUser(): AppUser? = firebaseAuthOrNull()?.currentUser?.toAppUser()

    override fun signInWithEmail(email: String, password: String, callback: (AuthResult) -> Unit) {
        val auth = firebaseAuthOrNull() ?: return callback(configFailure())
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(AuthResult.Success(it.user!!.toAppUser())) }
            .addOnFailureListener { callback(AuthResult.Failure(it.toVietnameseMessage())) }
    }

    override fun registerWithEmail(email: String, password: String, callback: (AuthResult) -> Unit) {
        val auth = firebaseAuthOrNull() ?: return callback(configFailure())
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { callback(AuthResult.Success(it.user!!.toAppUser())) }
            .addOnFailureListener { callback(AuthResult.Failure(it.toVietnameseMessage())) }
    }

    override fun sendPasswordReset(email: String, callback: (AuthResult) -> Unit) {
        val auth = firebaseAuthOrNull() ?: return callback(configFailure())
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                callback(AuthResult.Failure("Đã gửi email đặt lại mật khẩu. Hãy kiểm tra hộp thư."))
            }
            .addOnFailureListener { callback(AuthResult.Failure(it.toVietnameseMessage())) }
    }

    override fun signInWithGoogleIdToken(idToken: String, callback: (AuthResult) -> Unit) {
        val auth = firebaseAuthOrNull() ?: return callback(configFailure())
        auth.signInWithCredential(GoogleAuthProvider.getCredential(idToken, null))
            .addOnSuccessListener { callback(AuthResult.Success(it.user!!.toAppUser())) }
            .addOnFailureListener { callback(AuthResult.Failure(it.toVietnameseMessage())) }
    }

    override fun googleSignInIntent(activity: Activity): Intent {
        val options = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.google_web_client_id))
            .requestEmail()
            .build()
        return GoogleSignIn.getClient(activity, options).signInIntent
    }

    override fun updateDisplayName(displayName: String, callback: (AuthResult) -> Unit) {
        val app = firebaseAppOrNull()
        if (app == null) {
            callback(configFailure())
            return
        }
        val user = FirebaseAuth.getInstance(app).currentUser
        if (user == null) {
            callback(AuthResult.Failure("Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại."))
            return
        }
        user.updateProfile(UserProfileChangeRequest.Builder().setDisplayName(displayName).build())
            .addOnSuccessListener {
                val updated = user.toAppUser()
                FirebaseFirestore.getInstance(app).collection("users").document(user.uid)
                    .set(mapOf("uid" to user.uid, "displayName" to displayName, "email" to user.email.orEmpty()), com.google.firebase.firestore.SetOptions.merge())
                    .addOnCompleteListener { callback(AuthResult.Success(updated)) }
            }
            .addOnFailureListener { callback(AuthResult.Failure(it.toVietnameseMessage())) }
    }

    override fun signOut() {
        firebaseAuthOrNull()?.signOut()
    }

    private fun firebaseAuthOrNull(): FirebaseAuth? {
        return firebaseAppOrNull()?.let { FirebaseAuth.getInstance(it) }
    }

    private fun firebaseAppOrNull(): FirebaseApp? = runCatching {
        FirebaseApp.getApps(appContext).firstOrNull() ?: FirebaseApp.initializeApp(appContext)
    }.getOrNull()

    private fun configFailure() = AuthResult.Failure(
        "Firebase chưa được cấu hình. Hãy thêm google-services.json và bật Email/Password trong Firebase Console."
    )
}

private fun FirebaseUser.toAppUser(): AppUser = AppUser(
    id = uid,
    displayName = displayName?.takeIf { it.isNotBlank() } ?: email?.substringBefore("@") ?: "Bạn",
    email = email.orEmpty(),
    photoUrl = photoUrl?.toString()
)

private fun Exception.toVietnameseMessage(): String {
    val code = (this as? FirebaseAuthException)?.errorCode.orEmpty()
    val detail = message.orEmpty().lowercase()

    return when {
        code.contains("INVALID_EMAIL") || detail.contains("badly formatted") ->
            "Email không đúng định dạng."
        code.contains("USER_NOT_FOUND") || detail.contains("no user record") ->
            "Email này chưa được đăng ký. Hãy chọn Tạo tài khoản trước."
        code.contains("WRONG_PASSWORD") || code.contains("INVALID_CREDENTIAL") ||
            detail.contains("supplied auth credential") || detail.contains("password is invalid") ->
            "Email hoặc mật khẩu không chính xác. Nếu chưa có tài khoản, hãy chọn Tạo tài khoản."
        code.contains("EMAIL_ALREADY_IN_USE") || detail.contains("already in use") ->
            "Email này đã được sử dụng."
        code.contains("USER_DISABLED") ->
            "Tài khoản này đã bị vô hiệu hóa."
        code.contains("NETWORK") || detail.contains("network") ->
            "Không thể kết nối mạng. Vui lòng thử lại."
        else -> detail.takeIf { it.isNotBlank() }?.replaceFirstChar { it.uppercase() }
            ?: "Đã có lỗi xảy ra. Vui lòng thử lại."
    }
}
