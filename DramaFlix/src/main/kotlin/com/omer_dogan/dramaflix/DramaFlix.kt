package com.omer_dogan.dramaflix

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.lagradost.cloudstream3.*
import com.lagradost.cloudstream3.utils.*

class DramaFlix : MainAPI() {
    override var mainUrl = "https://dramaflix.cc/en"
    override var name = "DramaFlix"
    override val hasMainPage = true
    override var lang = "tr"
    override val hasQuickSearch = true
    override val supportedTypes = setOf(TvType.AsianDrama, TvType.TvSeries)

    private val objectMapper by lazy {
        ObjectMapper().registerModule(KotlinModule.Builder().build())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    override val mainPage = mainPageOf(
        "${mainUrl}/api/home?language=tr" to "Ana Sayfa (Gündem)",
        "${mainUrl}/api/series?language=tr&limit=24" to "Son Eklenenler",
        "${mainUrl}/api/series?language=tr&platform=dramabox&limit=24" to "DramaBox",
        "${mainUrl}/api/series?language=tr&platform=shortmax&limit=24" to "ShortMax",
        "${mainUrl}/api/series?language=tr&platform=netshort&limit=24" to "NetShort",
        "${mainUrl}/api/series?language=tr&platform=reelshort&limit=24" to "ReelShort"
    )

    private fun DramaItem.toSearchResponse(): SearchResponse? {
        val titleText = this.title ?: return null
        val slugStr = this.slug ?: return null
        val detailUrl = "${mainUrl}/tr/drama/${slugStr}"
        val poster = this.coverImage

        return newTvSeriesSearchResponse(titleText, detailUrl, TvType.AsianDrama) {
            this.posterUrl = poster
        }
    }

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse {
        val offset = (page - 1) * 24
        val url = if (request.data.contains("/api/series?")) {
            "${request.data}&offset=${offset}"
        } else {
            request.data
        }

        val responseText = app.get(url).text
        val searchResults = mutableListOf<SearchResponse>()

        if (url.contains("/api/home")) {
            val homeData = AppUtils.tryParseJson<HomeResponse>(responseText)
                ?: try { objectMapper.readValue(responseText, HomeResponse::class.java) } catch (e: Exception) { null }

            homeData?.popular?.mapNotNull { it.toSearchResponse() }?.let { searchResults.addAll(it) }
            homeData?.new?.mapNotNull { it.toSearchResponse() }?.let { searchResults.addAll(it) }
            homeData?.dubbed?.mapNotNull { it.toSearchResponse() }?.let { searchResults.addAll(it) }
        } else {
            val seriesData = AppUtils.tryParseJson<SeriesListResponse>(responseText)
                ?: try { objectMapper.readValue(responseText, SeriesListResponse::class.java) } catch (e: Exception) { null }

            seriesData?.series?.mapNotNull { it.toSearchResponse() }?.let { searchResults.addAll(it) }
        }

        return newHomePageResponse(request.name, searchResults.distinctBy { it.url })
    }

    override suspend fun quickSearch(query: String): List<SearchResponse> = search(query)

    override suspend fun search(query: String): List<SearchResponse> {
        val url = "${mainUrl}/api/series?search=${query}&language=tr"
        val responseText = app.get(url).text
        val seriesData = AppUtils.tryParseJson<SeriesListResponse>(responseText)
            ?: try { objectMapper.readValue(responseText, SeriesListResponse::class.java) } catch (e: Exception) { null }

        return seriesData?.series?.mapNotNull { it.toSearchResponse() } ?: emptyList()
    }

    override suspend fun load(url: String): LoadResponse? {
        val slug = url.substringAfter("/drama/").substringBefore("?").substringBefore("/")
        val apiUrl = "${mainUrl}/api/series/${slug}"
        val responseText = app.get(apiUrl).text

        val detailData = AppUtils.tryParseJson<SeriesDetailResponse>(responseText)
            ?: try { objectMapper.readValue(responseText, SeriesDetailResponse::class.java) } catch (e: Exception) { null }

        val item = detailData?.series ?: return null
        val title = item.title ?: "Drama"
        val poster = item.coverImage
        val description = item.description

        val episodes = detailData.episodes?.mapNotNull { ep ->
            val epUrl = ep.url ?: return@mapNotNull null
            val epNum = ep.episodeNumber ?: 1

            newEpisode(epUrl) {
                this.name = ep.title ?: "${epNum}. Bölüm"
                this.episode = epNum
                this.season = 1
            }
        } ?: emptyList()

        return newTvSeriesLoadResponse(title, url, TvType.AsianDrama, episodes) {
            this.posterUrl = poster
            this.plot = description
            this.tags = listOfNotNull(item.platform)
        }
    }

    override suspend fun loadLinks(
        data: String,
        isCasting: Boolean,
        subtitleCallback: (SubtitleFile) -> Unit,
        callback: (ExtractorLink) -> Unit
    ): Boolean {
        callback.invoke(
            newExtractorLink(
                source = name,
                name = name,
                url = data,
                type = ExtractorLinkType.M3U8
            ) {
                this.quality = Qualities.Unknown.value
                this.headers = mapOf("Referer" to "${mainUrl}/")
            }
        )
        return true
    }
}
