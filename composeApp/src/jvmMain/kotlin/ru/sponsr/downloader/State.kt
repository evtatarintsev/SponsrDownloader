package ru.sponsr.downloader

import ru.sponsr.client.SponsrClient
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
    class ProjectSelecting(val client: SponsrClient, projects: List<SponsrProject>): State()
    class PostsLoading(val client: SponsrClient, project: SponsrProject): State()
    class PostsSelecting(val client: SponsrClient): State()
    class Downloading(val client: SponsrClient, posts: List<Int>): State()
    class Downloaded(val client: SponsrClient): State()
}