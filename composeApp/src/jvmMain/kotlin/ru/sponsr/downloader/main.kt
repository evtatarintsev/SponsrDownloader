package ru.sponsr.downloader

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.launch
import ru.sponsr.client.SponsrClient
import ru.sponsr.client.SponsrException

fun main() = application {
    val client = SponsrClient()
    
    Window(
        onCloseRequest = ::exitApplication,
        title = "SponsrDownloader",
    ) {
        var isLoading by remember { mutableStateOf(true) }
        var error by remember { mutableStateOf<String?>(null) }
        var projects by remember { mutableStateOf(emptyList<ru.sponsr.client.SponsrProject>()) }
        var showLogin by remember { mutableStateOf(false) }
        val scope = rememberCoroutineScope()
        
        // Load projects when the app starts
        LaunchedEffect(Unit) {
            loadProjects(client, onLoading = { isLoading = it }, onError = { error = it }, onSuccess = { projects = it })
        }
        
        MaterialTheme {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    isLoading -> {
                        // Show loading indicator
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                    error != null -> {
                        // Show error and login form
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = error ?: "An error occurred",
                                color = MaterialTheme.colors.error,
                                style = MaterialTheme.typography.h6,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            
                            if (showLogin) {
                                Login(
                                    onLoginClick = { sess ->
                                        scope.launch {
                                            error = null
                                            isLoading = true
                                            loadProjects(
                                                client,
                                                onLoading = { isLoading = it },
                                                onError = { error = it },
                                                onSuccess = { projects = it }
                                            )
                                        }
                                    }
                                )
                            } else {
                                Button(onClick = { showLogin = true }) {
                                    Text("Войти")
                                }
                            }
                        }
                    }
                    projects.isNotEmpty() -> {
                        // Show the list of projects
                        PostList(
                            projects = projects,
                            onProjectClick = { project ->
                                // Handle project click (e.g., navigate to project details)
                                println("Project clicked: ${project.title}")
                            }
                        )
                    }
                    else -> {
                        // No projects found
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No projects found")
                        }
                    }
                }
            }
        }
    }
}

private suspend fun loadProjects(
    client: SponsrClient,
    onLoading: (Boolean) -> Unit,
    onError: (String) -> Unit,
    onSuccess: (List<ru.sponsr.client.SponsrProject>) -> Unit
) {
    try {
        onLoading(true)
        val projects = client.projects()
        onSuccess(projects)
        onError(null ?: "")
    } catch (e: SponsrException) {
        onError(e.message ?: "Failed to load projects. Please try again.")
    } catch (e: Exception) {
        onError("An unexpected error occurred: ${e.message}")
    } finally {
        onLoading(false)
    }
}