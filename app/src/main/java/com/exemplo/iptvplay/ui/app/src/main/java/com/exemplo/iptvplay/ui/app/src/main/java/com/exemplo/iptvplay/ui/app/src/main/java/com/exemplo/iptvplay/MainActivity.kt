package com.exemplo.iptvplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Button
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.tv.material3.darkColorScheme
import com.exemplo.iptvplay.data.Channel
import com.exemplo.iptvplay.data.XtreamCredentials
import com.exemplo.iptvplay.data.XtreamRepository
import com.exemplo.iptvplay.ui.HomeScreen
import com.exemplo.iptvplay.ui.PlayerScreen
import com.exemplo.iptvplay.ui.XtreamLoginScreen
import kotlinx.coroutines.launch

sealed interface Screen {
    data object Login : Screen
    data object Loading : Screen
    data object Home : Screen
    data object Error : Screen
    data class Player(val channel: Channel) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                var screen by remember { mutableStateOf<Screen>(Screen.Login) }
                var channels by remember { mutableStateOf<List<Channel>>(emptyList()) }
                var errorMsg by remember { mutableStateOf("") }
                val scope = rememberCoroutineScope()

                when (val s = screen) {
                    Screen.Login -> XtreamLoginScreen { server, user, pass ->
                        screen = Screen.Loading
                        scope.launch {
                            try {
                                val cred = XtreamCredentials(server, user, pass)
                                if (!XtreamRepository.validate(cred)) {
                                    errorMsg = "Servidor, usuário ou senha inválidos"
                                    screen = Screen.Error
                                    return@launch
                                }
                                val list = XtreamRepository.loadChannels(cred)
                                channels = list
                                screen = if (list.isEmpty()) {
                                    errorMsg = "Nenhum canal encontrado"
                                    Screen.Error
                                } else Screen.Home
                            } catch (e: Exception) {
                                errorMsg = e.message ?: "Erro de conexão"
                                screen = Screen.Error
                            }
                        }
                    }

                    Screen.Loading -> Box(
                        Modifier.fillMaxSize().background(Color(0xFF101010)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = Color.White)
                            Spacer(Modifier.height(16.dp))
                            Text("Carregando canais...", color = Color.White)
                        }
                    }

                    Screen.Error -> Box(
                        Modifier.fillMaxSize().background(Color(0xFF101010)).padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Erro: $errorMsg", color = Color(0xFFFF6B6B), fontSize = 18.sp)
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { screen = Screen.Login }) {
                                Text("Tentar novamente")
                            }
                        }
                    }

                    Screen.Home -> HomeScreen(channels) { ch ->
                        screen = Screen.Player(ch)
                    }

                    is Screen.Player -> PlayerScreen(s.channel) {
                        screen = Screen.Home
                    }
                }
            }
        }
    }
}
