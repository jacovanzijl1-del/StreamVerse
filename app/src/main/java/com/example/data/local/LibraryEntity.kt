package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MediaItem

@Entity(tableName = "library_items")
data class LibraryMediaEntity(
    @PrimaryKey val id: String,
    val mediaId: Int,
    val mediaType: String,
    val title: String,
    val posterPath: String?,
    val backdropPath: String?,
    val voteAverage: Double,
    val releaseYear: String,
    val overview: String,
    val isFavorite: Boolean = false,
    val isWatchLater: Boolean = false,
    val isFinished: Boolean = false,
    val progressMs: Long = 0L,
    val durationMs: Long = 0L,
    val seasonNumber: Int? = null,
    val episodeNumber: Int? = null,
    val episodeTitle: String? = null,
    val lastWatchedTimestamp: Long = 0L
) {
    fun toMediaItem(): MediaItem {
        return MediaItem(
            id = mediaId,
            title = title,
            overview = overview,
            posterPath = posterPath,
            backdropPath = backdropPath,
            mediaType = mediaType,
            releaseDate = releaseYear,
            voteAverage = voteAverage
        )
    }

    val progressFraction: Float
        get() = if (durationMs > 0L) (progressMs.toFloat() / durationMs.toFloat()).coerceIn(0f, 1f) else 0f
}
