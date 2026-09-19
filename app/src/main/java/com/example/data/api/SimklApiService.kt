package com.example.data.api

import retrofit2.Response
import retrofit2.http.GET

interface SimklApiService {
    @GET("discover/trending/movies/today_100.json")
    suspend fun getTrendingMovies(): Response<List<SimklItemDto>>

    @GET("discover/trending/tv/today_100.json")
    suspend fun getTrendingShows(): Response<List<SimklItemDto>>

    @GET("discover/trending/today_100.json")
    suspend fun getTrendingAll(): Response<List<SimklItemDto>>
}
