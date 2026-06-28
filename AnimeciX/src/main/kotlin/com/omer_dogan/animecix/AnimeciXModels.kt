@file:Suppress("DEPRECATION", "PARAMETER_NAME_CHANGED_ON_OVERRIDE", "UNCHECKED_CAST", "UNUSED_VARIABLE", "DEPRECATION_ERROR")
package com.omer_dogan.animecix

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














// ! Ömer Doğan CloudRepo Deposu




data class Category(
    @JsonProperty("pagination") val pagination: Pagination,
)

data class Search(
    @JsonProperty("results") val results: List<AnimeSearch>,
)

data class Title(
    @JsonProperty("title") val title: Anime,
)

data class Pagination(
    @JsonProperty("current_page") val currentPage: Int,
    @JsonProperty("last_page") val lastPage: Int,
    @JsonProperty("per_page") val perPage: Int,
    @JsonProperty("data") val data: List<AnimeSearch>,
    @JsonProperty("total") val total: Int,
)

data class AnimeSearch(
    @JsonProperty("id") val id: Int,
    @JsonProperty("title_type") val titleType: String,
    @JsonProperty("name") val title: String,
    @JsonProperty("poster") val poster: String?,
)

data class Anime(
    @JsonProperty("id") val id: Int,
    @JsonProperty("title_type") val titleType: String,
    @JsonProperty("name") val title: String,
    @JsonProperty("poster") val poster: String,
    @JsonProperty("description") val description: String,
    @JsonProperty("year") val year: Int?,
    @JsonProperty("mal_vote_average") val rating: String?,
    @JsonProperty("genres") val tags: List<Genre>,
    @JsonProperty("trailer") val trailer: String?,
    @JsonProperty("credits") val actors: List<Credit>,
    @JsonProperty("season_count") val seasonCount: Int, //Unrealiable?
    @JsonProperty("seasons") val seasons: List<Season>,
    @JsonProperty("videos") val videos: List<Video>
)

data class Genre(
    @JsonProperty("display_name") val name: String,
)

data class Credit(
    @JsonProperty("name") val name: String,
    @JsonProperty("poster") val poster: String?,
)

data class Video(
    @JsonProperty("episode_num") val episodeNum: Int?,
    @JsonProperty("season_num") val seasonNum: Int?,
    @JsonProperty("url") val url: String,
)

data class TitleVideos(
    @JsonProperty("videos") val videos: List<Video>
)

data class Season(@JsonProperty("number") val number: Int)