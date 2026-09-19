package com.example.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApiService {
    @GET("trending/movie/week")
    suspend fun getTrendingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMovieDto>>

    @GET("trending/tv/week")
    suspend fun getTrendingTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbTvDto>>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMovieDto>>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMovieDto>>

    @GET("movie/popular")
    suspend fun getPopularMovies(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMovieDto>>

    @GET("tv/on_the_air")
    suspend fun getOnTheAirTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbTvDto>>

    @GET("tv/top_rated")
    suspend fun getTopRatedTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbTvDto>>

    @GET("tv/popular")
    suspend fun getPopularTv(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbTvDto>>

    @GET("watch/providers/movie")
    suspend fun getMovieWatchProviders(
        @Query("api_key") apiKey: String,
        @Query("watch_region") watchRegion: String = "US"
    ): Response<TmdbWatchProvidersResponse>

    @GET("discover/movie")
    suspend fun discoverMovies(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProviders: String? = null,
        @Query("watch_region") watchRegion: String = "US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("vote_count.gte") voteCountGte: Int? = null,
        @Query("primary_release_date.gte") releaseDateGte: String? = null,
        @Query("primary_release_date.lte") releaseDateLte: String? = null,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMovieDto>>

    @GET("discover/tv")
    suspend fun discoverTv(
        @Query("api_key") apiKey: String,
        @Query("with_watch_providers") watchProviders: String? = null,
        @Query("watch_region") watchRegion: String = "US",
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("vote_count.gte") voteCountGte: Int? = null,
        @Query("first_air_date.gte") firstAirDateGte: String? = null,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbTvDto>>

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "credits,videos,recommendations,similar,watch/providers"
    ): Response<TmdbMovieDetailDto>

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(
        @Path("tv_id") tvId: Int,
        @Query("api_key") apiKey: String,
        @Query("append_to_response") append: String = "credits,videos,recommendations,similar,external_ids,watch/providers"
    ): Response<TmdbTvDetailDto>

    @GET("tv/{tv_id}/season/{season_number}")
    suspend fun getTvSeasonDetails(
        @Path("tv_id") tvId: Int,
        @Path("season_number") seasonNumber: Int,
        @Query("api_key") apiKey: String
    ): Response<TmdbSeasonDetailDto>

    @GET("search/multi")
    suspend fun multiSearch(
        @Query("api_key") apiKey: String,
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Response<TmdbPagedResponse<TmdbMultiSearchDto>>
}
