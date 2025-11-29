package ru.sponsr.downloader

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import ru.sponsr.client.SponsrClient
import ru.sponsr.client.SponsrResponse
import ru.sponsr.client.SponsrSession
import kotlin.io.path.Path

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SponsrDownloader",
    ) {
        var state by remember { mutableStateOf<State>(State.SavedSessionCheck()) }

        val snackbarHostState = remember { SnackbarHostState() }
        val snackbarScope = rememberCoroutineScope()

        MaterialTheme {
            Scaffold(
                snackbarHost = { SnackbarHost(snackbarHostState) }
            ) {
                val currentState = state
                when (currentState) {
                    is State.SavedSessionCheck -> {
                        val sessFile = Path(".sess").toFile()

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        LaunchedEffect(Unit) {
                            if (sessFile.exists()) {
                                snackbarScope.launch {
                                    snackbarHostState.show("Проверка сохраненной сессии...")
                                }
                                val client = SponsrClient(SponsrSession(sessFile.readText()))
                                state = currentState.toProjectsLoading(client)
                            } else {
                                snackbarScope.launch {
                                    snackbarHostState.show("Сохраненной сессии не найдено. Авторизуйтесь.")
                                }
                                state = currentState.toAuth()
                            }

                        }
                    }

                    is State.Auth -> {
                        Login(
                            onLoggedIn = { sess ->
                                val client = SponsrClient(SponsrSession(sess))
                                state = currentState.toProjectsLoading(client)
                            }
                        )
                    }

                    is State.ProjectsLoading -> {
                        LaunchedEffect(Unit) {
                            when (val result = currentState.client.projects()) {
                                is SponsrResponse.Success -> {
                                    state = currentState.toProjectSelecting(result.data)
                                }

                                is SponsrResponse.Unauthorized -> snackbarScope.launch {
                                    snackbarHostState.show("Неавторизован")
                                }

                                is SponsrResponse.Unexpected -> snackbarScope.launch {
                                    snackbarHostState.show("Неожиданная ошибка: ${result.message}")
                                }
                            }
                        }
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }

                    is State.ProjectSelecting -> {
                        ProjectList(
                            projects = currentState.projects,
                            onProjectClick = { project ->
                                state = currentState.toPostsLoading(project)
                            }
                        )
                    }

                    is State.PostsLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                        LaunchedEffect(Unit) {
                            snackbarScope.launch {
                                snackbarHostState.show("Загружаем материалы проекта.")
                            }
                            when (val result = currentState.client.posts(currentState.project)) {
                                is SponsrResponse.Success -> {
                                    state = currentState.toPostsSelecting(result.data)
                                }

                                is SponsrResponse.Unauthorized -> snackbarScope.launch {
                                    snackbarHostState.show("Неавторизован")
                                }

                                is SponsrResponse.Unexpected -> snackbarScope.launch {
                                    snackbarHostState.show("Неожиданная ошибка: ${result.message}")
                                }
                            }

                        }
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