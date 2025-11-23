package ru.sponsr.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Cookie
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JvmInline
value class SponsrSession(val value: String)

class SponsrClient(sess: SponsrSession) {
    private val client = HttpClient(CIO) {
        install(DefaultRequest) {
            url("https://sponsr.ru")
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
            connectTimeoutMillis = 10_000
            socketTimeoutMillis = 30_000
        }
        install(HttpTimeout) {
            requestTimeoutMillis = 30_000
        }
        install(HttpCookies) {
            storage = ConstantCookiesStorage(
                Cookie(
                    name = "SESS",
                    value = sess.value,
                    domain = ".sponsr.ru",
                    path = "/",
                    secure = true,
                    httpOnly = true
                )
            )
        }
        expectSuccess = false
    }


    suspend fun projects(): SponsrResponse<List<SponsrProject>> = withContext(Dispatchers.IO) {
        val response = try {
            client.get("/memberships/subscribe/")
        } catch (e: Exception) {
            return@withContext SponsrResponse.Unexpected("Failed to fetch projects: ${e.message}")
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                println("Status ${response.status}")
                val html = response.bodyAsText()
                println("HTML: $html")
                val subscribePage = SubscribePage(html)
                SponsrResponse.Success(subscribePage.projects())
            }

            HttpStatusCode.Unauthorized, HttpStatusCode.Forbidden -> SponsrResponse.Unauthorized()
            else -> SponsrResponse.Unexpected("Failed to fetch projects: ${response.status}")
        }
    }
}

sealed class SponsrResponse<T> {
    data class Success<T>(val data: T) : SponsrResponse<T>()
    class Unauthorized<T> : SponsrResponse<T>()
    data class Unexpected<T>(val message: String) : SponsrResponse<T>()
}
