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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import com.example.data.model.MediaItem
import com.example.data.model.Provider
import com.example.ui.MainViewModel
import com.example.ui.components.ContentRail
import com.example.ui.components.ContinueWatchingCard
import com.example.ui.components.ProviderPill
import com.example.ui.components.Top10Card
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamGold
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToDetail: (Int, String) -> Unit,
    onNavigateToProvider: (Provider) -> Unit,
    onNavigateToSearch: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val homeState by viewModel.homeState.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val detailState by viewModel.detailState.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(StreamBackground)) {
        if (homeState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StreamPrimary)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().testTag("home_lazy_column"),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Hero Banner
                item {
                    homeState.featured?.let { hero ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(460.dp)
                        ) {
                            AsyncImage(
                                model = hero.backdropUrl,
                                contentDescription = hero.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )

                            // Multi-layer Gradient Fades
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.verticalGradient(
                                            listOf(
                                                Color(0x9907090E),
                                                Color.Transparent,
                                                Color(0xCC07090E),
                                                StreamBackground
                                            )
                                        )
                                    )
                            )

                            // Top Bar Icons (Search, Settings)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .statusBarsPadding()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "STREAMVERSE",
                                    color = StreamPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp
                                )

                                Row {
                                    IconButton(
                                        onClick = onNavigateToSearch,
                                        modifier = Modifier.testTag("home_search_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Search,
                                            contentDescription = "Search",
                                            tint = StreamTextPrimary
                                        )
                                    }
                                    IconButton(
                                        onClick = onNavigateToSettings,
                                        modifier = Modifier.testTag("home_settings_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Settings,
                                            contentDescription = "Settings",
                                            tint = StreamTextPrimary
                                        )
                                    }
                                }
                            }

                            // Hero Content Info
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(horizontal = 20.dp, vertical = 20.dp)
                            ) {
                                if (hero.voteAverage > 0) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Star,
                                            contentDescription = null,
                                            tint = StreamGold,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = String.format("%.1f Rating", hero.voteAverage),
                                            color = StreamGold,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "• ${hero.releaseYear} • ${hero.mediaType.uppercase()}",
                                            color = StreamTextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Text(
                                    text = hero.title,
                                    color = StreamTextPrimary,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = hero.overview,
                                    color = StreamTextSecondary,
                                    fontSize = 13.sp,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // CTAs: WATCH NOW (Instant Stream) & MORE INFO
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.playMedia(
                                                mediaId = hero.id,
                                                mediaType = hero.mediaType,
                                                title = hero.title,
                                                year = hero.releaseYear,
                                                posterPath = hero.posterPath,
                                                backdropPath = hero.backdropPath,
                                                overview = hero.overview,
                                                voteAverage = hero.voteAverage
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StreamPrimary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .height(44.dp)
                                            .testTag("hero_watch_now_button")
                                    ) {
                                        if (detailState.isResolvingPlayback) {
                                            CircularProgressIndicator(
                                                color = Color.White,
                                                modifier = Modifier.size(18.dp),
                                                strokeWidth = 2.dp
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text("Connecting...", color = Color.White, fontWeight = FontWeight.Bold)
                                        } else {
                                            Icon(
                                                Icons.Default.PlayArrow,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "WATCH NOW",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { onNavigateToDetail(hero.id, hero.mediaType) },
                                        shape = RoundedCornerShape(8.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, StreamTextSecondary),
                                        modifier = Modifier.height(44.dp).testTag("hero_details_button")
                                    ) {
                                        Icon(
                                            Icons.Default.Info,
                                            contentDescription = null,
                                            tint = StreamTextPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Details",
                                            color = StreamTextPrimary,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // OTT Providers Rail
                item {
                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
                        Text(
                            text = "Streaming Providers",
                            color = StreamTextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                        )
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(homeState.providers, key = { it.providerId }) { provider ->
                                ProviderPill(
                                    provider = provider,
                                    onClick = {
                                        viewModel.selectProvider(provider)
                                        onNavigateToProvider(provider)
                                    }
                                )
                            }
                        }
                    }
                }

                // Continue Watching Rail (if any)
                if (continueWatching.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                            Text(
                                text = "Continue Watching",
                                color = StreamTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(continueWatching, key = { it.id }) { item ->
                                    ContinueWatchingCard(
                                        entity = item,
                                        onClick = {
                                            viewModel.playMedia(
                                                mediaId = item.mediaId,
                                                mediaType = item.mediaType,
                                                title = item.title,
                                                year = item.releaseYear,
                                                seasonNumber = item.seasonNumber,
                                                episodeNumber = item.episodeNumber,
                                                episodeTitle = item.episodeTitle,
                                                posterPath = item.posterPath,
                                                backdropPath = item.backdropPath,
                                                overview = item.overview,
                                                voteAverage = item.voteAverage
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Top 10 Today Rail (Giant stylized rank numbers)
                if (homeState.top10Today.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                            Text(
                                text = "Top 10 in StreamVerse Today",
                                color = StreamTextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(homeState.top10Today, key = { _, it -> "${it.id}_top10" }) { idx, item ->
                                    Top10Card(
                                        rank = idx + 1,
                                        item = item,
                                        onClick = { onNavigateToDetail(item.id, item.mediaType) }
                                    )
                                }
                            }
                        }
                    }
                }

                // Trending Movies
                item {
                    ContentRail(
                        title = "Trending Movies",
                        items = homeState.trendingMovies,
                        onItemClick = { onNavigateToDetail(it.id, it.mediaType) }
                    )
                }

                // Trending Series
                item {
                    ContentRail(
                        title = "Trending TV Series",
                        items = homeState.trendingSeries,
                        onItemClick = { onNavigateToDetail(it.id, it.mediaType) }
                    )
                }

                // New Releases
                item {
                    ContentRail(
                        title = "New Releases",
                        items = homeState.newReleases,
                        onItemClick = { onNavigateToDetail(it.id, it.mediaType) }
                    )
                }

                // Popular This Week (70% TMDB + 30% Simkl free trending)
                item {
                    ContentRail(
                        title = "Popular This Week",
                        items = homeState.popularThisWeek,
                        onItemClick = { onNavigateToDetail(it.id, it.mediaType) }
                    )
                }

                // Best of the Best (Critically Acclaimed)
                item {
                    ContentRail(
                        title = "Best of the Best",
                        items = homeState.bestOfTheBest,
                        onItemClick = { onNavigateToDetail(it.id, it.mediaType) }
                    )
                }
            }
        }
    }
}
