@file:Suppress("DEPRECATION", "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST", "UNUSED_VARIABLE", "DEPRECATION_ERROR")
package com.omer_dogan.hdabla

import android.util.Log
import android.util.Base64
import java.util.Locale
import java.util.Arrays
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec
import java.security.MessageDigest
import java.security.SecureRandom
import kotlin.math.min
import okhttp3.Interceptor
import okhttp3.Response
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*
import com.lagradost.cloudstream3.utils.StringUtils.decodeUri
import com.lagradost.cloudstream3.network.CloudflareKiller
import com.lagradost.cloudstream3.LoadResponse.Companion.addActors
import com.lagradost.cloudstream3.LoadResponse.Companion.addTrailer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Document














class HDAbla : MainAPI() {
    override var mainUrl              = "https://hdabla.net"
    override var name                 = "HD Abla"
    override val hasMainPage          = true
    override var lang                 = "tr"
    override val supportedTypes       = setOf(TvType.NSFW)

    override val mainPage = mainPageOf(
        "${mainUrl}/filmler/page/" to "Son Filmler",
        "${mainUrl}/en-cok-izlenenler/page/" to "En Çok İzlenenler",
        "${mainUrl}/imdb-7-plus/page/" to "IMDB 7+ Filmler",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(request.data + page).document
        val home = doc.select("div.movie-box, article.movie-box, div.film-box, div.poster, div.film-item").mapNotNull { element ->
            val title = element.selectFirst("h2, h3, a.title, div.title, span.title")?.text()?.trim() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src") ?: element.selectFirst("img")?.attr("data-src")

            newMovieSearchResponse(title, fixUrl(href), TvType.NSFW) {
                this.posterUrl = fixUrlNull(poster)
            }
        }
        return newHomePageResponse(request.name, home)
    }

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("${mainUrl}/?s=${query}").document
        return doc.select("div.movie-box, article.movie-box, div.film-box, div.poster, div.film-item").mapNotNull { element ->
            val title = element.selectFirst("h2, h3, a.title, div.title, span.title")?.text()?.trim() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src") ?: element.selectFirst("img")?.attr("data-src")

            newMovieSearchResponse(title, fixUrl(href), TvType.NSFW) {
                this.posterUrl = fixUrlNull(poster)
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(fixUrl(url)).document
        val title = doc.selectFirst("h1, h1.title, div.title h1")?.text()?.trim() ?: name
        val poster = doc.selectFirst("div.poster img, img.cover, div.film-poster img")?.attr("src")
            ?: doc.selectFirst("div.poster img")?.attr("data-src")
        val description = doc.selectFirst("div.description, div.synopsis, div.plot, div.ozet, div.entry-content p")?.text()?.trim()
        val tags = doc.select("div.genres a, div.categories a, span.genre a, a[rel=tag]").map { it.text() }
        val rating = doc.selectFirst("span.imdb-rating, span.imdb, div.rating span")?.text()?.trim()?.toRatingInt()
        val year = doc.selectFirst("span.year, div.year, span.date")?.text()?.trim()?.toIntOrNull()

        return newMovieLoadResponse(title, fixUrl(url), TvType.Movie, fixUrl(url)) {
            this.posterUrl = fixUrlNull(poster)
            this.plot = description
            this.tags = tags
            this.rating = rating
            this.year = year
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(fixUrl(data)).document
        val iframes = doc.select("iframe[src]").mapNotNull { fixUrlNull(it.attr("src")) }
        val altSources = doc.select("div.alternatives a, div.player-tabs a, div.source-list a").mapNotNull {
            fixUrlNull(it.attr("href") ?: it.attr("data-src") ?: it.attr("data-url"))
        }

        (iframes + altSources).distinct().forEach { source ->
            try {
                loadExtractor(source, data, subtitleCallback, callback)
            } catch (_: Exception) {
                try {
                    val iframeDoc = app.get(source, referer = mainUrl).document
                    val pageContent = iframeDoc.html()
                    val videoUrl = iframeDoc.selectFirst("source")?.attr("src") ?: iframeDoc.selectFirst("video source")?.attr("src")
                    if (videoUrl != null) {
                        callback.invoke(
                            ExtractorLink(
                                source = name,
                                name = name,
                                url = videoUrl,
                                referer = mainUrl,
                                quality = Qualities.Unknown.value,
                                isM3u8 = videoUrl.contains(".m3u8")
                            )
                        )
                    }
                    Regex("""(https?://[^\s"']+\.m3u8[^\s"']*)""").find(pageContent)?.let {
                        callback.invoke(
                            ExtractorLink(
                                source = name,
                                name = "$name - HLS",
                                url = it.value,
                                referer = mainUrl,
                                quality = Qualities.Unknown.value,
                                isM3u8 = true
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
        }
        return true
    }
}

