package com.omer_dogan.dramaflix

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class DramaItem(
    @JsonProperty("id") val id: Long? = null,
    @JsonProperty("slug") val slug: String? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("description") val description: String? = null,
    @JsonProperty("cover_image") val coverImage: String? = null,
    @JsonProperty("backdrop_image") val backdropImage: String? = null,
    @JsonProperty("platform") val platform: String? = null,
    @JsonProperty("total_episodes") val totalEpisodes: Int? = null,
    @JsonProperty("views") val views: Long? = null,
    @JsonProperty("rating") val rating: Double? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DramaEpisode(
    @JsonProperty("id") val id: Long? = null,
    @JsonProperty("episode_number") val episodeNumber: Int? = null,
    @JsonProperty("title") val title: String? = null,
    @JsonProperty("url") val url: String? = null,
    @JsonProperty("thumbnail") val thumbnail: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HomeResponse(
    @JsonProperty("hero") val hero: List<DramaItem>? = null,
    @JsonProperty("new") val new: List<DramaItem>? = null,
    @JsonProperty("popular") val popular: List<DramaItem>? = null,
    @JsonProperty("dubbed") val dubbed: List<DramaItem>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeriesListResponse(
    @JsonProperty("series") val series: List<DramaItem>? = null,
    @JsonProperty("total") val total: Int? = null,
    @JsonProperty("offset") val offset: Int? = null,
    @JsonProperty("limit") val limit: Int? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class SeriesDetailResponse(
    @JsonProperty("series") val series: DramaItem? = null,
    @JsonProperty("episodes") val episodes: List<DramaEpisode>? = null
)
