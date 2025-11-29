package ru.sponsr.client

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

    suspend fun posts(project: SponsrProject): SponsrResponse<List<SponsrPost>> {
        val limit = 50
        suspend fun getPage(page: Int) = client.sponsrPosts(project.id, page, limit)

        val firstPage = getPage(1)
        val totalPages = (firstPage.total + limit - 1) / limit

        val posts = buildList {
            addAll(firstPage.posts)
            for (page in 2..totalPages) {
                addAll(getPage(page).posts)
            }
        }
        return SponsrResponse.Success(posts.map { SponsrPost(it.id, it.title) })
    }

    private suspend fun HttpClient.sponsrPosts(projectId: Int, page: Int, limit: Int): PostListResponse =
        get("/api/v2/content/posts/?project_id=${projectId}&withText=true&tags=true&limit=${limit}&page=${page}&orderBy=date&withFiles=true&orderByType=desc")
            .body()
}

sealed class SponsrResponse<T> {
    data class Success<T>(val data: T) : SponsrResponse<T>()
    class Unauthorized<T> : SponsrResponse<T>()
    data class Unexpected<T>(val message: String) : SponsrResponse<T>()
}


@Serializable
data class PostListResponse(
    val total: Int,
    @SerialName("list")
    val posts: List<Post>,
    val page: Int,
    val limit: Int
)

@Serializable
data class Post(
    val id: Int,
    @SerialName("project_id")
    val projectId: Int,
    val date: String,
    val title: String,
    @SerialName("content_type")
    val contentType: String,
    val text: PostText?
)

@Serializable
data class PostText(
    @SerialName("post_id")
    val postId: Int,
    val text: String,
)