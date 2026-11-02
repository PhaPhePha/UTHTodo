package com.uthtodo.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.uthtodo.app.R
import com.uthtodo.app.domain.auth.AppUser
import com.uthtodo.app.domain.model.WelcomeBrand

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onGoogleSignIn: () -> Unit,
    onAuthenticated: (AppUser) -> Unit
) {
    val state = viewModel.uiState
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.user?.id) {
        state.user?.let(onAuthenticated)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(18.dp))
        Box(
            modifier = Modifier
                .size(68.dp)
                .background(WelcomeBrand.paleBlue, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(R.drawable.ic_uth_todo_logo),
                contentDescription = "UTH Todo",
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = if (state.isRegisterMode) "Tạo tài khoản UTH Todo" else "Chào mừng đến với UTH Todo",
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold,
            color = WelcomeBrand.text,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = if (state.isRegisterMode) "Đăng ký để đồng bộ công việc trên mọi thiết bị của bạn."
            else "Đăng nhập để lưu công việc và đồng bộ trên mọi thiết bị của bạn.",
            color = WelcomeBrand.muted,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))

        OutlinedButton(
            onClick = onGoogleSignIn,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("G", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(Modifier.size(10.dp))
            Text("Tiếp tục với Google", color = WelcomeBrand.text, fontWeight = FontWeight.SemiBold)
        }

        OrDivider()

        OutlinedTextField(
            value = state.email,
            onValueChange = viewModel::updateEmail,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Email") },
            placeholder = { Text("you@example.com") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            singleLine = true,
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = state.password,
            onValueChange = viewModel::updatePassword,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Mật khẩu") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        if (passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu"
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
            shape = RoundedCornerShape(12.dp)
        )

        if (state.isRegisterMode) {
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = state.confirmPassword,
                onValueChange = viewModel::updateConfirmPassword,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Xác nhận mật khẩu") },
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp)
            )
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = viewModel::resetPassword, enabled = !state.isLoading) {
                    Text("Quên mật khẩu?", color = WelcomeBrand.blue, fontWeight = FontWeight.SemiBold)
                }
            }
        }

        state.errorMessage?.let {
            MessageText(it, Color(0xFFB3261E))
        }
        state.infoMessage?.let {
            MessageText(it, WelcomeBrand.blue)
        }

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = if (state.isRegisterMode) viewModel::register else viewModel::signIn,
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WelcomeBrand.blue)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text(if (state.isRegisterMode) "Tạo tài khoản" else "Đăng nhập", fontWeight = FontWeight.Bold)
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                if (state.isRegisterMode) "Đã có tài khoản?" else "Chưa có tài khoản?",
                color = WelcomeBrand.muted,
                fontSize = 14.sp
            )
            TextButton(onClick = viewModel::toggleRegisterMode) {
                Text(
                    if (state.isRegisterMode) "Đăng nhập" else "Tạo tài khoản",
                    color = WelcomeBrand.blue,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Text(
            "Khi tiếp tục, bạn đồng ý với Điều khoản sử dụng và Chính sách quyền riêng tư của UTH Todo.",
            color = WelcomeBrand.muted,
            fontSize = 11.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )
    }
}

@Composable
private fun OrDivider() {
    Row(Modifier.fillMaxWidth().padding(vertical = 18.dp), verticalAlignment = Alignment.CenterVertically) {
        androidx.compose.material3.HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE4E5E9))
        Text("hoặc", color = WelcomeBrand.muted, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp))
        androidx.compose.material3.HorizontalDivider(Modifier.weight(1f), color = Color(0xFFE4E5E9))
    }
}

@Composable
private fun MessageText(message: String, color: Color) {
    Text(
        message,
        color = color,
        fontSize = 13.sp,
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
    )
}
