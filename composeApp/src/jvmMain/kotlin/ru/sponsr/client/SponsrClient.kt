package ru.sponsr.client

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JvmInline
value class SponsrSession(val value: String)

class SponsrClient(private val sess: SponsrSession) {
    private val baseUrl = "https://sponsr.ru"
    
    private val client = HttpClient(CIO) {
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
        expectSuccess = false // We'll handle status codes manually
    }

    /**
     * Fetches the list of subscribed projects from Sponsr
     * @return List of subscribed projects
     * @throws SponsrAuthException if authentication is required (HTTP 401 or 403)
     * @throws SponsrException for other HTTP errors
     */
    suspend fun projects(): List<SponsrProject> = withContext(Dispatchers.IO) {
        val url = "$baseUrl/memberships/subscribe/"
        
        val response = try {
            client.get(url) {
                timeout {
                    requestTimeoutMillis = 30_000
                }
            }
        } catch (e: Exception) {
            throw SponsrException("Failed to fetch projects: ${e.message}", e)
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val html = response.bodyAsText()
                val subscribePage = SubscribePage(html)
                subscribePage.projects()
            }
            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> {
                throw SponsrException("Authentication required. Please log in first.")
            }
            else -> {
                throw SponsrException("Failed to fetch projects: ${response.status}")
            }
        }
    }
}

class SponsrException(message: String, cause: Throwable? = null) : Exception(message, cause)
