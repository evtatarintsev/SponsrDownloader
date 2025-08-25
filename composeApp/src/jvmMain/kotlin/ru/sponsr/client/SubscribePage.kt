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
            val relativeUrl = link.attr("href")
            SponsrProject(link.text(), "https://sponsr.ru$relativeUrl")
        }
    }
}