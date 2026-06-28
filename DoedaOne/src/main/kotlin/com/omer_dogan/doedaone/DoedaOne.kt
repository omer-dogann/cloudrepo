@file:Suppress("DEPRECATION", "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST", "UNUSED_VARIABLE", "DEPRECATION_ERROR")
package com.omer_dogan.doedaone

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














class DoedaOne : MainAPI() {
    override var mainUrl              = "https://www.doeda.one"
    override var name                 = "DoedaOne"
    override val hasMainPage          = true
    override var lang                 = "tr"
    override val supportedTypes       = setOf(TvType.NSFW)

    override val mainPage = mainPageOf(
        "${mainUrl}/" to "Son Eklenenler",
        "${mainUrl}/populer" to "Popüler Videolar",
    )

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val doc = app.get(request.data).document
        val home = doc.select("div.item, article, div.film, div.video-box, div.poster").mapNotNull { element ->
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
        return doc.select("div.item, article, div.film, div.video-box, div.poster").mapNotNull { element ->
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
        val poster = doc.selectFirst("div.poster img, img.cover, video")?.attr("src")
            ?: doc.selectFirst("div.poster img")?.attr("data-src")
        val description = doc.selectFirst("div.description, div.synopsis, div.plot, div.ozet")?.text()?.trim()
        val tags = doc.select("div.genres a, div.categories a, span.genre a, a[rel=tag]").map { it.text() }

        return newMovieLoadResponse(title, fixUrl(url), TvType.NSFW, fixUrl(url)) {
            this.posterUrl = fixUrlNull(poster)
            this.plot = description
            this.tags = tags
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
        val videoSources = doc.select("video source, source[src]").mapNotNull { fixUrlNull(it.attr("src")) }

        (iframes + videoSources).distinct().forEach { source ->
            try {
                loadExtractor(source, data, subtitleCallback, callback)
            } catch (_: Exception) {
                try {
                    val iframeDoc = app.get(source, referer = mainUrl).document
                    val pageContent = iframeDoc.html()
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
                    Regex("""(https?://[^\s"']+\.mp4[^\s"']*)""").find(pageContent)?.let {
                        callback.invoke(
                            ExtractorLink(
                                source = name,
                                name = "$name - MP4",
                                url = it.value,
                                referer = mainUrl,
                                quality = Qualities.Unknown.value,
                                isM3u8 = false
                            )
                        )
                    }
                } catch (_: Exception) {}
            }
        }
        return true
    }
}
