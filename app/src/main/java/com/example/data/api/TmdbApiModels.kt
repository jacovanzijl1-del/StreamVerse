package com.example.data.api

import com.squareup.moshi.Json

data class TmdbPagedResponse<T>(
    @Json(name = "page") val page: Int = 1,
    @Json(name = "results") val results: List<T> = emptyList(),
    @Json(name = "total_pages") val totalPages: Int = 1,
    @Json(name = "total_results") val totalResults: Int = 0
)

data class TmdbMovieDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String? = null,
    @Json(name = "original_title") val originalTitle: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "popularity") val popularity: Double? = 0.0,
    @Json(name = "genre_ids") val genreIds: List<Int>? = emptyList()
)

data class TmdbTvDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "original_name") val originalName: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "popularity") val popularity: Double? = 0.0,
    @Json(name = "genre_ids") val genreIds: List<Int>? = emptyList()
)

data class TmdbMultiSearchDto(
    @Json(name = "id") val id: Int,
    @Json(name = "media_type") val mediaType: String? = "movie",
    @Json(name = "title") val title: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "profile_path") val profilePath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "popularity") val popularity: Double? = 0.0
)

data class TmdbProviderDto(
    @Json(name = "provider_id") val providerId: Int,
    @Json(name = "provider_name") val providerName: String,
    @Json(name = "logo_path") val logoPath: String?,
    @Json(name = "display_priority") val displayPriority: Int = 0
)

data class TmdbWatchProvidersResponse(
    @Json(name = "results") val results: List<TmdbProviderDto> = emptyList()
)

data class TmdbMovieDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "title") val title: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "release_date") val releaseDate: String? = null,
    @Json(name = "runtime") val runtime: Int? = null,
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "tagline") val tagline: String? = null,
    @Json(name = "imdb_id") val imdbId: String? = null,
    @Json(name = "genres") val genres: List<TmdbGenreDto>? = emptyList(),
    @Json(name = "credits") val credits: TmdbCreditsDto? = null,
    @Json(name = "videos") val videos: TmdbVideosDto? = null,
    @Json(name = "recommendations") val recommendations: TmdbPagedResponse<TmdbMovieDto>? = null,
    @Json(name = "similar") val similar: TmdbPagedResponse<TmdbMovieDto>? = null,
    @Json(name = "watch/providers") val watchProviders: TmdbWatchProvidersWrapperDto? = null
)

data class TmdbTvDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "poster_path") val posterPath: String? = null,
    @Json(name = "backdrop_path") val backdropPath: String? = null,
    @Json(name = "first_air_date") val firstAirDate: String? = null,
    @Json(name = "episode_run_time") val episodeRunTime: List<Int>? = emptyList(),
    @Json(name = "vote_average") val voteAverage: Double? = 0.0,
    @Json(name = "vote_count") val voteCount: Int? = 0,
    @Json(name = "tagline") val tagline: String? = null,
    @Json(name = "external_ids") val externalIds: TmdbExternalIdsDto? = null,
    @Json(name = "genres") val genres: List<TmdbGenreDto>? = emptyList(),
    @Json(name = "seasons") val seasons: List<TmdbSeasonInfoDto>? = emptyList(),
    @Json(name = "credits") val credits: TmdbCreditsDto? = null,
    @Json(name = "videos") val videos: TmdbVideosDto? = null,
    @Json(name = "recommendations") val recommendations: TmdbPagedResponse<TmdbTvDto>? = null,
    @Json(name = "similar") val similar: TmdbPagedResponse<TmdbTvDto>? = null,
    @Json(name = "watch/providers") val watchProviders: TmdbWatchProvidersWrapperDto? = null
)

data class TmdbGenreDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String
)

data class TmdbExternalIdsDto(
    @Json(name = "imdb_id") val imdbId: String? = null
)

data class TmdbSeasonInfoDto(
    @Json(name = "id") val id: Int,
    @Json(name = "season_number") val seasonNumber: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "episode_count") val episodeCount: Int? = 0
)

data class TmdbSeasonDetailDto(
    @Json(name = "id") val id: Int,
    @Json(name = "season_number") val seasonNumber: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "episodes") val episodes: List<TmdbEpisodeDto>? = emptyList()
)

data class TmdbEpisodeDto(
    @Json(name = "id") val id: Int,
    @Json(name = "season_number") val seasonNumber: Int,
    @Json(name = "episode_number") val episodeNumber: Int,
    @Json(name = "name") val name: String? = null,
    @Json(name = "overview") val overview: String? = null,
    @Json(name = "still_path") val stillPath: String? = null,
    @Json(name = "air_date") val airDate: String? = null,
    @Json(name = "runtime") val runtime: Int? = null
)

data class TmdbCreditsDto(
    @Json(name = "cast") val cast: List<TmdbCastMemberDto>? = emptyList(),
    @Json(name = "crew") val crew: List<TmdbCrewMemberDto>? = emptyList()
)

data class TmdbCastMemberDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "character") val character: String? = null,
    @Json(name = "profile_path") val profilePath: String? = null
)

data class TmdbCrewMemberDto(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "job") val job: String? = null
)

data class TmdbVideosDto(
    @Json(name = "results") val results: List<TmdbVideoDto>? = emptyList()
)

data class TmdbVideoDto(
    @Json(name = "key") val key: String,
    @Json(name = "site") val site: String,
    @Json(name = "type") val type: String
)

data class TmdbWatchProvidersWrapperDto(
    @Json(name = "results") val results: Map<String, TmdbCountryProvidersDto>? = emptyMap()
)

data class TmdbCountryProvidersDto(
    @Json(name = "flatrate") val flatrate: List<TmdbProviderDto>? = emptyList(),
    @Json(name = "rent") val rent: List<TmdbProviderDto>? = emptyList(),
    @Json(name = "buy") val buy: List<TmdbProviderDto>? = emptyList()
)
