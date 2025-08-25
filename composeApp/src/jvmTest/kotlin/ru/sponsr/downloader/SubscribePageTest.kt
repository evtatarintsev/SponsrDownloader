package ru.sponsr.downloader

import ru.sponsr.client.SponsrProject
import ru.sponsr.client.SubscribePage
import kotlin.test.Test
import kotlin.test.assertEquals

class SubscribePageTest {
    @Test
    fun `parse subscribe page and grab project list`() {
        // Load the HTML file from test resources
        val html = javaClass.getResource("/subscribe.html")?.readText()
            ?: error("Could not load subscribe.html from test resources")

        // Parse the HTML
        val subscribePage = SubscribePage(html)
        val projects = subscribePage.projects()
        assertEquals(listOf(SponsrProject("Уроки истории", "https://sponsr.ru/uzhukoffa_lessons/")), projects)
    }
}