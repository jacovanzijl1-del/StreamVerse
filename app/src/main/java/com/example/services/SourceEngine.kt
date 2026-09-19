package com.example.services

import com.example.data.api.ApiClient
import com.example.data.model.StreamSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URLEncoder

object SourceEngine {

    suspend fun resolveSources(
        title: String,
        year: String?,
        tmdbId: Int?,
        imdbId: String?,
        season: Int? = null,
        episode: Int? = null,
        orionApiKey: String? = null,
        orionAppKey: String? = null
    ): List<StreamSource> = withContext(Dispatchers.IO) {
        val parsedSources = mutableListOf<StreamSource>()

        // 1. Add instant, high-speed active Web Embed servers (Zero API key/setup required)
        if (tmdbId != null && tmdbId > 0) {
            parsedSources.addAll(generateWebEmbedSources(tmdbId, season, episode, title, year))
        }

        // 2. Query Orion for Premium / Cached Debrid streams (if user has API key configured)
        if (!orionApiKey.isNullOrBlank()) {
            try {
                val type = if (season != null && episode != null) "show" else "movie"
                val finalAppKey = if (orionAppKey.isNullOrBlank()) "TESTTESTTESTTESTTESTTESTTESTTEST" else orionAppKey
                val response = ApiClient.orionApi.queryStreams(
                    keyApp = finalAppKey,
                    keyUser = orionApiKey,
                    mode = "stream",
                    action = "retrieve",
                    type = type,
                    query = title,
                    year = year,
                    imdbId = imdbId,
                    tmdbId = tmdbId,
                    season = season,
                    episode = episode,
                    sortValue = "popularity",
                    limitCount = 20,
                    debridLookup = "user",
                    debridResolve = "user",
                    access = null
                )

                if (response.isSuccessful) {
                    val streams = response.body()?.data?.streams.orEmpty()
                    for (stream in streams) {
                        val quality = stream.video?.quality ?: "1080P"
                        val isCached = stream.cached == true || !stream.debrid.isNullOrBlank()
                        val seeds = stream.seeds ?: 0
                        val rawLink = stream.debrid ?: stream.stream?.link ?: stream.stream?.magnet ?: ""

                        // Calculate priority score (Premium Debrid / 4K / Remux gets highest priority)
                        var score = 1000
                        if (isCached) score += 600
                        if (!stream.debrid.isNullOrBlank()) score += 500 // Direct resolved debrid link
                        when {
                            quality.contains("4K", ignoreCase = true) || quality.contains("2160", ignoreCase = true) -> score += 500
                            quality.contains("1080", ignoreCase = true) -> score += 300
                            quality.contains("720", ignoreCase = true) -> score += 100
                        }
                        score += seeds.coerceAtMost(100)

                        val sizeBytes = stream.file?.size ?: 0L
                        val sizeFormatted = formatFileSize(sizeBytes)

                        val streamUrl = if (rawLink.startsWith("http") || rawLink.startsWith("magnet")) rawLink
                        else if (!stream.stream?.link.isNullOrBlank()) stream.stream?.link!!
                        else stream.stream?.magnet ?: ""

                        if (streamUrl.isNotBlank()) {
                            parsedSources.add(
                                StreamSource(
                                    id = stream.id ?: "orion_${parsedSources.size}",
                                    title = stream.file?.name ?: "$title ($year) $quality",
                                    quality = quality.uppercase(),
                                    resolution = "${stream.video?.width ?: 1920}x${stream.video?.height ?: 1080}",
                                    codec = stream.video?.codec ?: "H.264",
                                    audio = stream.audio?.codec ?: "5.1",
                                    sizeBytes = sizeBytes,
                                    sizeFormatted = sizeFormatted,
                                    seeds = seeds,
                                    isCached = isCached,
                                    sourceType = if (!stream.debrid.isNullOrBlank()) "Direct Debrid" else if (isCached) "Cached Debrid" else "Torrent Swarm",
                                    streamUrl = streamUrl,
                                    magnetUrl = stream.stream?.magnet,
                                    torrentHash = stream.file?.hash,
                                    providerName = stream.source ?: "Orion",
                                    priorityScore = score
                                )
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                // Log and gracefully continue
            }
        }

        // 3. Fetch public swarm torrents as supplementary fallback
        try {
            val queryTerm = if (season != null && episode != null) {
                "$title S${season.toString().padStart(2, '0')}E${episode.toString().padStart(2, '0')}"
            } else {
                if (!year.isNullOrBlank()) "$title $year" else title
            }
            parsedSources.addAll(fetchLivePublicTorrents(queryTerm))
        } catch (_: Exception) {}

        // Rank results: Premium Debrid 4K/1080P -> Active Web Embed Servers -> Public Swarms
        parsedSources.sortedByDescending { it.priorityScore }
    }

    private fun generateWebEmbedSources(
        tmdbId: Int,
        season: Int?,
        episode: Int?,
        title: String,
        year: String?
    ): List<StreamSource> {
        val isSeries = season != null && episode != null
        val s = season ?: 1
        val e = episode ?: 1
        val titleClean = if (isSeries) "$title S${s.toString().padStart(2, '0')}E${e.toString().padStart(2, '0')}" else if (!year.isNullOrBlank()) "$title ($year)" else title

        val embedProviders = listOf(
            Triple(
                "Vidzee (Server 1)",
                if (isSeries) "https://player.vidzee.wtf/embed/tv/$tmdbId/$s/$e" else "https://player.vidzee.wtf/embed/movie/$tmdbId",
                "Vidzee"
            ),
            Triple(
                "Willow (Server 2)",
                if (isSeries) "https://willow.arlen.icu/series/$tmdbId/$s/$e" else "https://willow.arlen.icu/movies/$tmdbId",
                "Willow"
            ),
            Triple(
                "Vidrock (Server 3)",
                if (isSeries) "https://vidrock.net/tv/$tmdbId/$s/$e?autoplay=true" else "https://vidrock.net/movie/$tmdbId?autoplay=true",
                "Vidrock"
            ),
            Triple(
                "VidAPI (Server 4)",
                if (isSeries) "https://vidapi.xyz/embed/tv/$tmdbId&s=$s&e=$e" else "https://vidapi.xyz/embed/movie/$tmdbId",
                "VidAPI"
            ),
            Triple(
                "SpencerDevs (Server 5)",
                if (isSeries) "https://spencerdevs.xyz/tv/$tmdbId/$s/$e?autoPlay=true" else "https://spencerdevs.xyz/movie/$tmdbId?autoPlay=true",
                "SpencerDevs"
            ),
            Triple(
                "Videasy (Server 6)",
                if (isSeries) "https://player.videasy.net/tv/$tmdbId/$s/$e" else "https://player.videasy.net/movie/$tmdbId",
                "Videasy"
            ),
            Triple(
                "Vidfast (Server 7)",
                if (isSeries) "https://vidfast.pro/tv/$tmdbId/$s/$e?autoPlay=true" else "https://vidfast.pro/movie/$tmdbId?autoPlay=true",
                "Vidfast"
            ),
            Triple(
                "Vidify (Server 8)",
                if (isSeries) "https://player.vidify.top/embed/tv/$tmdbId/$s/$e?autoplay=true" else "https://player.vidify.top/embed/movie/$tmdbId?autoplay=true",
                "Vidify"
            )
        )

        return embedProviders.mapIndexed { index, (name, url, provider) ->
            StreamSource(
                id = "embed_${provider.lowercase()}_$tmdbId",
                title = "$titleClean - $name",
                quality = "1080P",
                resolution = "1920x1080",
                codec = "H.264 / Multi-Sub",
                audio = "Stereo AAC",
                sizeBytes = 1_500_000_000L,
                sizeFormatted = "Fast Cloud Embed",
                seeds = 999,
                isCached = true,
                sourceType = "Web Embed",
                streamUrl = url,
                providerName = provider,
                priorityScore = 1400 - (index * 15) // High priority, instant out-of-the-box streaming
            )
        }
    }

    private suspend fun fetchLivePublicTorrents(query: String): List<StreamSource> = withContext(Dispatchers.IO) {
        val list = mutableListOf<StreamSource>()
        val client = OkHttpClient.Builder()
            .connectTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(8, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        // 1. SolidTorrents API (Great universal coverage for movies & TV shows)
        try {
            val url = "https://solidtorrents.net/api/v1/search?q=${URLEncoder.encode(query, "UTF-8")}&category=Video&sort=seeders&limit=10"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyStr = response.body?.string()
                    if (!bodyStr.isNullOrBlank()) {
                        val json = JSONObject(bodyStr)
                        val results = json.optJSONArray("results")
                        if (results != null) {
                            for (i in 0 until results.length()) {
                                val item = results.getJSONObject(i)
                                val title = item.optString("title", "Unknown Torrent")
                                val magnet = item.optString("magnet", "")
                                val size = item.optLong("size", 0L)
                                val swarm = item.optJSONObject("swarm")
                                val seeds = swarm?.optInt("seeders", 0) ?: 0

                                if (magnet.isNotBlank()) {
                                    val sizeFormatted = formatFileSize(size)
                                    val priority = 100 + seeds.coerceAtMost(300)
                                    list.add(
                                        StreamSource(
                                            id = "solid_${System.currentTimeMillis()}_$i",
                                            title = title,
                                            quality = detectQuality(title),
                                            resolution = "1920x1080",
                                            codec = "H.264",
                                            audio = "Stereo",
                                            sizeBytes = size,
                                            sizeFormatted = sizeFormatted,
                                            seeds = seeds,
                                            isCached = false,
                                            sourceType = "Torrent Stream",
                                            streamUrl = magnet,
                                            magnetUrl = magnet,
                                            torrentHash = extractHashFromMagnet(magnet),
                                            providerName = "SolidTorrents",
                                            priorityScore = priority
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Suppress and fallback to next provider
        }

        // 2. YTS API (High-quality, verified movie torrents with active seeds)
        try {
            val url = "https://yts.mx/api/v2/list_movies.json?query_term=${URLEncoder.encode(query, "UTF-8")}&limit=8"
            val request = Request.Builder()
                .url(url)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyStr = response.body?.string()
                    if (!bodyStr.isNullOrBlank()) {
                        val json = JSONObject(bodyStr)
                        val data = json.optJSONObject("data")
                        val movies = data?.optJSONArray("movies")
                        if (movies != null) {
                            for (i in 0 until movies.length()) {
                                val movie = movies.getJSONObject(i)
                                val movieTitle = movie.optString("title", "")
                                val movieYear = movie.optInt("year", 0)
                                val torrents = movie.optJSONArray("torrents")
                                if (torrents != null) {
                                    for (j in 0 until torrents.length()) {
                                        val torrent = torrents.getJSONObject(j)
                                        val hash = torrent.optString("hash", "")
                                        val quality = torrent.optString("quality", "1080p")
                                        val seeds = torrent.optInt("seeds", 0)
                                        val sizeBytes = torrent.optLong("size_bytes", 0L)
                                        val type = torrent.optString("type", "bluray")

                                        if (hash.isNotBlank()) {
                                            val magnet = "magnet:?xt=urn:btih:$hash&dn=${URLEncoder.encode(movieTitle, "UTF-8")}&tr=udp://tracker.opentrackr.org:1337/announce&tr=udp://open.demonii.com:1337/announce"
                                            val sizeFormatted = formatFileSize(sizeBytes)
                                            val priority = 150 + seeds.coerceAtMost(350)
                                            list.add(
                                                StreamSource(
                                                    id = "yts_${hash}",
                                                    title = "$movieTitle ($movieYear) [$quality] [$type]",
                                                    quality = quality.uppercase(),
                                                    resolution = if (quality == "2160p") "3840x2160" else if (quality == "1080p") "1920x1080" else "1280x720",
                                                    codec = "H.264",
                                                    audio = "AAC 5.1",
                                                    sizeBytes = sizeBytes,
                                                    sizeFormatted = sizeFormatted,
                                                    seeds = seeds,
                                                    isCached = false,
                                                    sourceType = "Torrent Stream",
                                                    streamUrl = magnet,
                                                    magnetUrl = magnet,
                                                    torrentHash = hash,
                                                    providerName = "YTS-Movie",
                                                    priorityScore = priority
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            // Suppress and exit
        }

        list
    }

    private fun detectQuality(title: String): String {
        return when {
            title.contains("2160") || title.contains("4k", ignoreCase = true) -> "4K"
            title.contains("1080") -> "1080P"
            title.contains("720") -> "720P"
            else -> "1080P"
        }
    }

    private fun extractHashFromMagnet(magnet: String): String? {
        val xtPrefix = "urn:btih:"
        val xtIndex = magnet.indexOf(xtPrefix)
        if (xtIndex != -1) {
            val start = xtIndex + xtPrefix.length
            val end = magnet.indexOf('&', start).let { if (it == -1) magnet.length else it }
            return magnet.substring(start, end).uppercase()
        }
        return null
    }

    fun selectBestSource(sources: List<StreamSource>): StreamSource? {
        return sources.firstOrNull()
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes <= 0) return "1.4 GB"
        val gb = bytes.toDouble() / (1024 * 1024 * 1024)
        return if (gb >= 1.0) String.format("%.2f GB", gb)
        else String.format("%.0f MB", bytes.toDouble() / (1024 * 1024))
    }
}
