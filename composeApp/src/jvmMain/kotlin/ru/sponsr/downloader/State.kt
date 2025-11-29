package ru.sponsr.downloader

import ru.sponsr.client.SponsrClient
import ru.sponsr.client.SponsrPost
import ru.sponsr.client.SponsrProject

sealed class State {
    class SavedSessionCheck(): State(){
        fun toAuth() = Auth()
        fun toProjectsLoading(client: SponsrClient) = ProjectsLoading(client)
    }
    class Auth() : State(){
        fun toProjectsLoading(client: SponsrClient) = ProjectsLoading(client)
    }
    class ProjectsLoading(val client: SponsrClient): State(){
        fun toProjectSelecting(projects: List<SponsrProject>) = ProjectSelecting(client, projects)
    }
    class ProjectSelecting(val client: SponsrClient, val projects: List<SponsrProject>): State(){
        fun toPostsLoading(project: SponsrProject) = PostsLoading(client, project)
    }
    class PostsLoading(val client: SponsrClient, val project: SponsrProject): State(){
        fun toPostsSelecting(posts: List<SponsrPost>) = PostsSelecting(client, posts)
    }
    class PostsSelecting(val client: SponsrClient, val posts: List<SponsrPost>): State()
    class Downloading(val client: SponsrClient, val posts: List<SponsrPost>): State()
    class Downloaded(val client: SponsrClient): State()
}