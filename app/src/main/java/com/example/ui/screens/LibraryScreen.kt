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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.LibraryMediaEntity
import com.example.ui.MainViewModel
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSurfaceVariant
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun LibraryScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (Int, String) -> Unit
) {
    val continueWatching by viewModel.continueWatching.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val watchLater by viewModel.watchLater.collectAsState()
    val watchHistory by viewModel.watchHistory.collectAsState()

    var selectedTabIdx by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Continue Watching (${continueWatching.size})",
        "Favorites (${favorites.size})",
        "Watch Later (${watchLater.size})",
        "History (${watchHistory.size})"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBackground)
            .statusBarsPadding()
            .testTag("library_screen")
    ) {
        // Screen Title
        Text(
            text = "My Library",
            color = StreamTextPrimary,
            fontSize = 26.sp,
            fontWeight = FontWeight.Black,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )

        // Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTabIdx,
            containerColor = StreamBackground,
            contentColor = StreamPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIdx]),
                    color = StreamPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, label ->
                Tab(
                    selected = selectedTabIdx == index,
                    onClick = { selectedTabIdx = index },
                    text = {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIdx == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        val currentList: List<LibraryMediaEntity> = when (selectedTabIdx) {
            0 -> continueWatching
            1 -> favorites
            2 -> watchLater
            else -> watchHistory
        }

        if (currentList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val icon = when (selectedTabIdx) {
                        0 -> Icons.Default.PlayCircleOutline
                        1 -> Icons.Default.Favorite
                        2 -> Icons.Default.Bookmark
                        else -> Icons.Default.History
                    }
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = StreamTextMuted,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No titles saved yet",
                        color = StreamTextSecondary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Browse titles and click Watch Now or Save to Library",
                        color = StreamTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(currentList, key = { it.id }) { entity ->
                    LibraryItemCard(
                        entity = entity,
                        onClick = { onNavigateToDetail(entity.mediaId, entity.mediaType) },
                        onPlay = {
                            viewModel.playMedia(
                                mediaId = entity.mediaId,
                                mediaType = entity.mediaType,
                                title = entity.title,
                                year = entity.releaseYear,
                                seasonNumber = entity.seasonNumber,
                                episodeNumber = entity.episodeNumber,
                                episodeTitle = entity.episodeTitle,
                                posterPath = entity.posterPath,
                                backdropPath = entity.backdropPath,
                                overview = entity.overview,
                                voteAverage = entity.voteAverage
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LibraryItemCard(
    entity: LibraryMediaEntity,
    onClick: () -> Unit,
    onPlay: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StreamCard)
        ) {
            val imgUrl = if (!entity.posterPath.isNullOrBlank()) {
                if (entity.posterPath.startsWith("http")) entity.posterPath
                else "https://image.tmdb.org/t/p/w500${entity.posterPath}"
            } else if (!entity.backdropPath.isNullOrBlank()) {
                if (entity.backdropPath.startsWith("http")) entity.backdropPath
                else "https://image.tmdb.org/t/p/w780${entity.backdropPath}"
            } else ""

            AsyncImage(
                model = imgUrl,
                contentDescription = entity.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Progress bar if in progress
            if (entity.progressMs > 0) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { entity.progressFraction },
                    color = StreamPrimary,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .align(Alignment.BottomCenter)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = entity.title,
            color = StreamTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        val meta = if (entity.seasonNumber != null && entity.episodeNumber != null) {
            "S${entity.seasonNumber}:E${entity.episodeNumber}"
        } else {
            "${entity.releaseYear} • ${entity.mediaType.uppercase()}"
        }
        Text(
            text = meta,
            color = StreamTextSecondary,
            fontSize = 11.sp
        )
    }
}
