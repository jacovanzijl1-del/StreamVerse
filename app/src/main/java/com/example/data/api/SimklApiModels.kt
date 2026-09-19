package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = false)
data class SimklItemDto(
    @Json(name = "title") val title: String,
    @Json(name = "url") val url: String? = null,
    @Json(name = "poster") val poster: String? = null,
    @Json(name = "fanart") val fanart: String? = null,
    @Json(name = "ids") val ids: SimklIdsDto? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "rank") val rank: Int? = null,
    @Json(name = "watched") val watched: Int? = null,
    @Json(name = "ratings") val ratings: SimklRatingsDto? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "runtime") val runtime: String? = null,
    @Json(name = "genres") val genres: List<String>? = null
)

@JsonClass(generateAdapter = false)
data class SimklIdsDto(
    @Json(name = "simkl_id") val simklId: Int? = null,
    @Json(name = "slug") val slug: String? = null,
    @Json(name = "imdb") val imdb: String? = null,
    @Json(name = "tmdb") val tmdb: String? = null,
    @Json(name = "tvdb") val tvdb: String? = null
)

@JsonClass(generateAdapter = false)
data class SimklRatingsDto(
    @Json(name = "simkl") val simkl: SimklScoreDto? = null,
    @Json(name = "imdb") val imdb: SimklScoreDto? = null
)

@JsonClass(generateAdapter = false)
data class SimklScoreDto(
    @Json(name = "rating") val rating: Double? = null,
    @Json(name = "votes") val votes: Int? = null
)
