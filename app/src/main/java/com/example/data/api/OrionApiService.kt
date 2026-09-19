package com.example.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OrionApiService {
    @GET("/")
    suspend fun queryStreams(
        @Query("keyapp") keyApp: String,
        @Query("keyuser") keyUser: String,
        @Query("mode") mode: String,
        @Query("action") action: String,
        @Query("type") type: String, // "movie" or "show" (note: "show" is used for episodes as well, season and episode parameters specify the exact one)
        @Query("query") query: String,
        @Query("year") year: String?,
        @Query("idimdb") imdbId: String?,
        @Query("idtmdb") tmdbId: Int?,
        @Query("numberseason") season: Int?,
        @Query("numberepisode") episode: Int?,
        @Query("sortvalue") sortValue: String,
        @Query("limitcount") limitCount: Int,
        @Query("debridlookup") debridLookup: String?,
        @Query("debridresolve") debridResolve: String?,
        @Query("access") access: String?
    ): Response<OrionResponseDto>

    @GET("/")
    suspend fun retrieveUser(
        @Query("keyapp") keyApp: String,
        @Query("keyuser") keyUser: String,
        @Query("mode") mode: String,
        @Query("action") action: String
    ): Response<OrionUserResponseDto>
}

