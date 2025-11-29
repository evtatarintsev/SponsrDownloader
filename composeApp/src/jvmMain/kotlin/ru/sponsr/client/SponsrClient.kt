package ru.sponsr.client

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@JvmInline
value class SponsrSession(val value: String)

class SponsrClient(sess: SponsrSession) {
    private val client = getHttpClient(sess)

    suspend fun projects(): SponsrResponse<List<SponsrProject>> = withContext(Dispatchers.IO) {
        val response = try {
            client.get("/memberships/subscribe/")
        } catch (e: Exception) {
            return@withContext SponsrResponse.Unexpected("Failed to fetch projects: ${e.message}")
        }

        when (response.status) {
            HttpStatusCode.OK -> {
                val subscribePage = SubscribePage(response.bodyAsText())
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
