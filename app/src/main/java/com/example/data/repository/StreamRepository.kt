package com.example.data.repository

import com.example.data.api.ApiClient
import com.example.data.api.TmdbMovieDto
import com.example.data.api.TmdbTvDto
import com.example.data.local.UserPreferences
import com.example.data.model.CastMember
import com.example.data.model.Episode
import com.example.data.model.MediaDetail
import com.example.data.model.MediaItem
import com.example.data.model.Provider
import com.example.data.model.Season
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StreamRepository(private val prefs: UserPreferences) {

    val defaultProviders = listOf(
        Provider(8, "Netflix", "/pbpMk2JmcoNnQwx5JGpXngfoWtp.jpg", 1),
        Provider(9, "Amazon Prime Video", "/pvsag1Tgr6E65yGLB6Z8jG6i2t2.jpg", 2),
        Provider(337, "Disney+", "/7rwgEs15tFwyR9NPQ5vpzxTj19Q.jpg", 3),
        Provider(350, "Apple TV+", "/2E03jvSV7P2m7gTIdGJy5sF2k9u.jpg", 4),
        Provider(15, "Hulu", "/zxrVdFjIjLqkfnwyghn2XuMinQA.jpg", 5),
        Provider(1899, "Max", "/6Q3ZzT7gL1xQ3C7C7a8bZ3yX1a.jpg", 6),
        Provider(386, "Peacock", "/8VCV78prwd9QzZnEm0ReO6bERDa.jpg", 7),
        Provider(531, "Paramount+", "/fi83B1oztoS47xxcemFdPMhIzK.jpg", 8)
    )

    // Curated catalog for immediate out-of-the-box streaming and offline preview
    val curatedCatalog: List<MediaItem> = listOf(
        MediaItem(
            id = 693134,
            title = "Dune: Part Two",
            overview = "Follow the mythic journey of Paul Atreides as he unites with Chani and the Fremen while on a path of revenge against the conspirators who destroyed his family.",
            posterPath = "/1pdfLvkbY9ohJlCjQH2CZjjYVvJ.jpg",
            backdropPath = "/xOMo8BRK7PfcJv9JCnx7s520bvw.jpg",
            mediaType = "movie",
            releaseDate = "2024-03-01",
            voteAverage = 8.2,
            voteCount = 5400,
            genres = listOf("Sci-Fi", "Adventure"),
            providerIds = listOf(1899, 9),
            popularity = 95.5
        ),
        MediaItem(
            id = 94605,
            title = "Arcane",
            overview = "Amid the stark discord of twin cities Piltover and Zaun, two sisters fight on rival sides of a war between magic technologies and incompatible convictions.",
            posterPath = "/fqldf2t8ztc9aiwn397rHg2SepL.jpg",
            backdropPath = "/u3YQJILZuNi2bABn9cu9vgxP9Yp.jpg",
            mediaType = "tv",
            releaseDate = "2024-11-09",
            voteAverage = 9.0,
            voteCount = 4200,
            genres = listOf("Animation", "Sci-Fi & Fantasy", "Action"),
            providerIds = listOf(8),
            popularity = 98.2
        ),
        MediaItem(
            id = 872585,
            title = "Oppenheimer",
            overview = "The story of J. Robert Oppenheimer’s role in the development of the atomic bomb during World War II.",
            posterPath = "/8Gxv8gSFCU0XGDykEGv7zR1n2ua.jpg",
            backdropPath = "/rMvPXy8PUjj1o8o1pzgQbdNCsvj.jpg",
            mediaType = "movie",
            releaseDate = "2023-07-21",
            voteAverage = 8.1,
            voteCount = 8900,
            genres = listOf("Drama", "History"),
            providerIds = listOf(386, 9),
            popularity = 88.0
        ),
        MediaItem(
            id = 111110,
            title = "Shōgun",
            overview = "When a mysterious European ship is found marooned in a nearby fishing village, Lord Yoshii Toranaga discovers secrets that could tip the scales of power.",
            posterPath = "/7O4iVfOMQmdCSxhOg1WnzG1AgYT.jpg",
            backdropPath = "/5zmiBoMzeWuoA06V8zgKj4f6z6z.jpg",
            mediaType = "tv",
            releaseDate = "2024-02-27",
            voteAverage = 8.5,
            voteCount = 1800,
            genres = listOf("Drama", "War & Politics"),
            providerIds = listOf(15, 337),
            popularity = 92.4
        ),
        MediaItem(
            id = 157336,
            title = "Interstellar",
            overview = "The adventures of a group of explorers who make use of a newly discovered wormhole to surpass the limitations on human space travel and conquer the vast distances involved in an interstellar voyage.",
            posterPath = "/gEU2QniE6E77NI6lCU6MxlNBvIx.jpg",
            backdropPath = "/xJHokMbljvjADYdit5fK5VQsXEG.jpg",
            mediaType = "movie",
            releaseDate = "2014-11-05",
            voteAverage = 8.4,
            voteCount = 35200,
            genres = listOf("Adventure", "Drama", "Sci-Fi"),
            providerIds = listOf(531, 9),
            popularity = 84.1
        ),
        MediaItem(
            id = 100088,
            title = "The Last of Us",
            overview = "Twenty years after modern civilization has been destroyed, Joel, a hardened survivor, is hired to smuggle Ellie, a 14-year-old girl, out of an oppressive quarantine zone.",
            posterPath = "/uKvVjHNqB5VmOrdxqAt2V7JMrHG.jpg",
            backdropPath = "/uDgy6hyPd82kOHh6I95FLtLnj6p.jpg",
            mediaType = "tv",
            releaseDate = "2023-01-15",
            voteAverage = 8.6,
            voteCount = 5100,
            genres = listOf("Drama", "Sci-Fi & Fantasy"),
            providerIds = listOf(1899),
            popularity = 89.7
        ),
        MediaItem(
            id = 533535,
            title = "Deadpool & Wolverine",
            overview = "A listless Wade Wilson toils away in civilian life with his days as the morally flexible mercenary Deadpool behind him. But when his homeworld faces an existential threat, he must reluctantly suit-up again with an even more reluctant Wolverine.",
            posterPath = "/8cdWjvZQUExUUTzyp4t6EDMubfO.jpg",
            backdropPath = "/yDHYTjA3R0jFYba16jBB1jv8AmC.jpg",
            mediaType = "movie",
            releaseDate = "2024-07-26",
            voteAverage = 7.7,
            voteCount = 5900,
            genres = listOf("Action", "Comedy", "Sci-Fi"),
            providerIds = listOf(337),
            popularity = 96.0
        ),
        MediaItem(
            id = 93405,
            title = "Squid Game",
            overview = "Hundreds of cash-strapped players accept a strange invitation to compete in children's games. Inside, a tempting prize awaits with deadly high stakes.",
            posterPath = "/dDlEmu3EZ0Pgg93K2SVNLCjCSvE.jpg",
            backdropPath = "/2meX1nMdScFOoV4370rqHWFDxZ2.jpg",
            mediaType = "tv",
            releaseDate = "2024-12-26",
            voteAverage = 8.3,
            voteCount = 14200,
            genres = listOf("Action & Adventure", "Mystery", "Drama"),
            providerIds = listOf(8),
            popularity = 94.0
        ),
        MediaItem(
            id = 1022789,
            title = "Inside Out 2",
            overview = "Teenager Riley's mind headquarters is undergoing a sudden demolition to make room for something entirely unexpected: new Emotions! Joy, Sadness, Anger, Fear and Disgust aren't sure how to feel when Anxiety shows up.",
            posterPath = "/vpnVM9B6NMmQpWeZvzLvDESb2QY.jpg",
            backdropPath = "/p5ozvmdgsmbWe0H8wf4DySSSYGQ.jpg",
            mediaType = "movie",
            releaseDate = "2024-06-14",
            voteAverage = 7.6,
            voteCount = 5100,
            genres = listOf("Animation", "Family", "Adventure"),
            providerIds = listOf(337),
            popularity = 91.2
        ),
        MediaItem(
            id = 76479,
            title = "The Boys",
            overview = "A fun and irreverent take on what happens when superheroes—who are as popular as celebrities, as influential as politicians, and as revered as gods—abuse their superpowers rather than use them for good.",
            posterPath = "/2zmTngn1tYCzAvfnrFLhxeD82hz.jpg",
            backdropPath = "/nxxCPRGTzxUH8AhMrjw79umvh0A.jpg",
            mediaType = "tv",
            releaseDate = "2024-06-13",
            voteAverage = 8.5,
            voteCount = 9800,
            genres = listOf("Sci-Fi & Fantasy", "Action & Adventure"),
            providerIds = listOf(9),
            popularity = 93.1
        ),
        MediaItem(
            id = 912649,
            title = "Venom: The Last Dance",
            overview = "Eddie and Venom are on the run. Hunted by both of their worlds and with the net closing in, the duo are forced into a devastating decision that will bring the curtains down on Venom and Eddie's last dance.",
            posterPath = "/aosm8Vh92loBt6SmSf6IRlyj4JJ.jpg",
            backdropPath = "/3V4kLQg0kSqPLctI5ziYWgAZYqa.jpg",
            mediaType = "movie",
            releaseDate = "2024-10-25",
            voteAverage = 6.8,
            voteCount = 2400,
            genres = listOf("Action", "Sci-Fi", "Adventure"),
            providerIds = listOf(8, 1899),
            popularity = 89.0
        ),
        MediaItem(
            id = 84958,
            title = "Loki",
            overview = "After stealing the Tesseract during the events of “Avengers: Endgame,” an alternate version of Loki is brought to the mysterious Time Variance Authority (TVA).",
            posterPath = "/voHUmlviYlo5sgFLNYCq5ATn9d3.jpg",
            backdropPath = "/q36n14jPZg8TqBmsrYj2v1Xp9mR.jpg",
            mediaType = "tv",
            releaseDate = "2023-10-05",
            voteAverage = 8.2,
            voteCount = 11200,
            genres = listOf("Drama", "Sci-Fi & Fantasy"),
            providerIds = listOf(337),
            popularity = 87.5
        ),
        MediaItem(
            id = 1184918,
            title = "The Wild Robot",
            overview = "After a shipwreck, an intelligent robot called Roz is stranded on an uninhabited island. To survive the harsh environment, Roz bonds with the island's animals and cares for an orphaned baby goose.",
            posterPath = "/wTnV3PCVW5O92JMrFvvrRil3RsH.jpg",
            backdropPath = "/417tYZ4XUyJrtyZXj7HpvWf1E8f.jpg",
            mediaType = "movie",
            releaseDate = "2024-09-27",
            voteAverage = 8.4,
            voteCount = 4100,
            genres = listOf("Animation", "Sci-Fi", "Family"),
            providerIds = listOf(386),
            popularity = 92.0
        ),
        MediaItem(
            id = 70523,
            title = "Dark",
            overview = "A missing child sets four families on a frantic hunt for answers as they unearth a mind-bending mystery that spans three generations.",
            posterPath = "/apbrbWs8M9lyOpJYU5WXrpFbk1Z.jpg",
            backdropPath = "/3lBDg3i6nn5R2NKICJ79KyKPJYR.jpg",
            mediaType = "tv",
            releaseDate = "2020-06-27",
            voteAverage = 8.4,
            voteCount = 6200,
            genres = listOf("Sci-Fi & Fantasy", "Drama", "Mystery"),
            providerIds = listOf(8),
            popularity = 83.2
        ),
        MediaItem(
            id = 278,
            title = "The Shawshank Redemption",
            overview = "Imprisoned in the 1940s for the double murder of his wife and her lover, upstanding banker Andy Dufresne begins a new life at the Shawshank prison.",
            posterPath = "/9cqNxx0GxF0bflZmeSMuL5tnGzr.jpg",
            backdropPath = "/kXfqcdQKsToO0OUXHcrrNCHDBzO.jpg",
            mediaType = "movie",
            releaseDate = "1994-09-23",
            voteAverage = 8.7,
            voteCount = 27500,
            genres = listOf("Drama", "Crime"),
            providerIds = listOf(1899),
            popularity = 86.4
        )
    )

    suspend fun getFeatured(): MediaItem = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTrendingMovies(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    val first = res.body()!!.results.first()
                    return@withContext movieDtoToMediaItem(first)
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.first()
    }

    suspend fun getTrendingMovies(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTrendingMovies(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.map { movieDtoToMediaItem(it) }
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.filter { it.mediaType == "movie" }
    }

    suspend fun getTrendingSeries(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTrendingTv(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.map { tvDtoToMediaItem(it) }
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.filter { it.mediaType == "tv" }
    }

    suspend fun getRecentlyReleased(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getNowPlayingMovies(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.map { movieDtoToMediaItem(it) }
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.sortedByDescending { it.releaseDate ?: "" }
    }

    suspend fun getTopRatedMovies(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTopRatedMovies(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.map { movieDtoToMediaItem(it) }
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.filter { it.mediaType == "movie" }.sortedByDescending { it.voteAverage }
    }

    suspend fun getTopRatedSeries(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTopRatedTv(apiKey = tmdbKey)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.map { tvDtoToMediaItem(it) }
                }
            } catch (_: Exception) {}
        }
        curatedCatalog.filter { it.mediaType == "tv" }.sortedByDescending { it.voteAverage }
    }

    suspend fun getPopularThisWeek(): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey

        // Fetch Simkl trending rank (100% free open CDN, no key required!)
        var simklTitleRanks: Map<String, Int> = emptyMap()
        try {
            val simklRes = ApiClient.simklApi.getTrendingMovies()
            if (simklRes.isSuccessful && !simklRes.body().isNullOrEmpty()) {
                simklTitleRanks = simklRes.body()!!
                    .mapIndexed { index, item -> item.title.lowercase().trim() to index }
                    .toMap()
            }
        } catch (_: Exception) {}

        if (tmdbKey.isNotBlank()) {
            try {
                val tmdbRes = ApiClient.tmdbApi.getPopularMovies(apiKey = tmdbKey)
                if (tmdbRes.isSuccessful && !tmdbRes.body()?.results.isNullOrEmpty()) {
                    val movies = tmdbRes.body()!!.results.map { movieDtoToMediaItem(it) }

                    // Weight 70% TMDB + 30% Simkl free trending
                    if (simklTitleRanks.isNotEmpty()) {
                        return@withContext movies.map { m ->
                            val simklRank = simklTitleRanks[m.title.lowercase().trim()]
                            val simklScore = if (simklRank != null) (100 - simklRank).coerceAtLeast(0) * 1.5 else 0.0
                            val combinedScore = (m.popularity * 0.7) + (simklScore * 0.3)
                            m.copy(popularity = combinedScore, simklTrendingRank = simklRank?.plus(1))
                        }.sortedByDescending { it.popularity }
                    }
                    return@withContext movies
                }
            } catch (_: Exception) {}
        }

        // Live fallback: Convert Simkl's top trending movies directly (zero API key required!)
        try {
            val simklRes = ApiClient.simklApi.getTrendingMovies()
            if (simklRes.isSuccessful && !simklRes.body().isNullOrEmpty()) {
                val simklMovies = simklRes.body()!!.take(20).map { simklItemToMediaItem(it, "movie") }
                if (simklMovies.isNotEmpty()) return@withContext simklMovies
            }
        } catch (_: Exception) {}

        curatedCatalog.sortedByDescending { it.popularity }
    }

    suspend fun getSimklTrendingShows(): List<MediaItem> = withContext(Dispatchers.IO) {
        try {
            val simklRes = ApiClient.simklApi.getTrendingShows()
            if (simklRes.isSuccessful && !simklRes.body().isNullOrEmpty()) {
                return@withContext simklRes.body()!!.take(20).map { simklItemToMediaItem(it, "tv") }
            }
        } catch (_: Exception) {}
        curatedCatalog.filter { it.mediaType == "tv" }
    }

    private fun simklItemToMediaItem(dto: com.example.data.api.SimklItemDto, mediaType: String): MediaItem {
        val tmdbId = dto.ids?.tmdb?.toIntOrNull() ?: dto.ids?.simklId ?: dto.title.hashCode()
        val poster = if (!dto.poster.isNullOrBlank()) "https://simkl.in/posters/${dto.poster}_m.jpg" else null
        val fanart = if (!dto.fanart.isNullOrBlank()) "https://simkl.in/fanarts/${dto.fanart}_medium.jpg" else poster
        val rating = dto.ratings?.imdb?.rating ?: dto.ratings?.simkl?.rating ?: 7.5
        val votes = dto.ratings?.imdb?.votes ?: dto.ratings?.simkl?.votes ?: (dto.watched ?: 150)
        return MediaItem(
            id = tmdbId,
            title = dto.title,
            overview = dto.overview ?: "Trending on Simkl",
            posterPath = poster,
            backdropPath = fanart,
            mediaType = mediaType,
            releaseDate = dto.releaseDate,
            voteAverage = rating,
            voteCount = votes,
            genres = dto.genres ?: emptyList(),
            providerIds = emptyList(),
            popularity = ((dto.watched ?: 100) * 2.0) + (100 - (dto.rank ?: 50)).coerceAtLeast(0),
            simklTrendingRank = dto.rank
        )
    }

    suspend fun getProviders(): List<Provider> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getMovieWatchProviders(apiKey = tmdbKey, watchRegion = prefs.watchRegion)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    val list = res.body()!!.results.map {
                        Provider(
                            providerId = it.providerId,
                            providerName = it.providerName,
                            logoPath = it.logoPath,
                            displayPriority = it.displayPriority
                        )
                    }
                    if (list.isNotEmpty()) return@withContext list.take(16)
                }
            } catch (_: Exception) {}
        }
        defaultProviders
    }

    suspend fun getProviderContent(
        providerId: Int,
        mediaType: String, // "movie" or "tv"
        section: String // "new_releases", "top_10", "popular", "best_of_the_best"
    ): List<MediaItem> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val region = prefs.watchRegion
                val sortBy = when (section) {
                    "new_releases" -> if (mediaType == "movie") "primary_release_date.desc" else "first_air_date.desc"
                    "best_of_the_best" -> "vote_average.desc"
                    else -> "popularity.desc"
                }
                val voteCountGte = if (section == "best_of_the_best") 1000 else null

                if (mediaType == "movie") {
                    val res = ApiClient.tmdbApi.discoverMovies(
                        apiKey = tmdbKey,
                        watchProviders = providerId.toString(),
                        watchRegion = region,
                        sortBy = sortBy,
                        voteCountGte = voteCountGte
                    )
                    if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                        val limit = when (section) {
                            "top_10" -> 10
                            "new_releases" -> 30
                            "best_of_the_best" -> 50
                            else -> 100
                        }
                        return@withContext res.body()!!.results.take(limit).map { movieDtoToMediaItem(it) }
                    }
                } else {
                    val res = ApiClient.tmdbApi.discoverTv(
                        apiKey = tmdbKey,
                        watchProviders = providerId.toString(),
                        watchRegion = region,
                        sortBy = sortBy,
                        voteCountGte = voteCountGte
                    )
                    if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                        val limit = when (section) {
                            "top_10" -> 10
                            "new_releases" -> 30
                            "best_of_the_best" -> 50
                            else -> 100
                        }
                        return@withContext res.body()!!.results.take(limit).map { tvDtoToMediaItem(it) }
                    }
                }
            } catch (_: Exception) {}
        }

        // Filter curated fallback
        val filtered = curatedCatalog.filter {
            it.mediaType == mediaType && (it.providerIds.isEmpty() || it.providerIds.contains(providerId))
        }
        val items = if (filtered.isNotEmpty()) filtered else curatedCatalog.filter { it.mediaType == mediaType }
        when (section) {
            "top_10" -> items.take(10)
            "new_releases" -> items.sortedByDescending { it.releaseDate ?: "" }
            "best_of_the_best" -> items.sortedByDescending { it.voteAverage }
            else -> items.sortedByDescending { it.popularity }
        }
    }

    suspend fun getDetails(id: Int, mediaType: String): MediaDetail = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                if (mediaType == "movie") {
                    val res = ApiClient.tmdbApi.getMovieDetails(movieId = id, apiKey = tmdbKey)
                    if (res.isSuccessful && res.body() != null) {
                        val dto = res.body()!!
                        val director = dto.credits?.crew?.firstOrNull { it.job.equals("Director", ignoreCase = true) }?.name
                        val cast = dto.credits?.cast?.take(12)?.map {
                            CastMember(it.id, it.name, it.character.orEmpty(), it.profilePath)
                        }.orEmpty()
                        val trailer = dto.videos?.results?.firstOrNull { it.site.equals("YouTube", ignoreCase = true) && (it.type.equals("Trailer", ignoreCase = true) || it.type.equals("Teaser", ignoreCase = true)) }?.key
                        val similar = dto.similar?.results?.take(10)?.map { movieDtoToMediaItem(it) }.orEmpty()
                        val recs = dto.recommendations?.results?.take(10)?.map { movieDtoToMediaItem(it) }.orEmpty()

                        val region = prefs.watchRegion
                        val provs = dto.watchProviders?.results?.get(region)?.flatrate?.map {
                            Provider(it.providerId, it.providerName, it.logoPath)
                        }.orEmpty()

                        return@withContext MediaDetail(
                            id = dto.id,
                            title = dto.title ?: "Untitled Movie",
                            overview = dto.overview.orEmpty(),
                            posterPath = dto.posterPath,
                            backdropPath = dto.backdropPath,
                            mediaType = "movie",
                            releaseDate = dto.releaseDate,
                            runtime = dto.runtime,
                            voteAverage = dto.voteAverage ?: 0.0,
                            voteCount = dto.voteCount ?: 0,
                            genres = dto.genres?.map { it.name }.orEmpty(),
                            tagline = dto.tagline,
                            director = director,
                            cast = cast,
                            trailerKey = trailer,
                            imdbId = dto.imdbId,
                            seasons = emptyList(),
                            similar = similar,
                            recommendations = recs,
                            watchProviders = provs
                        )
                    }
                } else {
                    val res = ApiClient.tmdbApi.getTvDetails(tvId = id, apiKey = tmdbKey)
                    if (res.isSuccessful && res.body() != null) {
                        val dto = res.body()!!
                        val cast = dto.credits?.cast?.take(12)?.map {
                            CastMember(it.id, it.name, it.character.orEmpty(), it.profilePath)
                        }.orEmpty()
                        val trailer = dto.videos?.results?.firstOrNull { it.site.equals("YouTube", ignoreCase = true) }?.key
                        val similar = dto.similar?.results?.take(10)?.map { tvDtoToMediaItem(it) }.orEmpty()
                        val recs = dto.recommendations?.results?.take(10)?.map { tvDtoToMediaItem(it) }.orEmpty()

                        val seasons = dto.seasons?.filter { it.seasonNumber > 0 }?.map {
                            Season(
                                id = it.id,
                                seasonNumber = it.seasonNumber,
                                name = it.name ?: "Season ${it.seasonNumber}",
                                episodeCount = it.episodeCount ?: 0
                            )
                        }.orEmpty()

                        val region = prefs.watchRegion
                        val provs = dto.watchProviders?.results?.get(region)?.flatrate?.map {
                            Provider(it.providerId, it.providerName, it.logoPath)
                        }.orEmpty()

                        return@withContext MediaDetail(
                            id = dto.id,
                            title = dto.name ?: "Untitled Series",
                            overview = dto.overview.orEmpty(),
                            posterPath = dto.posterPath,
                            backdropPath = dto.backdropPath,
                            mediaType = "tv",
                            releaseDate = dto.firstAirDate,
                            runtime = dto.episodeRunTime?.firstOrNull() ?: 45,
                            voteAverage = dto.voteAverage ?: 0.0,
                            voteCount = dto.voteCount ?: 0,
                            genres = dto.genres?.map { it.name }.orEmpty(),
                            tagline = dto.tagline,
                            director = null,
                            cast = cast,
                            trailerKey = trailer,
                            imdbId = dto.externalIds?.imdbId,
                            seasons = seasons,
                            similar = similar,
                            recommendations = recs,
                            watchProviders = provs
                        )
                    }
                }
            } catch (_: Exception) {}
        }

        // Fallback detail
        val item = curatedCatalog.firstOrNull { it.id == id } ?: curatedCatalog.first()
        val defaultSeasons = if (item.mediaType == "tv") {
            listOf(
                Season(
                    id = 1,
                    seasonNumber = 1,
                    name = "Season 1",
                    episodeCount = 8,
                    episodes = (1..8).map { ep ->
                        Episode(
                            id = ep,
                            seasonNumber = 1,
                            episodeNumber = ep,
                            name = "Episode $ep: The Awakening",
                            overview = "The journey begins as long-buried mysteries begin to unfold across the realms.",
                            stillPath = item.backdropPath,
                            airDate = item.releaseDate,
                            runtime = 52
                        )
                    }
                )
            )
        } else emptyList()

        MediaDetail(
            id = item.id,
            title = item.title,
            overview = item.overview,
            posterPath = item.posterPath,
            backdropPath = item.backdropPath,
            mediaType = item.mediaType,
            releaseDate = item.releaseDate,
            runtime = if (item.mediaType == "movie") 145 else 55,
            voteAverage = item.voteAverage,
            voteCount = item.voteCount,
            genres = item.genres,
            tagline = "Prepare for the ultimate cinematic voyage.",
            director = if (item.mediaType == "movie") "Christopher Nolan" else "Craig Mazin",
            cast = listOf(
                CastMember(1, "Timothée Chalamet", "Paul Atreides", "/1zM47q96nUj0mX21J3fE1uB9P0.jpg"),
                CastMember(2, "Zendaya", "Chani", "/r2GAjd4rEcKy1v2R2Fk2G2mZp7a.jpg"),
                CastMember(3, "Rebecca Ferguson", "Lady Jessica", "/6nRjhP3G4t5R2m5r1F3K9pL2.jpg"),
                CastMember(4, "Javier Bardem", "Stilgar", "/3k2r1J5G6hL9mP2R1F3K9pL2.jpg")
            ),
            trailerKey = "Way9Dexny3w",
            imdbId = "tt15239678",
            seasons = defaultSeasons,
            similar = curatedCatalog.filter { it.id != id }.take(6),
            recommendations = curatedCatalog.filter { it.id != id }.take(6),
            watchProviders = defaultProviders.take(3)
        )
    }

    suspend fun getSeasonEpisodes(tvId: Int, seasonNumber: Int): List<Episode> = withContext(Dispatchers.IO) {
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.getTvSeasonDetails(tvId, seasonNumber, tmdbKey)
                if (res.isSuccessful && !res.body()?.episodes.isNullOrEmpty()) {
                    return@withContext res.body()!!.episodes!!.map {
                        Episode(
                            id = it.id,
                            seasonNumber = it.seasonNumber,
                            episodeNumber = it.episodeNumber,
                            name = it.name ?: "Episode ${it.episodeNumber}",
                            overview = it.overview.orEmpty(),
                            stillPath = it.stillPath,
                            airDate = it.airDate,
                            runtime = it.runtime
                        )
                    }
                }
            } catch (_: Exception) {}
        }

        // Fallback episodes
        (1..8).map { epNum ->
            Episode(
                id = epNum,
                seasonNumber = seasonNumber,
                episodeNumber = epNum,
                name = "Episode $epNum: Odyssey Beyond",
                overview = "High-stakes confrontations emerge as alliances are tested under extreme conditions.",
                stillPath = "/u3YQJILZuNi2bABn9cu9vgxP9Yp.jpg",
                airDate = "2024-01-10",
                runtime = 48
            )
        }
    }

    suspend fun multiSearch(query: String): List<MediaItem> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val tmdbKey = prefs.tmdbApiKey
        if (tmdbKey.isNotBlank()) {
            try {
                val res = ApiClient.tmdbApi.multiSearch(apiKey = tmdbKey, query = query)
                if (res.isSuccessful && !res.body()?.results.isNullOrEmpty()) {
                    return@withContext res.body()!!.results.mapNotNull { item ->
                        when (item.mediaType) {
                            "movie" -> MediaItem(
                                id = item.id,
                                title = item.title ?: "Untitled",
                                overview = item.overview.orEmpty(),
                                posterPath = item.posterPath,
                                backdropPath = item.backdropPath,
                                mediaType = "movie",
                                releaseDate = item.releaseDate,
                                voteAverage = item.voteAverage ?: 0.0,
                                voteCount = item.voteCount ?: 0
                            )
                            "tv" -> MediaItem(
                                id = item.id,
                                title = item.name ?: "Untitled",
                                overview = item.overview.orEmpty(),
                                posterPath = item.posterPath,
                                backdropPath = item.backdropPath,
                                mediaType = "tv",
                                releaseDate = item.firstAirDate,
                                voteAverage = item.voteAverage ?: 0.0,
                                voteCount = item.voteCount ?: 0
                            )
                            else -> null
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        // Fallback search
        curatedCatalog.filter {
            it.title.contains(query, ignoreCase = true) || it.overview.contains(query, ignoreCase = true)
        }
    }

    private fun movieDtoToMediaItem(dto: TmdbMovieDto): MediaItem {
        return MediaItem(
            id = dto.id,
            title = dto.title ?: dto.originalTitle ?: "Untitled",
            overview = dto.overview.orEmpty(),
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            mediaType = "movie",
            releaseDate = dto.releaseDate,
            voteAverage = dto.voteAverage ?: 0.0,
            voteCount = dto.voteCount ?: 0,
            popularity = dto.popularity ?: 0.0
        )
    }

    private fun tvDtoToMediaItem(dto: TmdbTvDto): MediaItem {
        return MediaItem(
            id = dto.id,
            title = dto.name ?: dto.originalName ?: "Untitled",
            overview = dto.overview.orEmpty(),
            posterPath = dto.posterPath,
            backdropPath = dto.backdropPath,
            mediaType = "tv",
            releaseDate = dto.firstAirDate,
            voteAverage = dto.voteAverage ?: 0.0,
            voteCount = dto.voteCount ?: 0,
            popularity = dto.popularity ?: 0.0
        )
    }
}
