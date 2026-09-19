package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Episode
import com.example.ui.MainViewModel
import com.example.ui.components.ContentRail
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamGold
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSurfaceVariant
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun DetailScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMedia: (Int, String) -> Unit
) {
    val detailState by viewModel.detailState.collectAsState()
    val detail = detailState.detail

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBackground)
            .testTag("detail_screen")
    ) {
        if (detailState.isLoading || detail == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StreamPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Large Backdrop Header
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp)
                    ) {
                        AsyncImage(
                            model = detail.backdropUrl,
                            contentDescription = detail.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Vignette Gradients
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color(0xB307090E),
                                            Color.Transparent,
                                            Color(0xD907090E),
                                            StreamBackground
                                        )
                                    )
                                )
                        )

                        // Back button
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .statusBarsPadding()
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                                .testTag("detail_back_button")
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Back",
                                tint = StreamTextPrimary
                            )
                        }

                        // Hero Meta Block
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(horizontal = 20.dp, vertical = 16.dp)
                        ) {
                            Text(
                                text = detail.title,
                                color = StreamTextPrimary,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (detail.voteAverage > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = StreamGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            text = String.format("%.1f", detail.voteAverage),
                                            color = StreamGold,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Text(
                                    text = detail.releaseYear,
                                    color = StreamTextSecondary,
                                    fontSize = 13.sp
                                )

                                if (detail.runtimeFormatted.isNotBlank()) {
                                    Text(
                                        text = "• ${detail.runtimeFormatted}",
                                        color = StreamTextSecondary,
                                        fontSize = 13.sp
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = StreamPrimary.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, StreamPrimary)
                                ) {
                                    Text(
                                        text = "4K HDR",
                                        color = StreamPrimary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // CTA Buttons: WATCH NOW (Stream immediately) & Favorites / Watchlist
                item {
                    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Button(
                                onClick = {
                                    viewModel.playMedia(
                                        mediaId = detail.id,
                                        mediaType = detail.mediaType,
                                        title = detail.title,
                                        year = detail.releaseYear,
                                        imdbId = detail.imdbId,
                                        posterPath = detail.posterPath,
                                        backdropPath = detail.backdropPath,
                                        overview = detail.overview,
                                        voteAverage = detail.voteAverage
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = StreamPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("detail_watch_now_button")
                            ) {
                                if (detailState.isResolvingPlayback) {
                                    CircularProgressIndicator(
                                        color = Color.White,
                                        modifier = Modifier.size(18.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Resolving Streams...", color = Color.White, fontWeight = FontWeight.Bold)
                                } else {
                                    Icon(
                                        Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "WATCH NOW",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Favorite Icon Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StreamCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StreamBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.toggleFavorite() }
                                    .testTag("favorite_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (detailState.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                        contentDescription = "Favorite",
                                        tint = if (detailState.isFavorite) StreamPrimary else StreamTextPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }

                            // Watch Later Icon Button
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = StreamCard,
                                border = androidx.compose.foundation.BorderStroke(1.dp, StreamBorder),
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { viewModel.toggleWatchLater() }
                                    .testTag("watchlist_button")
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (detailState.isWatchLater) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Watchlist",
                                        tint = if (detailState.isWatchLater) StreamGold else StreamTextPrimary,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                            }
                        }

                        // Genres Pills
                        if (detail.genres.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(detail.genres) { genre ->
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = StreamSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StreamBorder)
                                    ) {
                                        Text(
                                            text = genre,
                                            color = StreamTextSecondary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Tagline & Overview
                        if (!detail.tagline.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "“${detail.tagline}”",
                                color = StreamTextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = detail.overview,
                            color = StreamTextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 21.sp
                        )

                        if (!detail.director.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Director: ${detail.director}",
                                color = StreamTextSecondary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                // TV Series Seasons & Episodes Section
                if (detail.mediaType == "tv" && detail.seasons.isNotEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp)
                        ) {
                            Text(
                                text = "Episodes",
                                color = StreamTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )

                            // Season Pills Selector
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(detail.seasons) { s ->
                                    val isSelected = s.seasonNumber == detailState.selectedSeasonNumber
                                    Surface(
                                        shape = RoundedCornerShape(16.dp),
                                        color = if (isSelected) StreamPrimary else StreamSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            if (isSelected) StreamPrimary else StreamBorder
                                        ),
                                        modifier = Modifier.clickable { viewModel.selectSeason(s.seasonNumber) }
                                    ) {
                                        Text(
                                            text = s.name,
                                            color = if (isSelected) Color.White else StreamTextSecondary,
                                            fontSize = 13.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            // Episode Cards
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 10.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                detailState.episodes.forEach { ep ->
                                    EpisodeRowItem(
                                        episode = ep,
                                        onPlay = {
                                            viewModel.playMedia(
                                                mediaId = detail.id,
                                                mediaType = "tv",
                                                title = detail.title,
                                                year = detail.releaseYear,
                                                imdbId = detail.imdbId,
                                                seasonNumber = ep.seasonNumber,
                                                episodeNumber = ep.episodeNumber,
                                                episodeTitle = ep.name,
                                                posterPath = detail.posterPath,
                                                backdropPath = ep.stillPath ?: detail.backdropPath,
                                                overview = ep.overview
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Cast Members Carousel
                if (detail.cast.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                            Text(
                                text = "Cast & Crew",
                                color = StreamTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)
                            )

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                items(detail.cast) { cast ->
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.width(85.dp)
                                    ) {
                                        AsyncImage(
                                            model = cast.profileUrl,
                                            contentDescription = cast.name,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .size(72.dp)
                                                .clip(CircleShape)
                                                .background(StreamCard)
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = cast.name,
                                            color = StreamTextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = cast.character,
                                            color = StreamTextSecondary,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Similar & Recommendations
                if (detail.similar.isNotEmpty()) {
                    item {
                        ContentRail(
                            title = "Similar Titles",
                            items = detail.similar,
                            onItemClick = { onNavigateToMedia(it.id, it.mediaType) }
                        )
                    }
                }

                if (detail.recommendations.isNotEmpty()) {
                    item {
                        ContentRail(
                            title = "Recommended For You",
                            items = detail.recommendations,
                            onItemClick = { onNavigateToMedia(it.id, it.mediaType) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EpisodeRowItem(
    episode: Episode,
    onPlay: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = StreamCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, StreamBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlay() }
            .testTag("episode_${episode.episodeNumber}")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Still Image with Play Icon
            Box(
                modifier = Modifier
                    .width(115.dp)
                    .height(68.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(StreamSurfaceVariant)
            ) {
                AsyncImage(
                    model = episode.stillUrl,
                    contentDescription = episode.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(30.dp)
                        .background(Color(0x99000000), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${episode.episodeNumber}. ${episode.name}",
                    color = StreamTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (episode.runtime != null && episode.runtime > 0) {
                    Text(
                        text = "${episode.runtime}m",
                        color = StreamTextSecondary,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = episode.overview,
                    color = StreamTextMuted,
                    fontSize = 12.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
