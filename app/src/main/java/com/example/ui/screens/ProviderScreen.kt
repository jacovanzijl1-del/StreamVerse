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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.MainViewModel
import com.example.ui.components.MediaCard
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSurfaceVariant
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun ProviderScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int, String) -> Unit
) {
    val providerState by viewModel.providerState.collectAsState()
    val provider = providerState.selectedProvider

    val sections = listOf(
        "popular" to "Popular",
        "top_10" to "Top 10",
        "new_releases" to "New Releases",
        "best_of_the_best" to "Best of the Best"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBackground)
            .statusBarsPadding()
            .testTag("provider_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("provider_back_button")
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = StreamTextPrimary
                )
            }

            if (provider != null) {
                if (!provider.logoPath.isNullOrBlank()) {
                    AsyncImage(
                        model = provider.logoUrl,
                        contentDescription = provider.providerName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                }
                Text(
                    text = provider.providerName,
                    color = StreamTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Movies vs Series Tab Bar
        TabRow(
            selectedTabIndex = if (providerState.mediaType == "movie") 0 else 1,
            containerColor = StreamBackground,
            contentColor = StreamPrimary,
            indicator = { tabPositions ->
                val tabIdx = if (providerState.mediaType == "movie") 0 else 1
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[tabIdx]),
                    color = StreamPrimary
                )
            }
        ) {
            Tab(
                selected = providerState.mediaType == "movie",
                onClick = { viewModel.setProviderMediaType("movie") },
                text = {
                    Text(
                        text = "Movies",
                        fontWeight = if (providerState.mediaType == "movie") FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
            Tab(
                selected = providerState.mediaType == "tv",
                onClick = { viewModel.setProviderMediaType("tv") },
                text = {
                    Text(
                        text = "TV Series",
                        fontWeight = if (providerState.mediaType == "tv") FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }

        // Sub-sections Filter Row: New Releases (30), Top 10 (10), Popular (100), Best of the Best (50)
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sections) { (secKey, secLabel) ->
                val isSelected = providerState.section == secKey
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (isSelected) StreamPrimary else StreamSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) StreamPrimary else StreamBorder
                    ),
                    modifier = Modifier.clickable { viewModel.setProviderSection(secKey) }
                ) {
                    Text(
                        text = secLabel,
                        color = if (isSelected) Color.White else StreamTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Grid of Content Items
        if (providerState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StreamPrimary)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(providerState.items, key = { "${it.id}_${it.mediaType}" }) { item ->
                    MediaCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.id, item.mediaType) }
                    )
                }
            }
        }
    }
}
