package ru.sponsr.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.Cookie
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


fun getHttpClient(sess: SponsrSession) = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.HEADERS
    }
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
            }
        )
    }
    install(ContentEncoding) {
        gzip()
        deflate()
    }
    install(DefaultRequest) {
        url("https://sponsr.ru")

        // Основные заголовки из curl
        header(
            HttpHeaders.UserAgent,
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.15; rv:145.0) Gecko/20100101 Firefox/145.0"
        )
        header(HttpHeaders.Accept, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        header(HttpHeaders.AcceptLanguage, "en-US,en;q=0.5")
        header(HttpHeaders.AcceptEncoding, "gzip, deflate, br, zstd")
        header(HttpHeaders.Referrer, "https://sponsr.ru/")
        header(HttpHeaders.CacheControl, "no-cache")
        header("Pragma", "no-cache")
        header("Priority", "u=0, i")
        header("Sec-GPC", "1")
        header("Upgrade-Insecure-Requests", "1")
        header("Sec-Fetch-Dest", "document")
        header("Sec-Fetch-Mode", "navigate")
        header("Sec-Fetch-Site", "same-origin")
        header("Sec-Fetch-User", "?1")

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
            sponsrCookie("SESS", sess.value),
            sponsrCookie("uuid_na", "ba65b550-9bfc-4994-ab73-44b593818771"),
            sponsrCookie("tmr_lvid", "b65dbc7715afae3239283429de21689d"),
            sponsrCookie("_ym_uid", "1751141600586211590"),
            sponsrCookie("_ga", "GA1.1.519286524.1756065676"),
            sponsrCookie("user_id", "193285")
        )
    }
    expectSuccess = false
}

fun sponsrCookie(name: String, value: String) =
    Cookie(name, value, domain = ".sponsr.ru", path = "/", secure = true, httpOnly = true)
