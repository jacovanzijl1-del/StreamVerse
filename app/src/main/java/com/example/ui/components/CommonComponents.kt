package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.local.LibraryMediaEntity
import com.example.data.model.MediaItem
import com.example.data.model.Provider
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamGold
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSecondary
import com.example.ui.theme.StreamSurfaceVariant
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun MediaCard(
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(135.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("media_card_${item.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(195.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StreamCard)
        ) {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Rating Pill
            if (item.voteAverage > 0) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xCC07090E),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = StreamGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", item.voteAverage),
                            color = StreamTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quality / Media Type badge
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = StreamPrimary.copy(alpha = 0.85f),
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp)
            ) {
                Text(
                    text = item.mediaType.uppercase(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = item.title,
            color = StreamTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Text(
            text = item.releaseYear,
            color = StreamTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun Top10Card(
    rank: Int,
    item: MediaItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .width(180.dp)
            .height(210.dp)
            .clickable { onClick() }
            .testTag("top10_card_${rank}")
    ) {
        // Stylized Giant Rank Number Behind
        Text(
            text = "$rank",
            style = TextStyle(
                fontSize = 120.sp,
                fontWeight = FontWeight.Black,
                color = StreamBorder.copy(alpha = 0.6f)
            ),
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-10).dp, y = 15.dp)
        )

        // Poster shifted to the right
        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .width(125.dp)
                .height(185.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StreamCard)
                .shadow(8.dp)
        ) {
            AsyncImage(
                model = item.posterUrl,
                contentDescription = item.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Surface(
                shape = RoundedCornerShape(4.dp),
                color = StreamPrimary,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(4.dp)
            ) {
                Text(
                    text = "TOP 10",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ContinueWatchingCard(
    entity: LibraryMediaEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(220.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .testTag("continue_card_${entity.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(125.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StreamCard)
        ) {
            val bgUrl = if (!entity.backdropPath.isNullOrBlank()) {
                if (entity.backdropPath.startsWith("http")) entity.backdropPath
                else "https://image.tmdb.org/t/p/w780${entity.backdropPath}"
            } else if (!entity.posterPath.isNullOrBlank()) {
                if (entity.posterPath.startsWith("http")) entity.posterPath
                else "https://image.tmdb.org/t/p/w500${entity.posterPath}"
            } else ""

            AsyncImage(
                model = bgUrl,
                contentDescription = entity.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Play Overlay Circle
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(42.dp)
                    .background(Color(0x99000000), CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.8f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Resume",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Progress bar at bottom
            LinearProgressIndicator(
                progress = { entity.progressFraction },
                color = StreamPrimary,
                trackColor = Color.White.copy(alpha = 0.3f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .align(Alignment.BottomCenter)
            )
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

        val epInfo = if (entity.seasonNumber != null && entity.episodeNumber != null) {
            "S${entity.seasonNumber}:E${entity.episodeNumber}"
        } else {
            entity.releaseYear
        }
        Text(
            text = epInfo,
            color = StreamTextSecondary,
            fontSize = 11.sp
        )
    }
}

@Composable
fun ProviderPill(
    provider: Provider,
    isSelected: Boolean = false,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) StreamPrimary.copy(alpha = 0.2f) else StreamSurfaceVariant,
        border = androidx.compose.foundation.BorderStroke(
            1.5.dp,
            if (isSelected) StreamPrimary else StreamBorder
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .testTag("provider_${provider.providerId}")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            if (!provider.logoPath.isNullOrBlank()) {
                AsyncImage(
                    model = provider.logoUrl,
                    contentDescription = provider.providerName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(26.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = provider.providerName,
                color = if (isSelected) StreamPrimary else StreamTextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ContentRail(
    title: String,
    items: List<MediaItem>,
    onItemClick: (MediaItem) -> Unit,
    modifier: Modifier = Modifier
) {
    if (items.isEmpty()) return

    Column(modifier = modifier.fillMaxWidth().padding(vertical = 10.dp)) {
        Text(
            text = title,
            color = StreamTextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items, key = { "${it.id}_${it.mediaType}" }) { item ->
                MediaCard(item = item, onClick = { onItemClick(item) })
            }
        }
    }
}
