package ru.sponsr.downloader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import ru.sponsr.client.SponsrProject
import ru.sponsr.client.SponsrSession
import kotlin.io.path.Path

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SponsrDownloader",
    ) {
        var state by remember { mutableStateOf<State>(State.SavedSessionCheck()) }
        var projects by remember { mutableStateOf(emptyList<SponsrProject>()) }
        val scope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val snackbarScope = rememberCoroutineScope()

        MaterialTheme {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) {
                when (state) {
                    is State.SavedSessionCheck -> {
                        val sessFile = Path(".sess").toFile()

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        LaunchedEffect(Unit) {
                            if (sessFile.exists()) {
                                SponsrSession(sessFile.readText())
                                snackbarScope.launch {
                                    snackbarHostState.show("Проверка сохраненной сессии...")
                                }
                            } else {
                                snackbarScope.launch {
                                    snackbarHostState.show("Сохраненной сессии не найдено. Авторизуйтесь.")
                                }
                                state = State.Auth()
                            }

                        }
                    }

                    is State.Auth -> {
                        Login(
                            onLoggedIn = { sess ->
                                scope.launch {
                                    snackbarHostState.show("Получено значение SESS. Идет проверка...")
                                }
                            }
                        )
                    }

                    is State.ProjectSelecting -> {
                        ProjectList(
                            projects = projects,
                            onProjectClick = { project ->
                                println("Project clicked: ${project.title}")
                            }
                        )
                    }

                    is State.PostsLoading -> {

                    }

                    is State.PostsSelecting -> {

                    }

                    is State.Downloading -> {

                    }

                    is State.Downloaded -> {

                    }
                }
            }
        }
    }
}


suspend fun SnackbarHostState.show(message: String) = showSnackbar(
    message,
    null,
    true,
    SnackbarDuration.Short
)