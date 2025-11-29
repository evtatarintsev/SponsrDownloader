package ru.sponsr.client

import org.jsoup.Jsoup


/**
 * Parser for the subscription page
 */
class SubscribePage(private val html: String) {
    fun projects(): List<SponsrProject> {
        val doc = Jsoup.parse(html)

        val projectLinks = doc.select("table.subs a.project_title")

        return projectLinks.map { link ->
            val row = link.closest("tr")
            val id = row?.attr("data-id")?.toInt()
                ?: throw IllegalArgumentException("Project ID not found for link: ${link.attr("href")}")
            val relativeUrl = link.attr("href")
            SponsrProject(id, link.text(), "https://sponsr.ru$relativeUrl")
        }
    }
}