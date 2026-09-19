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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.MediaCard
import com.example.ui.theme.StreamBackground
import com.example.ui.theme.StreamBorder
import com.example.ui.theme.StreamCard
import com.example.ui.theme.StreamPrimary
import com.example.ui.theme.StreamSurfaceVariant
import com.example.ui.theme.StreamTextMuted
import com.example.ui.theme.StreamTextPrimary
import com.example.ui.theme.StreamTextSecondary

@Composable
fun SearchScreen(
    viewModel: MainViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Int, String) -> Unit
) {
    val searchState by viewModel.searchState.collectAsState()
    var selectedChip by remember { mutableStateOf("All") }
    val chips = listOf("All", "Movies", "TV Series", "Action", "Sci-Fi", "Drama", "Animation")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StreamBackground)
            .statusBarsPadding()
            .testTag("search_screen")
    ) {
        // Search Bar Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("search_back_button")
            ) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = StreamTextPrimary
                )
            }

            OutlinedTextField(
                value = searchState.query,
                onValueChange = { viewModel.search(it) },
                placeholder = { Text("Search movies, series, stars...", color = StreamTextMuted) },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = StreamTextSecondary
                    )
                },
                trailingIcon = {
                    if (searchState.query.isNotBlank()) {
                        IconButton(onClick = { viewModel.search("") }) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = StreamTextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StreamPrimary,
                    unfocusedBorderColor = StreamBorder,
                    focusedContainerColor = StreamCard,
                    unfocusedContainerColor = StreamCard,
                    focusedTextColor = StreamTextPrimary,
                    unfocusedTextColor = StreamTextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .testTag("search_input_field")
            )
        }

        // Quick Category Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chips) { chip ->
                val isSelected = selectedChip == chip
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (isSelected) StreamPrimary else StreamSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isSelected) StreamPrimary else StreamBorder
                    ),
                    modifier = Modifier.clickable {
                        selectedChip = chip
                        if (chip != "All") {
                            viewModel.search(chip)
                        }
                    }
                ) {
                    Text(
                        text = chip,
                        color = if (isSelected) Color.White else StreamTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Results Grid
        if (searchState.isSearching) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = StreamPrimary)
            }
        } else if (searchState.results.isEmpty() && searchState.query.isNotBlank()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "No results found for \"${searchState.query}\"",
                        color = StreamTextSecondary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.padding(top = 4.dp))
                    Text(
                        text = "Try searching for a title, actor, or genre",
                        color = StreamTextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            val displayResults = if (searchState.query.isBlank()) {
                // Show default popular titles
                viewModel.homeState.value.popularThisWeek
            } else {
                searchState.results
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 130.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp, top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayResults, key = { "${it.id}_${it.mediaType}" }) { item ->
                    MediaCard(
                        item = item,
                        onClick = { onNavigateToDetail(item.id, item.mediaType) }
                    )
                }
            }
        }
    }
}
