package ru.sponsr.downloader

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

/**
 * Экран авторизации на [sponsr.ru](https://sponsr.ru).
 *
 * В результате авторизации необходимо получить значение SESS cookie.
 */
@Composable
fun Login(onLoggedIn: (String) -> Unit) {
    var sess by remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.width(400.dp),
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Авторизация на sponsr.ru",
                    style = MaterialTheme.typography.h5
                )

                OutlinedTextField(
                    value = sess,
                    onValueChange = { sess = it },
                    label = { Text("Значение cookie SESS") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { onLoggedIn(sess) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = sess.isNotBlank()
                ) {
                    Text("Войти")
                }
            }
        }
    }
}