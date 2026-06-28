package com.omer.cloudrepo.extractors

import com.lagradost.cloudstream3.SubtitleFile
import com.lagradost.cloudstream3.utils.ExtractorApi
import com.lagradost.cloudstream3.utils.ExtractorLink
import com.lagradost.cloudstream3.utils.ExtractorLinkType
import com.lagradost.cloudstream3.utils.newExtractorLink
import com.lagradost.cloudstream3.utils.Qualities
import com.lagradost.cloudstream3.app

class VidmolyExtractor : ExtractorApi() {
    override val name = "Vidmoly"
    override val mainUrl = "https://vidmoly.me"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val response = app.get(url, headers = mapOf("Referer" to (referer ?: mainUrl))).text
        val masterRegex = Regex("""file:\s*"([^"]+\.m3u8[^"]*)"""")
        val match = masterRegex.find(response)
        if (match != null) {
            val m3u8 = match.groupValues[1]
            callback.invoke(
                newExtractorLink(
                    name = name,
                    source = name,
                    url = m3u8,
                    type = ExtractorLinkType.M3U8
                ) {
                    this.referer = url
                    this.quality = Qualities.P1080.value
                }
            )
        }
    }
}

class FilemoonExtractor : ExtractorApi() {
    override val name = "Filemoon"
    override val mainUrl = "https://filemoon.sx"
    override val requiresReferer = true

    override suspend fun getUrl(
        url: String,
        referer: String?,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ) {
        val res = app.get(url).text
        val hlsMatch = Regex("""file:\s*"([^"]+)"""").find(res)
        if (hlsMatch != null) {
            callback.invoke(
                newExtractorLink(
                    name = name,
                    source = name,
                    url = hlsMatch.groupValues[1],
                    type = ExtractorLinkType.M3U8
                ) {
                    this.referer = url
                    this.quality = Qualities.P1080.value
                }
            )
        }
    }
}

