package com.exemplo.iptvplay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.Text
import kotlinx.coroutines.delay

@Composable
fun XtreamLoginScreen(onLogin: (server: String, user: String, pass: String) -> Unit) {
    var server by remember { mutableStateOf("http://") }
    var user by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val serverFocus = remember { FocusRequester() }

    LaunchedEffect(Unit) { delay(300); serverFocus.requestFocus() }

    Column(
        modifier = Modifier.fillMaxSize().background(Color(0xFF101010)).padding(48.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("BrunoTV", color = Color.White, fontSize = 40.sp)
        Spacer(Modifier.height(8.dp))
        Text("Servidor, usuário e senha Xtream", color = Color(0xFFBBBBBB), fontSize = 18.sp)
        Spacer(Modifier.height(24.dp))

        Field("Servidor (ex: http://servidor.com:8080)", server, { server = it }, serverFocus, false)
        Spacer(Modifier.height(12.dp))
        Field("Usuário", user, { user = it }, null, false)
        Spacer(Modifier.height(12.dp))
        Field("Senha", pass, { pass = it }, null, true)
        Spacer(Modifier.height(24.dp))

        Button(onClick = {
            if (server.isNotBlank() && user.isNotBlank() && pass.isNotBlank())
                onLogin(server.trim(), user.trim(), pass)
        }) { Text("Entrar") }
    }
}

@Composable
private fun Field(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    focusRequester: FocusRequester?,
    isPassword: Boolean
) {
    Column(Modifier.fillMaxWidth(0.6f)) {
        Text(label, color = Color(0xFF888888), fontSize = 14.sp)
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier.fillMaxWidth()
                .background(Color(0xFF1E1E1E), RoundedCornerShape(8.dp))
                .border(1.dp, Color(0xFF444444), RoundedCornerShape(8.dp))
        ) {
            BasicTextField(
                value = value,
                onValueChange = onChange,
                singleLine = true,
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                textStyle = TextStyle(color = Color.White, fontSize = 18.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .let { if (focusRequester != null) it.focusRequester(focusRequester) else it }
            )
        }
    }
}
