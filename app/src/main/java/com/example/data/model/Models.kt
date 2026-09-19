package com.example.data.model

data class MediaItem(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val mediaType: String, // "movie" or "tv"
    val releaseDate: String?,
    val voteAverage: Double,
    val voteCount: Int = 0,
    val genres: List<String> = emptyList(),
    val providerIds: List<Int> = emptyList(),
    val popularity: Double = 0.0,
    val simklTrendingRank: Int? = null
) {
    val releaseYear: String
        get() {
            if (releaseDate.isNullOrBlank()) return ""
            return releaseDate.take(4)
        }

    val posterUrl: String
        get() = if (!posterPath.isNullOrBlank()) {
            if (posterPath.startsWith("http")) posterPath
            else "https://image.tmdb.org/t/p/w500$posterPath"
        } else ""

    val backdropUrl: String
        get() = if (!backdropPath.isNullOrBlank()) {
            if (backdropPath.startsWith("http")) backdropPath
            else "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else posterUrl
}

data class Provider(
    val providerId: Int,
    val providerName: String,
    val logoPath: String?,
    val displayPriority: Int = 0
) {
    val logoUrl: String
        get() = if (!logoPath.isNullOrBlank()) {
            if (logoPath.startsWith("http")) logoPath
            else "https://image.tmdb.org/t/p/w185$logoPath"
        } else ""
}

data class CastMember(
    val id: Int,
    val name: String,
    val character: String,
    val profilePath: String?
) {
    val profileUrl: String
        get() = if (!profilePath.isNullOrBlank()) {
            if (profilePath.startsWith("http")) profilePath
            else "https://image.tmdb.org/t/p/w185$profilePath"
        } else ""
}

data class Episode(
    val id: Int,
    val seasonNumber: Int,
    val episodeNumber: Int,
    val name: String,
    val overview: String,
    val stillPath: String?,
    val airDate: String?,
    val runtime: Int?
) {
    val stillUrl: String
        get() = if (!stillPath.isNullOrBlank()) {
            if (stillPath.startsWith("http")) stillPath
            else "https://image.tmdb.org/t/p/w500$stillPath"
        } else ""
}

data class Season(
    val id: Int,
    val seasonNumber: Int,
    val name: String,
    val episodeCount: Int,
    val episodes: List<Episode> = emptyList()
)

data class MediaDetail(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val mediaType: String,
    val releaseDate: String?,
    val runtime: Int?,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<String>,
    val tagline: String?,
    val director: String?,
    val cast: List<CastMember>,
    val trailerKey: String?,
    val imdbId: String?,
    val seasons: List<Season> = emptyList(),
    val similar: List<MediaItem> = emptyList(),
    val recommendations: List<MediaItem> = emptyList(),
    val watchProviders: List<Provider> = emptyList()
) {
    val releaseYear: String
        get() = if (!releaseDate.isNullOrBlank()) releaseDate.take(4) else ""

    val runtimeFormatted: String
        get() = if (runtime != null && runtime > 0) {
            val hours = runtime / 60
            val minutes = runtime % 60
            if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        } else ""

    val backdropUrl: String
        get() = if (!backdropPath.isNullOrBlank()) {
            if (backdropPath.startsWith("http")) backdropPath
            else "https://image.tmdb.org/t/p/w1280$backdropPath"
        } else if (!posterPath.isNullOrBlank()) {
            if (posterPath.startsWith("http")) posterPath
            else "https://image.tmdb.org/t/p/w780$posterPath"
        } else ""
}

data class StreamSource(
    val id: String,
    val title: String,
    val quality: String, // "4K", "1080P", "720P"
    val resolution: String,
    val codec: String,
    val audio: String,
    val sizeBytes: Long,
    val sizeFormatted: String,
    val seeds: Int,
    val isCached: Boolean,
    val sourceType: String, // "Cached Debrid", "Direct Hoster", "Torrent Stream"
    val streamUrl: String,
    val magnetUrl: String? = null,
    val torrentHash: String? = null,
    val providerName: String = "Orion",
    val priorityScore: Int = 0
)
