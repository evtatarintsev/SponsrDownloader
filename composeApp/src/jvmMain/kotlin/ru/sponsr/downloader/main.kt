package ru.sponsr.downloader

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import ru.sponsr.client.SponsrClient
import ru.sponsr.client.SponsrException

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "SponsrDownloader",
    ) {
        var state by remember { mutableStateOf<State>(State.SavedSessionCheck()) }
        var error by remember { mutableStateOf<String?>(null) }
        var projects by remember { mutableStateOf(emptyList<ru.sponsr.client.SponsrProject>()) }
        var showLogin by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()

        // Load projects when the app starts

        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                when (state) {
                    is State.SavedSessionCheck -> {
                        // Show loading indicator
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
//                        LaunchedEffect(Unit) {
//                            loadProjects(
//                                client,
//                                onLoading = { isLoading = it },
//                                onError = { error = it },
//                                onSuccess = { projects = it })
//                        }

                    }

                    is State.Auth -> {
//                        Login(
//                            onLoginClick = { sess ->
//                                scope.launch {
//                                    error = null
//                                    isLoading = true
//                                    loadProjects(
//                                        client,
//                                        onLoading = { isLoading = it },
//                                        onError = { error = it },
//                                        onSuccess = { projects = it }
//                                    )
//                                }
//                            }
//                        )
                    }

                    is State.ProjectSelecting -> {
                        ProjectList(
                            projects = projects,
                            onProjectClick = { project ->
                                // Handle project click (e.g., navigate to project details)
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
