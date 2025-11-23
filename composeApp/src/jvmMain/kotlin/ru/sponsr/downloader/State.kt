package ru.sponsr.downloader

import ru.sponsr.client.SponsrClient
import ru.sponsr.client.SponsrProject

sealed class State {
    class SavedSessionCheck(): State()
    class Auth() : State()
    class ProjectSelecting(client: SponsrClient, projects: List<SponsrProject>): State()
    class PostsLoading(client: SponsrClient, project: SponsrProject): State()
    class PostsSelecting(client: SponsrClient): State()
    class Downloading(client: SponsrClient, posts: List<Int>): State()
    class Downloaded(client: SponsrClient): State()
}