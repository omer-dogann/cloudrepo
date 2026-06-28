@file:Suppress("DEPRECATION", "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST", "UNUSED_VARIABLE", "DEPRECATION_ERROR")
package com.omer_dogan.animpow

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














class Animpow : MainAPI() {
    override var mainUrl              = "https://animpow.com"
    override var name                 = "Animpow"
    override val hasMainPage          = false
    override var lang                 = "tr"
    override val supportedTypes       = setOf(TvType.Anime)

    override suspend fun search(query: String): List<SearchResponse> {
        val doc = app.get("/?s=").document
        return doc.select("div.item, article, div.film").mapNotNull { element ->
            val title = element.selectFirst("h2, h3, a.title")?.text() ?: return@mapNotNull null
            val href = element.selectFirst("a")?.attr("href") ?: return@mapNotNull null
            val poster = element.selectFirst("img")?.attr("src")

            newMovieSearchResponse(title, fixUrl(href), TvType.Anime) {
                this.posterUrl = poster
            }
        }
    }

    override suspend fun load(url: String): LoadResponse {
        val doc = app.get(fixUrl(url)).document
        val title = doc.selectFirst("h1")?.text()?.trim() ?: name
        val poster = doc.selectFirst("div.poster img, img.cover")?.attr("src")

        return newMovieLoadResponse(title, fixUrl(url), TvType.Anime, fixUrl(url)) {
            this.posterUrl = poster
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        val doc = app.get(fixUrl(data)).document
        doc.select("iframe[src]").forEach { iframe ->
            loadExtractor(iframe.attr("src"), data, subtitleCallback, callback)
        }
        return true
    }
}
